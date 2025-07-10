package dao;

import model.Book;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private DBConnection dbConnection;
    
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
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, isbn, price, quantity, category) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setDouble(4, book.getPrice());
            stmt.setInt(5, book.getQuantity());
            stmt.setString(6, book.getCategory());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateBook(Book book) {
        String query = "UPDATE books SET title=?, author=?, isbn=?, price=?, quantity=?, category=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setDouble(4, book.getPrice());
            stmt.setInt(5, book.getQuantity());
            stmt.setString(6, book.getCategory());
            stmt.setInt(7, book.getId());
            
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
        String query = "SELECT COUNT(*) FROM books WHERE quantity <= 5";
        
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
} 