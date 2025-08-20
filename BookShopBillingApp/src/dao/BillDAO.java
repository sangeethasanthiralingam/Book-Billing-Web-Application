package dao;

import model.Bill;
import model.BillItem;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String query = "SELECT b.*, u1.full_name as customer_name, u1.phone as customer_phone, u1.email as customer_email, " +
                      "u2.full_name as cashier_name " +
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
                model.User customer = new model.User();
                customer.setId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("customer_name"));
                customer.setPhone(rs.getString("customer_phone"));
                customer.setEmail(rs.getString("customer_email"));
                bill.setCustomer(customer);
                
                // Create cashier object
                model.User cashier = new model.User();
                cashier.setId(rs.getInt("cashier_id"));
                cashier.setFullName(rs.getString("cashier_name"));
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
        String query = "SELECT bi.*, b.title, b.author, b.isbn " +
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
        String query = "SELECT b.*, u1.full_name as customer_name, u2.full_name as cashier_name " +
                      "FROM bills b " +
                      "JOIN users u1 ON b.customer_id = u1.id " +
                      "JOIN users u2 ON b.cashier_id = u2.id " +
                      "ORDER BY b.bill_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
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
                
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
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
    
    @SuppressWarnings("CallToPrintStackTrace")
    public double getTodaySalesTotal() {
        String query = "SELECT COALESCE(SUM(total), 0) FROM bills WHERE DATE(bill_date) = CURDATE()";
        
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
        String query = "SELECT b.*, u1.full_name as customer_name, u1.phone as customer_phone, " +
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
                
                // Create customer and cashier objects
                model.User customer = new model.User();
                customer.setId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("customer_name"));
                customer.setPhone(rs.getString("customer_phone"));
                bill.setCustomer(customer);
                
                model.User cashier = new model.User();
                cashier.setId(rs.getInt("cashier_id"));
                cashier.setFullName(rs.getString("cashier_name"));
                bill.setCashier(cashier);
                
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public List<Bill> getBillsByCashier(int cashierId) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM bills WHERE cashier_id = ? ORDER BY bill_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cashierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
        String query = "SELECT SUM(total) FROM bills WHERE cashier_id = ? AND DATE(bill_date) = CURDATE()";
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
        String query = "SELECT SUM(total) FROM bills WHERE cashier_id = ?";
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
        String query = "SELECT b.*, u1.full_name as customer_name, u1.phone as customer_phone, " +
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
                
                // Create customer and cashier objects
                model.User customer = new model.User();
                customer.setId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("customer_name"));
                customer.setPhone(rs.getString("customer_phone"));
                bill.setCustomer(customer);
                
                model.User cashier = new model.User();
                cashier.setId(rs.getInt("cashier_id"));
                cashier.setFullName(rs.getString("cashier_name"));
                bill.setCashier(cashier);
                
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public List<Bill> getBillsByCustomer(int customerId) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM bills WHERE customer_id = ? ORDER BY bill_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }
    
    public List<Bill> getBillsByCustomerWithDateRange(int customerId, java.sql.Date startDate, java.sql.Date endDate) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM bills WHERE customer_id = ? AND DATE(bill_date) BETWEEN ? AND ? ORDER BY bill_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }
    
    public java.util.Map<String, Object> getCustomerPurchaseStats(int customerId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        String query = "SELECT " +
                      "COUNT(*) as total_bills, " +
                      "SUM(total) as total_spent, " +
                      "AVG(total) as avg_bill_amount, " +
                      "MIN(bill_date) as first_purchase, " +
                      "MAX(bill_date) as last_purchase, " +
                      "COUNT(DISTINCT DATE(bill_date)) as unique_days " +
                      "FROM bills WHERE customer_id = ?";
        
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
    
    /**
     * Save individual bill item
     */
    public boolean saveBillItem(BillItem item) {
        String query = "INSERT INTO bill_items (bill_id, book_id, quantity, unit_price, total) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, item.getBillId());
            stmt.setInt(2, item.getBookId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            stmt.setDouble(5, item.getPrice() * item.getQuantity()); // Calculate total
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get collection requests (bills with status COLLECTION_REQUEST)
     */
    public List<Bill> getCollectionRequests(int limit) {
        List<Bill> requests = new ArrayList<>();
        String query = "SELECT * FROM bills WHERE status = 'PENDING' AND payment_method = 'COLLECTION' ORDER BY bill_date DESC LIMIT ?";
        
        System.out.println("DEBUG: Executing query: " + query);
        System.out.println("DEBUG: Limit: " + limit);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            
            while (rs.next()) {
                count++;
                System.out.println("DEBUG: Found collection request #" + count + ": " + rs.getString("bill_number"));
                
                Bill request = new Bill();
                request.setId(rs.getInt("id"));
                request.setBillNumber(rs.getString("bill_number"));
                request.setBillDate(rs.getTimestamp("bill_date"));
                request.setStatus(rs.getString("status"));
                request.setTotal(rs.getDouble("total"));
                
                // Get customer separately
                model.User customer = getUserById(rs.getInt("customer_id"));
                request.setCustomer(customer);
                
                // Load bill items (requested books)
                request.setItems(getBillItems(request.getId()));
                
                requests.add(request);
            }
            
            System.out.println("DEBUG: Total collection requests found: " + requests.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    private model.User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                model.User user = new model.User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save a bill (alias for saveBill, for test compatibility)
     */
    public boolean save(Bill bill) {
        return saveBill(bill);
    }

    /**
     * Update a bill (implement as needed)
     */
    public boolean update(Bill bill) {
        // Implement update logic if needed for your tests
        // For now, return false or throw UnsupportedOperationException
        throw new UnsupportedOperationException("Update not implemented");
    }

    /**
     * Find a bill by ID (alias for getBillById, for test compatibility)
     */
    public Bill findById(int id) {
        return getBillById(id);
    }

    /**
     * Delete a bill by ID (implement as needed)
     */
    public boolean delete(int id) {
        // Implement delete logic if needed for your tests
        // For now, return false or throw UnsupportedOperationException
        throw new UnsupportedOperationException("Delete not implemented");
    }
}
