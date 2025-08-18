package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dao.*;
import model.*;

/**
 * Controller for reporting operations
 */
public class ReportController extends BaseController {
    
    /**
     * Handle reports page
     */
    public void handleReports(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BillDAO billDAO = new BillDAO();
            BookDAO bookDAO = new BookDAO();
            String userRole = getUserRole(request);
            Integer userId = getUserId(request);

            if ("CASHIER".equals(userRole) && userId != null) {
                // Cashier-specific reports
                List<Bill> allBills = billDAO.getBillsByCashier(userId);
                int totalBills = allBills.size();
                double totalSales = billDAO.getTotalSalesByCashier(userId);
                int todayBills = billDAO.getTodayBillsCountByCashier(userId);
                double todaySales = billDAO.getTodaySalesTotalByCashier(userId);
                List<Bill> recentBills = billDAO.getRecentBillsByCashier(userId, 5);
                
                request.setAttribute("allBills", allBills);
                request.setAttribute("totalBills", totalBills);
                request.setAttribute("totalSales", totalSales);
                request.setAttribute("todayBills", todayBills);
                request.setAttribute("todaySales", todaySales);
                request.setAttribute("recentBills", recentBills);
            } else {
                // Admin/other roles: show global reports
                List<Bill> allBills = billDAO.getAllBills();
                List<Book> allBooks = bookDAO.getAllBooks();
                int todayBills = billDAO.getTodayBillsCount();
                double todaySales = billDAO.getTodaySalesTotal();
                int todayCustomers = billDAO.getTodayCustomersCount();
                
                // Calculate total sales from all bills
                double totalSales = 0.0;
                for (Bill bill : allBills) {
                    totalSales += bill.getTotal();
                }
                
                request.setAttribute("allBills", allBills);
                request.setAttribute("allBooks", allBooks);
                request.setAttribute("totalBills", allBills.size());
                request.setAttribute("totalBooks", allBooks.size());
                request.setAttribute("totalSales", totalSales);
                request.setAttribute("todayBills", todayBills);
                request.setAttribute("todaySales", todaySales);
                request.setAttribute("todayCustomers", todayCustomers);
                request.setAttribute("lowStockBooks", bookDAO.getLowStockBooksCount());
            }
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading reports");
        }
        request.getRequestDispatcher("/jsp/reports.jsp").forward(request, response);
    }
} 