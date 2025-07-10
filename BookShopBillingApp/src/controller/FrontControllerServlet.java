package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.BookDAO;
import dao.BillDAO;
import dao.UserDAO;
import model.Book;
import model.Bill;
import model.User;
import java.util.List;

@WebServlet("/controller/*")
public class FrontControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        String action = pathInfo != null ? pathInfo.substring(1) : "";
        
        // Check if user is authenticated (except for login)
        if (!action.equals("login") && !isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }
        
        try {
            switch (action) {
                case "login":
                    handleLogin(request, response);
                    break;
                case "logout":
                    handleLogout(request, response);
                    break;
                case "dashboard":
                    handleDashboard(request, response);
                    break;
                case "books":
                    handleBooks(request, response);
                    break;
                case "billing":
                    handleBilling(request, response);
                    break;
                case "invoice":
                    handleInvoice(request, response);
                    break;
                case "reports":
                    handleReports(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/jsp/dashboard.jsp");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }
    
    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username != null && password != null) {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticateUser(username, password);
            
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user.getFullName());
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                response.sendRedirect(request.getContextPath() + "/controller/dashboard");
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
    }
    
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            BillDAO billDAO = new BillDAO();
            
            // Load dashboard statistics
            int totalBooks = bookDAO.getTotalBooksCount();
            int lowStockBooks = bookDAO.getLowStockBooksCount();
            int todayBills = billDAO.getTodayBillsCount();
            double todaySales = billDAO.getTodaySalesTotal();
            int todayCustomers = billDAO.getTodayCustomersCount();
            List<Bill> recentBills = billDAO.getRecentBills(5);
            
            // Set attributes for JSP
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("lowStockBooks", lowStockBooks);
            request.setAttribute("todayBills", todayBills);
            request.setAttribute("todaySales", todaySales);
            request.setAttribute("todayCustomers", todayCustomers);
            request.setAttribute("recentBills", recentBills);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard data: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }
    
    private void handleBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            request.setAttribute("books", books);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading books: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/books.jsp").forward(request, response);
    }
    
    private void handleBilling(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            request.setAttribute("books", books);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading books for billing: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/billing.jsp").forward(request, response);
    }
    
    private void handleInvoice(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // TODO: Generate invoice
        request.getRequestDispatcher("/jsp/invoice.jsp").forward(request, response);
    }
    
    private void handleReports(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BillDAO billDAO = new BillDAO();
            BookDAO bookDAO = new BookDAO();
            
            List<Bill> allBills = billDAO.getAllBills();
            List<Book> allBooks = bookDAO.getAllBooks();
            
            request.setAttribute("allBills", allBills);
            request.setAttribute("allBooks", allBooks);
            request.setAttribute("totalBills", allBills.size());
            request.setAttribute("totalBooks", allBooks.size());
            request.setAttribute("lowStockBooks", bookDAO.getLowStockBooksCount());
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading reports: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/reports.jsp").forward(request, response);
    }
} 