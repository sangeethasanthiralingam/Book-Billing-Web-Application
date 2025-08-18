package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dao.*;
import model.*;

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
                
                // Get collection requests
                System.out.println("DEBUG: About to call getCollectionRequests(10)");
                List<Bill> collectionRequests = billDAO.getCollectionRequests(10);
                System.out.println("DEBUG: getCollectionRequests returned: " + (collectionRequests != null ? collectionRequests.size() : "null") + " items");
                
                request.setAttribute("totalBooks", totalBooks);
                request.setAttribute("lowStockBooks", lowStockBooks);
                request.setAttribute("todayBills", todayBills);
                request.setAttribute("todaySales", todaySales);
                request.setAttribute("todayCustomers", todayCustomers);
                request.setAttribute("recentBills", recentBills);
                request.setAttribute("collectionRequests", collectionRequests);
            }
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading dashboard data");
        }
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }
} 