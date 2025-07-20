package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dao.BookDAO;
import dao.BillDAO;
import model.Bill;

/**
 * Controller for dashboard operations
 */
public class DashboardController extends BaseController {
    
    /**
     * Handle dashboard page
     */
    public void handleDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            BillDAO billDAO = new BillDAO();
            String userRole = getUserRole(request);
            Integer userId = getUserId(request);

            if ("CASHIER".equals(userRole) && userId != null) {
                // Cashier-specific dashboard
                int todayBills = billDAO.getTodayBillsCountByCashier(userId);
                double todaySales = billDAO.getTodaySalesTotalByCashier(userId);
                List<Bill> recentBills = billDAO.getRecentBillsByCashier(userId, 5);
                request.setAttribute("todayBills", todayBills);
                request.setAttribute("todaySales", todaySales);
                request.setAttribute("recentBills", recentBills);
            } else {
                // Admin/other roles: show global stats
                int totalBooks = bookDAO.getTotalBooksCount();
                int lowStockBooks = bookDAO.getLowStockBooksCount();
                int todayBills = billDAO.getTodayBillsCount();
                double todaySales = billDAO.getTodaySalesTotal();
                int todayCustomers = billDAO.getTodayCustomersCount();
                List<Bill> recentBills = billDAO.getRecentBills(5);
                request.setAttribute("totalBooks", totalBooks);
                request.setAttribute("lowStockBooks", lowStockBooks);
                request.setAttribute("todayBills", todayBills);
                request.setAttribute("todaySales", todaySales);
                request.setAttribute("todayCustomers", todayCustomers);
                request.setAttribute("recentBills", recentBills);
            }
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading dashboard data");
        }
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }
} 