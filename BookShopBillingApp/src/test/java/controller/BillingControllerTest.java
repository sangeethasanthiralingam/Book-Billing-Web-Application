package controller;

import dao.*;
import model.*;
import util.TestDataBuilder;
import org.junit.jupiter.api.*;
import org.mockito.*;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
@DisplayName("Billing Controller Tests")
class BillingControllerTest {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private RequestDispatcher dispatcher;
    
    @Mock
    private PrintWriter writer;
    
    @Mock
    private BufferedReader reader;
    
    @Mock
    private BillDAO billDAO;
    
    @Mock
    private BookDAO bookDAO;
    
    @Mock
    private UserDAO userDAO;
    
    @InjectMocks
    private BillingController billingController;
    
    private User testCashier;
    private User testCustomer;
    private Book testBook;
    
    @BeforeEach
    void setUp() throws IOException {
        testCashier = TestDataBuilder.createTestCashier();
        testCustomer = TestDataBuilder.createTestCustomer();
        testBook = TestDataBuilder.createTestBook();
        
        when(request.getSession(false)).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
    }
    
    @Test
    @DisplayName("Should handle billing page request")
    void testHandleBilling() throws ServletException, IOException {

        when(session.getAttribute("user")).thenReturn(testCashier);
        when(request.getRequestDispatcher("/jsp/billing.jsp")).thenReturn(dispatcher);
        

        billingController.handleBilling(request, response);
        

        verify(request).setAttribute("user", testCashier);
        verify(dispatcher).forward(request, response);
    }
    
    @Test
    @DisplayName("Should redirect to login when user not authenticated")
    void testHandleBillingNotAuthenticated() throws ServletException, IOException {

        when(session.getAttribute("user")).thenReturn(null);
        

        billingController.handleBilling(request, response);
        

        verify(response).sendRedirect("login");
    }
    
    @Test
    @DisplayName("Should create bill successfully")
    void testCreateBill() throws ServletException, IOException {

        when(session.getAttribute("user")).thenReturn(testCashier);
        when(request.getParameter("customerId")).thenReturn("1");
        when(request.getParameter("paymentMethod")).thenReturn("CASH");
        when(request.getParameterValues("bookId")).thenReturn(new String[]{"1"});
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"2"});
        
        when(userDAO.findById(1)).thenReturn(testCustomer);
        when(bookDAO.findById(1)).thenReturn(testBook);
        when(billDAO.save(any(Bill.class))).thenReturn(true);
        

        billingController.handleCreateBill(request, response);
        

        verify(billDAO).save(any(Bill.class));
        verify(response).setContentType("application/json");
    }
    
    @Test
    @DisplayName("Should handle invalid customer ID")
    void testCreateBillInvalidCustomer() throws ServletException, IOException {

        when(session.getAttribute("user")).thenReturn(testCashier);
        when(request.getParameter("customerId")).thenReturn("999");
        when(userDAO.findById(999)).thenReturn(null);
        

        billingController.handleCreateBill(request, response);
        

        verify(writer).write(contains("error"));
        verify(writer).write(contains("Customer not found"));
    }
    
    @Test
    @DisplayName("Should handle pattern demo request")
    void testHandlePatternDemo() throws ServletException, IOException {

        when(session.getAttribute("user")).thenReturn(testCashier);
        

        billingController.handlePatternDemo(request, response);
        

        verify(response).setContentType("application/json");
        verify(writer).write(contains("patterns"));
    }
    
    @Test
    @DisplayName("Should generate invoice successfully")
    void testGenerateInvoice() throws ServletException, IOException {

        Bill testBill = TestDataBuilder.createTestBill();
        when(request.getParameter("billId")).thenReturn("1");
        when(billDAO.findById(1)).thenReturn(testBill);
        when(request.getRequestDispatcher("/jsp/invoice.jsp")).thenReturn(dispatcher);
        

        billingController.handleInvoice(request, response);
        

        verify(request).setAttribute("bill", testBill);
        verify(dispatcher).forward(request, response);
    }
    
    @Test
    @DisplayName("Should handle bill not found for invoice")
    void testGenerateInvoiceBillNotFound() throws ServletException, IOException {

        when(request.getParameter("billId")).thenReturn("999");
        when(billDAO.findById(999)).thenReturn(null);
        

        billingController.handleInvoice(request, response);
        

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Bill not found");
    }
    
    @Test
    @DisplayName("Should handle command history request")
    void testHandleCommandHistory() throws ServletException, IOException {

        when(request.getParameter("action")).thenReturn("history");
        

        billingController.handleCommandHistory(request, response);
        

        verify(response).setContentType("application/json");
        verify(writer).write(contains("commands"));
    }
    
    @Test
    @DisplayName("Should handle order state transition")
    void testHandleOrderState() throws ServletException, IOException {

        when(request.getParameter("billId")).thenReturn("1");
        when(request.getParameter("action")).thenReturn("process");
        
        Bill testBill = TestDataBuilder.createTestBill();
        when(billDAO.findById(1)).thenReturn(testBill);
        

        billingController.handleOrderState(request, response);
        

        verify(response).setContentType("application/json");
        verify(writer).write(contains("state"));
    }
}