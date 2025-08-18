package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Enhanced Database Connection Pool for better performance and resource management
 * Implements connection pooling with automatic connection validation and cleanup
 */
public class DatabaseConnectionPool {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionPool.class.getName());
    
    // Pool configuration
    private static final int MIN_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 20;
    private static final int CONNECTION_TIMEOUT = 30; // seconds
    private static final int VALIDATION_TIMEOUT = 5; // seconds
    private static final long CONNECTION_MAX_AGE = 30 * 60 * 1000; // 30 minutes
    
    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/bookshop";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root@1234";
    
    // Pool management
    private final BlockingQueue<PooledConnection> availableConnections;
    private final AtomicInteger totalConnections;
    private final AtomicInteger activeConnections;
    private volatile boolean isShutdown;
    
    // Singleton instance
    private static volatile DatabaseConnectionPool instance;
    
    /**
     * Wrapper class for pooled connections to track usage
     */
    private static class PooledConnection {
        private final Connection connection;
        private final long createdTime;
        private volatile long lastUsedTime;
        private volatile boolean isValid;
        
        public PooledConnection(Connection connection) {
            this.connection = connection;
            this.createdTime = System.currentTimeMillis();
            this.lastUsedTime = createdTime;
            this.isValid = true;
        }
        
        public Connection getConnection() { return connection; }
        public long getCreatedTime() { return createdTime; }
        public long getLastUsedTime() { return lastUsedTime; }
        public void setLastUsedTime(long time) { this.lastUsedTime = time; }
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { this.isValid = valid; }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - createdTime > CONNECTION_MAX_AGE;
        }
    }
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private DatabaseConnectionPool() {
        this.availableConnections = new LinkedBlockingQueue<>();
        this.totalConnections = new AtomicInteger(0);
        this.activeConnections = new AtomicInteger(0);
        this.isShutdown = false;
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Initialize minimum pool size
            initializePool();
            
            // Start cleanup thread
            startCleanupThread();
            
            LOGGER.info("Database connection pool initialized with " + MIN_POOL_SIZE + " connections");
            
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC driver not found", e);
            throw new RuntimeException("MySQL JDBC driver not found", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }
    
    /**
     * Get singleton instance with thread-safe lazy initialization
     */
    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionPool();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get a connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (isShutdown) {
            throw new SQLException("Connection pool is shutdown");
        }
        
        PooledConnection pooledConnection = null;
        
        try {
            // Try to get a connection from the pool with timeout
            pooledConnection = availableConnections.poll(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            
            if (pooledConnection == null) {
                // If no connection available and can create more, create new one
                if (totalConnections.get() < MAX_POOL_SIZE) {
                    pooledConnection = createNewConnection();
                } else {
                    throw new SQLException("Connection pool exhausted. Maximum pool size reached.");
                }
            }
            
            // Validate connection before returning
            if (!isConnectionValid(pooledConnection)) {
                closeConnection(pooledConnection);
                totalConnections.decrementAndGet();
                return getConnection(); // Recursively try to get another connection
            }
            
            pooledConnection.setLastUsedTime(System.currentTimeMillis());
            activeConnections.incrementAndGet();
            
            return new ConnectionWrapper(pooledConnection.getConnection(), this);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }
    
    /**
     * Return a connection to the pool
     */
    protected void returnConnection(Connection connection) {
        if (isShutdown || connection == null) {
            return;
        }
        
        try {
            // Unwrap to get the original connection
            Connection originalConnection = connection;
            if (connection instanceof ConnectionWrapper) {
                originalConnection = ((ConnectionWrapper) connection).getWrappedConnection();
            }
            
            // Create pooled connection and add back to pool
            PooledConnection pooledConnection = new PooledConnection(originalConnection);
            
            // Validate before returning to pool
            if (isConnectionValid(pooledConnection) && !pooledConnection.isExpired()) {
                availableConnections.offer(pooledConnection);
            } else {
                closeConnection(pooledConnection);
                totalConnections.decrementAndGet();
            }
            
            activeConnections.decrementAndGet();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error returning connection to pool", e);
        }
    }
    
    /**
     * Initialize the connection pool with minimum connections
     */
    private void initializePool() throws SQLException {
        for (int i = 0; i < MIN_POOL_SIZE; i++) {
            PooledConnection pooledConnection = createNewConnection();
            availableConnections.offer(pooledConnection);
        }
    }
    
    /**
     * Create a new database connection
     */
    private PooledConnection createNewConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);
        props.setProperty("useSSL", "false");
        props.setProperty("allowPublicKeyRetrieval", "true");
        props.setProperty("autoReconnect", "true");
        props.setProperty("useUnicode", "true");
        props.setProperty("characterEncoding", "UTF-8");
        
        Connection connection = DriverManager.getConnection(URL, props);
        connection.setAutoCommit(true);
        
        totalConnections.incrementAndGet();
        
        LOGGER.fine("Created new database connection. Total connections: " + totalConnections.get());
        
        return new PooledConnection(connection);
    }
    
    /**
     * Validate if connection is still usable
     */
    private boolean isConnectionValid(PooledConnection pooledConnection) {
        if (pooledConnection == null || !pooledConnection.isValid()) {
            return false;
        }
        
        try {
            Connection connection = pooledConnection.getConnection();
            return connection != null && 
                   !connection.isClosed() && 
                   connection.isValid(VALIDATION_TIMEOUT);
        } catch (SQLException e) {
            LOGGER.log(Level.FINE, "Connection validation failed", e);
            return false;
        }
    }
    
    /**
     * Close a pooled connection
     */
    private void closeConnection(PooledConnection pooledConnection) {
        if (pooledConnection != null && pooledConnection.getConnection() != null) {
            try {
                pooledConnection.getConnection().close();
                pooledConnection.setValid(false);
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Error closing connection", e);
            }
        }
    }
    
    /**
     * Start background cleanup thread
     */
    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (!isShutdown) {
                try {
                    Thread.sleep(60000); // Run every minute
                    cleanupExpiredConnections();
                    ensureMinimumConnections();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error in cleanup thread", e);
                }
            }
        });
        
        cleanupThread.setName("DatabaseConnectionPool-Cleanup");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
        
        LOGGER.fine("Started database connection pool cleanup thread");
    }
    
    /**
     * Clean up expired connections
     */
    private void cleanupExpiredConnections() {
        int cleanedUp = 0;
        
        // Create a temporary list to hold connections to be returned
        var connectionsToReturn = new java.util.ArrayList<PooledConnection>();
        
        // Drain available connections to check for expired ones
        while (!availableConnections.isEmpty()) {
            PooledConnection pooledConnection = availableConnections.poll();
            if (pooledConnection != null) {
                if (pooledConnection.isExpired() || !isConnectionValid(pooledConnection)) {
                    closeConnection(pooledConnection);
                    totalConnections.decrementAndGet();
                    cleanedUp++;
                } else {
                    connectionsToReturn.add(pooledConnection);
                }
            }
        }
        
        // Return valid connections back to the pool
        for (PooledConnection conn : connectionsToReturn) {
            availableConnections.offer(conn);
        }
        
        if (cleanedUp > 0) {
            LOGGER.fine("Cleaned up " + cleanedUp + " expired connections");
        }
    }
    
    /**
     * Ensure minimum number of connections in the pool
     */
    private void ensureMinimumConnections() {
        while (availableConnections.size() < MIN_POOL_SIZE && totalConnections.get() < MAX_POOL_SIZE) {
            try {
                PooledConnection pooledConnection = createNewConnection();
                availableConnections.offer(pooledConnection);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to create minimum connections", e);
                break;
            }
        }
    }
    
    /**
     * Get pool statistics
     */
    public PoolStats getPoolStats() {
        return new PoolStats(
            totalConnections.get(),
            activeConnections.get(),
            availableConnections.size(),
            MAX_POOL_SIZE
        );
    }
    
    /**
     * Shutdown the connection pool
     */
    public synchronized void shutdown() {
        if (isShutdown) {
            return;
        }
        
        isShutdown = true;
        
        LOGGER.info("Shutting down database connection pool...");
        
        // Close all available connections
        while (!availableConnections.isEmpty()) {
            PooledConnection pooledConnection = availableConnections.poll();
            if (pooledConnection != null) {
                closeConnection(pooledConnection);
            }
        }
        
        totalConnections.set(0);
        activeConnections.set(0);
        
        LOGGER.info("Database connection pool shutdown completed");
    }
    
    /**
     * Pool statistics class
     */
    public static class PoolStats {
        private final int totalConnections;
        private final int activeConnections;
        private final int availableConnections;
        private final int maxPoolSize;
        
        public PoolStats(int totalConnections, int activeConnections, int availableConnections, int maxPoolSize) {
            this.totalConnections = totalConnections;
            this.activeConnections = activeConnections;
            this.availableConnections = availableConnections;
            this.maxPoolSize = maxPoolSize;
        }
        
        public int getTotalConnections() { return totalConnections; }
        public int getActiveConnections() { return activeConnections; }
        public int getAvailableConnections() { return availableConnections; }
        public int getMaxPoolSize() { return maxPoolSize; }
        
        @Override
        public String toString() {
            return String.format("PoolStats{total=%d, active=%d, available=%d, max=%d}", 
                totalConnections, activeConnections, availableConnections, maxPoolSize);
        }
    }
    
    /**
     * Connection wrapper that automatically returns connection to pool when closed
     */
    private static class ConnectionWrapper implements Connection {
        private final Connection wrappedConnection;
        private final DatabaseConnectionPool pool;
        private boolean isClosed = false;
        
        public ConnectionWrapper(Connection connection, DatabaseConnectionPool pool) {
            this.wrappedConnection = connection;
            this.pool = pool;
        }
        
        public Connection getWrappedConnection() {
            return wrappedConnection;
        }
        
        @Override
        public void close() throws SQLException {
            if (!isClosed) {
                isClosed = true;
                pool.returnConnection(this);
            }
        }
        
        @Override
        public boolean isClosed() throws SQLException {
            return isClosed || wrappedConnection.isClosed();
        }
        
        // Delegate all other methods to the wrapped connection
        @Override
        public java.sql.Statement createStatement() throws SQLException {
            return wrappedConnection.createStatement();
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            return wrappedConnection.prepareStatement(sql);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            return wrappedConnection.prepareCall(sql);
        }
        
        @Override
        public String nativeSQL(String sql) throws SQLException {
            return wrappedConnection.nativeSQL(sql);
        }
        
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            wrappedConnection.setAutoCommit(autoCommit);
        }
        
        @Override
        public boolean getAutoCommit() throws SQLException {
            return wrappedConnection.getAutoCommit();
        }
        
        @Override
        public void commit() throws SQLException {
            wrappedConnection.commit();
        }
        
        @Override
        public void rollback() throws SQLException {
            wrappedConnection.rollback();
        }
        
        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            return wrappedConnection.getMetaData();
        }
        
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            wrappedConnection.setReadOnly(readOnly);
        }
        
        @Override
        public boolean isReadOnly() throws SQLException {
            return wrappedConnection.isReadOnly();
        }
        
        @Override
        public void setCatalog(String catalog) throws SQLException {
            wrappedConnection.setCatalog(catalog);
        }
        
        @Override
        public String getCatalog() throws SQLException {
            return wrappedConnection.getCatalog();
        }
        
        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            wrappedConnection.setTransactionIsolation(level);
        }
        
        @Override
        public int getTransactionIsolation() throws SQLException {
            return wrappedConnection.getTransactionIsolation();
        }
        
        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            return wrappedConnection.getWarnings();
        }
        
        @Override
        public void clearWarnings() throws SQLException {
            wrappedConnection.clearWarnings();
        }
        
        // Additional Connection interface methods would be implemented here...
        // For brevity, implementing key methods. In production, all methods should be implemented.
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }
        
        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
            return wrappedConnection.getTypeMap();
        }
        
        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
            wrappedConnection.setTypeMap(map);
        }
        
        @Override
        public void setHoldability(int holdability) throws SQLException {
            wrappedConnection.setHoldability(holdability);
        }
        
        @Override
        public int getHoldability() throws SQLException {
            return wrappedConnection.getHoldability();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            return wrappedConnection.setSavepoint();
        }
        
        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            return wrappedConnection.setSavepoint(name);
        }
        
        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            wrappedConnection.rollback(savepoint);
        }
        
        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            wrappedConnection.releaseSavepoint(savepoint);
        }
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return wrappedConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return wrappedConnection.prepareStatement(sql, autoGeneratedKeys);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return wrappedConnection.prepareStatement(sql, columnIndexes);
        }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return wrappedConnection.prepareStatement(sql, columnNames);
        }
        
        @Override
        public java.sql.Clob createClob() throws SQLException {
            return wrappedConnection.createClob();
        }
        
        @Override
        public java.sql.Blob createBlob() throws SQLException {
            return wrappedConnection.createBlob();
        }
        
        @Override
        public java.sql.NClob createNClob() throws SQLException {
            return wrappedConnection.createNClob();
        }
        
        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            return wrappedConnection.createSQLXML();
        }
        
        @Override
        public boolean isValid(int timeout) throws SQLException {
            return wrappedConnection.isValid(timeout);
        }
        
        @Override
        public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
            wrappedConnection.setClientInfo(name, value);
        }
        
        @Override
        public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
            wrappedConnection.setClientInfo(properties);
        }
        
        @Override
        public String getClientInfo(String name) throws SQLException {
            return wrappedConnection.getClientInfo(name);
        }
        
        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            return wrappedConnection.getClientInfo();
        }
        
        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return wrappedConnection.createArrayOf(typeName, elements);
        }
        
        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return wrappedConnection.createStruct(typeName, attributes);
        }
        
        @Override
        public void setSchema(String schema) throws SQLException {
            wrappedConnection.setSchema(schema);
        }
        
        @Override
        public String getSchema() throws SQLException {
            return wrappedConnection.getSchema();
        }
        
        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            wrappedConnection.abort(executor);
        }
        
        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
            wrappedConnection.setNetworkTimeout(executor, milliseconds);
        }
        
        @Override
        public int getNetworkTimeout() throws SQLException {
            return wrappedConnection.getNetworkTimeout();
        }
        
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return wrappedConnection.unwrap(iface);
        }
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return wrappedConnection.isWrapperFor(iface);
        }
    }
}
