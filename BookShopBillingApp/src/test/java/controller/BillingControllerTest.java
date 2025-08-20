package controller;

import dao.*;
import model.*;
import util.TestDataBuilder;
import org.junit.jupiter.api.*;
import org.mockito.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Session setup
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        // Writer setup
        when(response.getWriter()).thenReturn(writer);

        // Dispatcher setup
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Reader setup
        when(request.getReader()).thenReturn(reader);
        when(reader.readLine()).thenReturn(null);

        // Test data
        testCashier = new User();
        testCashier.setId(1);
        testCashier.setName("Cashier");

        testCustomer = new User();
        testCustomer.setId(2);
        testCustomer.setName("Customer");

        testBook = new Book();
        testBook.setId(1);
        testBook.setTitle("Test Book");
    }

    @Test
    @DisplayName("Should handle billing page request")
    void testHandleBilling() throws Exception {
        when(session.getAttribute("user")).thenReturn(testCashier);

        billingController.handleBilling(request, response);

        verify(request).setAttribute("user", testCashier);
        verify(request).getRequestDispatcher("/jsp/billing.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should redirect to login when user not authenticated")
    void testHandleBillingNotAuthenticated() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);

        billingController.handleBilling(request, response);

        verify(response).sendRedirect("login");
    }

    @Test
    @DisplayName("Should create bill successfully")
    void testCreateBill() throws Exception {
        when(session.getAttribute("user")).thenReturn(testCashier);
        when(request.getParameter("customerId")).thenReturn("2");
        when(request.getParameter("paymentMethod")).thenReturn("CASH");
        when(request.getParameterValues("bookId")).thenReturn(new String[]{"1"});
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"2"});

        when(userDAO.findById(2)).thenReturn(testCustomer);
        when(bookDAO.findById(1)).thenReturn(testBook);
        when(billDAO.save(any(Bill.class))).thenReturn(true);

        billingController.handleGenerateBill(request, response);

        verify(billDAO).save(any(Bill.class));
        verify(response).setContentType("application/json");
        verify(writer).write(contains("success"));
    }

    @Test
    @DisplayName("Should handle invalid customer ID")
    void testCreateBillInvalidCustomer() throws Exception {
        when(session.getAttribute("user")).thenReturn(testCashier);
        when(request.getParameter("customerId")).thenReturn("999");
        when(userDAO.findById(999)).thenReturn(null);

        billingController.handleGenerateBill(request, response);

        verify(writer).write(contains("Customer not found"));
    }

    @Test
    @DisplayName("Should handle pattern demo request")
    void testHandlePatternDemo() throws Exception {
        when(session.getAttribute("user")).thenReturn(testCashier);

        billingController.handlePatternDemo(request, response);

        verify(response).setContentType("application/json");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(captor.capture());

        String actualOutput = captor.getValue();
        assertTrue(actualOutput.contains("patterns"));
    }

    @Test
    @DisplayName("Should generate invoice successfully")
    void testGenerateInvoice() throws Exception {
        Bill testBill = TestDataBuilder.createTestBill();
        when(request.getParameter("billId")).thenReturn("1");
        when(billDAO.findById(1)).thenReturn(testBill);

        billingController.handleInvoice(request, response);

        verify(request).setAttribute("bill", testBill);
        verify(request).getRequestDispatcher("/jsp/invoice.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should handle bill not found for invoice")
    void testGenerateInvoiceBillNotFound() throws Exception {
        when(request.getParameter("billId")).thenReturn("999");
        when(billDAO.findById(999)).thenReturn(null);

        billingController.handleInvoice(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Bill not found");
    }

    @Test
    @DisplayName("Should handle command history request")
    void testHandleCommandHistory() throws Exception {
        when(request.getParameter("action")).thenReturn("history");

        billingController.handleCommandHistory(request, response);

        verify(response).setContentType("application/json");
        verify(writer).write(contains("commands"));
    }

    @Test
    @DisplayName("Should handle order state transition")
    void testHandleOrderState() throws Exception {
        when(request.getParameter("billId")).thenReturn("1");
        when(request.getParameter("action")).thenReturn("process");

        Bill testBill = TestDataBuilder.createTestBill();
        when(billDAO.findById(1)).thenReturn(testBill);

        billingController.handleOrderState(request, response);

        verify(response).setContentType("application/json");
        verify(writer).write(contains("state"));
    }
}