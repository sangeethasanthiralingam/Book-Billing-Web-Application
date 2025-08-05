package dao;

import model.Bill;
import model.BillItem;
import model.User;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BillDAO {
    private DBConnection dbConnection;
    
    public BillDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    public boolean saveBill(Bill bill) {
        String query = "INSERT INTO bills (bill_number, bill_date, customer_id, cashier_id, subtotal, discount, tax, total, payment_method, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, bill.getBillNumber());
            stmt.setTimestamp(2, new Timestamp(bill.getBillDate().getTime()));
            stmt.setInt(3, bill.getCustomer().getId());
            stmt.setInt(4, bill.getCashier().getId());
            stmt.setDouble(5, bill.getSubtotal());
            stmt.setDouble(6, bill.getDiscount());
            stmt.setDouble(7, bill.getTax());
            stmt.setDouble(8, bill.getTotal());
            stmt.setString(9, bill.getPaymentMethod());
            stmt.setString(10, bill.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int billId = rs.getInt(1);
                    bill.setId(billId);
                    
                    // Save bill items
                    return saveBillItems(bill);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean saveBillItems(Bill bill) {
        String query = "INSERT INTO bill_items (bill_id, book_id, quantity, unit_price, total) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            for (BillItem item : bill.getItems()) {
                stmt.setInt(1, bill.getId());
                stmt.setInt(2, item.getBook().getId());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getUnitPrice());
                stmt.setDouble(5, item.getTotal());
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Bill getBillById(int id) {
        String query = "SELECT b.*, " +
                      "u1.id as customer_id, u1.full_name as customer_name, u1.email as customer_email, u1.phone as customer_phone, u1.account_number as customer_account, " +
                      "u2.id as cashier_id, u2.full_name as cashier_name, u2.email as cashier_email " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "WHERE b.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillNumber(rs.getString("bill_number"));
                bill.setBillDate(rs.getTimestamp("bill_date"));
                bill.setSubtotal(rs.getDouble("subtotal"));
                bill.setDiscount(rs.getDouble("discount"));
                bill.setTax(rs.getDouble("tax"));
                bill.setTotal(rs.getDouble("total"));
                bill.setPaymentMethod(rs.getString("payment_method"));
                bill.setStatus(rs.getString("status"));
                
                // Create customer object
                User customer = new User();
                customer.setId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("customer_name"));
                customer.setEmail(rs.getString("customer_email"));
                customer.setPhone(rs.getString("customer_phone"));
                customer.setAccountNumber(rs.getString("customer_account"));
                bill.setCustomer(customer);
                
                // Create cashier object
                User cashier = new User();
                cashier.setId(rs.getInt("cashier_id"));
                cashier.setFullName(rs.getString("cashier_name"));
                cashier.setEmail(rs.getString("cashier_email"));
                bill.setCashier(cashier);
                
                // Load bill items
                bill.setItems(getBillItems(id));
                
                return bill;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<BillItem> getBillItems(int billId) {
        List<BillItem> items = new ArrayList<>();
        String query = "SELECT bi.*, b.title, b.author, b.isbn, b.price " +
                      "FROM bill_items bi " +
                      "JOIN books b ON bi.book_id = b.id " +
                      "WHERE bi.bill_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillItem item = new BillItem();
                item.setId(rs.getInt("id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setTotal(rs.getDouble("total"));
                
                // Create book object
                model.Book book = new model.Book();
                book.setId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPrice(rs.getDouble("price"));
                item.setBook(book);
                
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT b.*, " +
                      "u1.full_name as customer_name, u1.email as customer_email, " +
                      "u2.full_name as cashier_name " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "ORDER BY b.bill_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Bill bill = createBillFromResultSet(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }
    
    public boolean updateBillStatus(int billId, String status) {
        String query = "UPDATE bills SET status = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, billId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int getTodayBillsCount() {
        String query = "SELECT COUNT(*) FROM bills WHERE DATE(bill_date) = CURDATE()";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getTodaySalesTotal() {
        String query = "SELECT COALESCE(SUM(total), 0) FROM bills WHERE DATE(bill_date) = CURDATE() AND status != 'CANCELLED'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public int getTodayCustomersCount() {
        String query = "SELECT COUNT(DISTINCT customer_id) FROM bills WHERE DATE(bill_date) = CURDATE()";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Bill> getRecentBills(int limit) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT b.*, " +
                      "u1.full_name as customer_name, u1.email as customer_email, " +
                      "u2.full_name as cashier_name " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "ORDER BY b.bill_date DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Bill bill = createBillFromResultSet(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public List<Bill> getBillsByCashier(int cashierId) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT b.*, " +
                      "u1.full_name as customer_name, u1.email as customer_email, " +
                      "u2.full_name as cashier_name " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "WHERE b.cashier_id = ? ORDER BY b.bill_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cashierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Bill bill = createBillFromResultSet(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public int getTodayBillsCountByCashier(int cashierId) {
        String query = "SELECT COUNT(*) FROM bills WHERE cashier_id = ? AND DATE(bill_date) = CURDATE()";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cashierId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTodaySalesTotalByCashier(int cashierId) {
        String query = "SELECT COALESCE(SUM(total), 0) FROM bills WHERE cashier_id = ? AND DATE(bill_date) = CURDATE() AND status != 'CANCELLED'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cashierId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalSalesByCashier(int cashierId) {
        String query = "SELECT COALESCE(SUM(total), 0) FROM bills WHERE cashier_id = ? AND status != 'CANCELLED'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cashierId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Bill> getRecentBillsByCashier(int cashierId, int limit) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT b.*, " +
                      "u1.full_name as customer_name, u1.email as customer_email, " +
                      "u2.full_name as cashier_name " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "WHERE b.cashier_id = ? ORDER BY b.bill_date DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cashierId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Bill bill = createBillFromResultSet(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public List<Bill> getBillsByCustomer(int customerId) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT b.*, " +
                      "u1.full_name as customer_name, u1.email as customer_email, " +
                      "u2.full_name as cashier_name " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "WHERE b.customer_id = ? ORDER BY b.bill_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Bill bill = createBillFromResultSet(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }
    
    public List<Bill> getBillsByCustomerWithDateRange(int customerId, java.sql.Date startDate, java.sql.Date endDate) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT b.*, " +
                      "u1.full_name as customer_name, u1.email as customer_email, " +
                      "u2.full_name as cashier_name " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "WHERE b.customer_id = ? AND DATE(b.bill_date) BETWEEN ? AND ? ORDER BY b.bill_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Bill bill = createBillFromResultSet(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }
    
    public Map<String, Object> getCustomerPurchaseStats(int customerId) {
        Map<String, Object> stats = new HashMap<>();
        String query = "SELECT " +
                      "COUNT(*) as total_bills, " +
                      "COALESCE(SUM(total), 0) as total_spent, " +
                      "COALESCE(AVG(total), 0) as avg_bill_amount, " +
                      "MIN(bill_date) as first_purchase, " +
                      "MAX(bill_date) as last_purchase, " +
                      "COUNT(DISTINCT DATE(bill_date)) as unique_days " +
                      "FROM bills WHERE customer_id = ? AND status != 'CANCELLED'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                stats.put("totalBills", rs.getInt("total_bills"));
                stats.put("totalSpent", rs.getDouble("total_spent"));
                stats.put("avgBillAmount", rs.getDouble("avg_bill_amount"));
                stats.put("firstPurchase", rs.getTimestamp("first_purchase"));
                stats.put("lastPurchase", rs.getTimestamp("last_purchase"));
                stats.put("uniqueDays", rs.getInt("unique_days"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public List<Map<String, Object>> getCashierStats() {
        List<Map<String, Object>> cashierStats = new ArrayList<>();
        String query = "SELECT u.id, u.full_name, u.username, u.email, " +
                      "COUNT(b.id) as bill_count, " +
                      "COALESCE(SUM(b.total), 0) as total_sales, " +
                      "COALESCE(AVG(b.total), 0) as avg_bill_amount " +
                      "FROM users u " +
                      "LEFT JOIN bills b ON u.id = b.cashier_id AND b.status != 'CANCELLED' " +
                      "WHERE u.role = 'CASHIER' " +
                      "GROUP BY u.id, u.full_name, u.username, u.email " +
                      "ORDER BY total_sales DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> stats = new HashMap<>();
                
                User cashier = new User();
                cashier.setId(rs.getInt("id"));
                cashier.setFullName(rs.getString("full_name"));
                cashier.setUsername(rs.getString("username"));
                cashier.setEmail(rs.getString("email"));
                cashier.setRole("CASHIER");
                
                stats.put("cashier", cashier);
                stats.put("billCount", rs.getInt("bill_count"));
                stats.put("totalSales", rs.getDouble("total_sales"));
                stats.put("avgBillAmount", rs.getDouble("avg_bill_amount"));
                stats.put("feedbackScore", 4.5); // Mock feedback score
                
                cashierStats.add(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cashierStats;
    }
    
    private Bill createBillFromResultSet(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setBillDate(rs.getTimestamp("bill_date"));
        bill.setSubtotal(rs.getDouble("subtotal"));
        bill.setDiscount(rs.getDouble("discount"));
        bill.setTax(rs.getDouble("tax"));
        bill.setTotal(rs.getDouble("total"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        bill.setStatus(rs.getString("status"));
        
        // Create customer object with basic info
        User customer = new User();
        customer.setFullName(rs.getString("customer_name"));
        customer.setEmail(rs.getString("customer_email"));
        bill.setCustomer(customer);
        
        // Create cashier object with basic info
        User cashier = new User();
        cashier.setFullName(rs.getString("cashier_name"));
        bill.setCashier(cashier);
        
        return bill;
    }
}