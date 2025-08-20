package template;

import java.util.ArrayList;
import java.util.List;

import dao.BillDAO;
import model.Bill;

/**
 * Template Method Pattern: Sales Report Template
 * Concrete implementation for generating sales reports
 */
public class SalesReportTemplate extends ReportTemplate {
    private final BillDAO billDAO;
    
    public SalesReportTemplate() {
        this.billDAO = new BillDAO();
    }
    
    @Override
    protected boolean validateParameters(String reportType, Object... parameters) {
        if (reportType == null || reportType.trim().isEmpty()) {
            return false;
        }
        
        // Validate based on report type
        return switch (reportType.toUpperCase()) {
            case "DAILY_SALES", "WEEKLY_SALES", "MONTHLY_SALES", "YEARLY_SALES" -> true;
            default -> false;
        };
    }
    
    @Override
    protected List<Object> collectData(String reportType, Object... parameters) {
        List<Object> data = new ArrayList<>();
        
        try {
            switch (reportType.toUpperCase()) {
                case "DAILY_SALES" -> {
                    data.add(billDAO.getTodayBillsCount());
                    data.add(billDAO.getTodaySalesTotal());
                    data.add(billDAO.getTodayCustomersCount());
                    data.add(billDAO.getRecentBills(10));
                }
                    
                case "WEEKLY_SALES" -> // In a real implementation, you'd have weekly methods
                    data.add(billDAO.getAllBills());
                    
                case "MONTHLY_SALES" -> // In a real implementation, you'd have monthly methods
                    data.add(billDAO.getAllBills());
                    
                case "YEARLY_SALES" -> // In a real implementation, you'd have yearly methods
                    data.add(billDAO.getAllBills());
            }
        } catch (Exception e) {
            System.err.println("Error collecting data for " + reportType + ": " + e.getMessage());
        }
        
        return data;
    }
    
    @Override
    protected List<Object> processData(List<Object> data) {
        List<Object> processedData = new ArrayList<>();
        
        try {
            for (Object item : data) {
                if (item instanceof Number number) {
                    // Process numeric data
                    processedData.add(formatCurrency(number));
                } else if (item instanceof List) {
                    // Process list data
                    processedData.add(processBillList((List<?>) item));
                } else {
                    processedData.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing data: " + e.getMessage());
        }
        
        return processedData;
    }
    
    @Override
    protected String formatReport(List<Object> data, String reportType) {
        StringBuilder report = new StringBuilder();
        
        switch (reportType.toUpperCase()) {
            case "DAILY_SALES" -> {
                report.append("DAILY SALES SUMMARY\n");
                report.append("===================\n\n");
                
                if (data.size() >= 4) {
                    report.append("Total Bills Today: ").append(data.get(0)).append("\n");
                    report.append("Total Sales Today: ").append(data.get(1)).append("\n");
                    report.append("Unique Customers: ").append(data.get(2)).append("\n");
                    report.append("Recent Bills: ").append(data.get(3)).append("\n");
                }
            }
                
            case "WEEKLY_SALES" -> {
                report.append("WEEKLY SALES SUMMARY\n");
                report.append("====================\n\n");
                report.append("Weekly data processing...\n");
            }
                
            case "MONTHLY_SALES" -> {
                report.append("MONTHLY SALES SUMMARY\n");
                report.append("=====================\n\n");
                report.append("Monthly data processing...\n");
            }
                
            case "YEARLY_SALES" -> {
                report.append("YEARLY SALES SUMMARY\n");
                report.append("====================\n\n");
                report.append("Yearly data processing...\n");
            }
        }
        
        return report.toString();
    }
    
    @Override
    public String getReportType() {
        return "SALES_REPORT";
    }
    
    @Override
    public String[] getSupportedReportTypes() {
        return new String[]{"DAILY_SALES", "WEEKLY_SALES", "MONTHLY_SALES", "YEARLY_SALES"};
    }
    
    private String formatCurrency(Number amount) {
        if (amount instanceof Double || amount instanceof Float) {
            return String.format("LKR %.2f", amount.doubleValue());
        }
        return amount.toString();
    }
    
    private String processBillList(List<?> bills) {
        if (bills == null || bills.isEmpty()) {
            return "No bills found";
        }
        
        StringBuilder result = new StringBuilder();
        result.append(bills.size()).append(" bills found\n");
        
        for (Object bill : bills) {
            if (bill instanceof Bill) {
                Bill b = (Bill) bill;
                result.append("- Bill #").append(b.getBillNumber())
                      .append(": ").append(formatCurrency(b.getTotal()))
                      .append(" (").append(b.getBillDate()).append(")\n");
            }
        }
        
        return result.toString();
    }
} 