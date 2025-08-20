package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Book;
import service.ConfigurationService;
import util.DBConnection;

/**
 * DAO Pattern: Book Data Access Object
 * Handles all database operations for Book entities
 */
public class BookDAO {
    private final DBConnection dbConnection;
    
    /**
     * Initialize BookDAO with database connection
     */
    public BookDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT * FROM books";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPrice(rs.getDouble("price"));
                book.setQuantity(rs.getInt("quantity"));
                book.setCategory(rs.getString("category"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublicationYear(rs.getObject("publication_year") != null ? rs.getInt("publication_year") : null);
                book.setActive(rs.getBoolean("is_active"));
                book.setCoverImage(rs.getString("cover_image"));
                book.setLanguage(rs.getString("language"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return books;
    }
    
    public Book getBookById(int id) {
        String query = "SELECT * FROM books WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPrice(rs.getDouble("price"));
                book.setQuantity(rs.getInt("quantity"));
                book.setCategory(rs.getString("category"));
                book.setPublisher(rs.getString("publisher"));
                
                book.setPublicationYear(rs.getObject("publication_year") != null ? rs.getInt("publication_year") : null);
                book.setActive(rs.getBoolean("is_active"));
                book.setCoverImage(rs.getString("cover_image"));
                book.setLanguage(rs.getString("language"));
                
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, isbn, price, quantity, category, publisher, publication_year, language) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setDouble(4, book.getPrice());
            stmt.setInt(5, book.getQuantity());
            stmt.setString(6, book.getCategory());
            stmt.setString(7, book.getPublisher());
            stmt.setObject(8, book.getPublicationYear());
            stmt.setString(9, book.getLanguage());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateBook(Book book) {
        String query = "UPDATE books SET title=?, author=?, isbn=?, price=?, quantity=?, category=?, publisher=?, publication_year=?, language=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setDouble(4, book.getPrice());
            stmt.setInt(5, book.getQuantity());
            stmt.setString(6, book.getCategory());
            stmt.setString(7, book.getPublisher());
            stmt.setObject(8, book.getPublicationYear());
            stmt.setString(9, book.getLanguage());
            stmt.setInt(10, book.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteBook(int id) {
        String query = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateQuantity(int bookId, int quantity) {
        String query = "UPDATE books SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, bookId);
            stmt.setInt(3, quantity);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int getTotalBooksCount() {
        String query = "SELECT COUNT(*) FROM books";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return 0;
    }
    
    public int getLowStockBooksCount() {
        ConfigurationService configService = ConfigurationService.getInstance();
        int threshold = configService.getLowStockThreshold();
        
        String query = "SELECT COUNT(*) FROM books WHERE quantity <= ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return 0;
    }
    
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE (title LIKE ? OR author LIKE ? OR isbn LIKE ?) AND is_active = 1 ORDER BY title";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPrice(rs.getDouble("price"));
                book.setQuantity(rs.getInt("quantity"));
                book.setCategory(rs.getString("category"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublicationYear(rs.getObject("publication_year") != null ? rs.getInt("publication_year") : null);
                book.setActive(rs.getBoolean("is_active"));
                book.setCoverImage(rs.getString("cover_image"));
                book.setLanguage(rs.getString("language"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return books;
    }

    public boolean save(Book book) {
        if (book.getId() == 0) {
            return addBook(book);
        } else {
            return updateBook(book);
        }
    }

    public Book findById(int id) {
        return getBookById(id);
    }

    public boolean update(Book book) {
        return updateBook(book);
    }

    public boolean delete(int id) {
        return deleteBook(id);
    }
}