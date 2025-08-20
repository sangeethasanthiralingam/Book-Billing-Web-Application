package dao;

import model.Book;
import util.DBConnection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookDAO Tests")
class BookDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private BookDAO bookDAO;

    private Book testBook;

    private static MockedStatic<DBConnection> mockedDBConnection;

    @BeforeAll
    static void beforeAll() {
        // Mock the static DBConnection.getInstance() once for all tests
        mockedDBConnection = mockStatic(DBConnection.class);
    }

    @AfterAll
    static void afterAll() {
        mockedDBConnection.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        testBook = new Book(1, "Test Title", "Test Author", "123456", 9.99, 10, "Fiction");

        DBConnection mockInstance = mock(DBConnection.class);
        mockedDBConnection.when(DBConnection::getInstance).thenReturn(mockInstance);
        when(mockInstance.getConnection()).thenReturn(mockConnection);
    }

    @Test
    @DisplayName("Should save book successfully")
    void testSaveBook() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = bookDAO.save(testBook);

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, testBook.getTitle());
        verify(mockPreparedStatement).setString(2, testBook.getAuthor());
        verify(mockPreparedStatement).setString(3, testBook.getIsbn());
        verify(mockPreparedStatement).setDouble(4, testBook.getPrice());
        verify(mockPreparedStatement).setInt(5, testBook.getQuantity());
        verify(mockPreparedStatement).setString(6, testBook.getCategory());
    }

    @Test
    @DisplayName("Should handle database exception gracefully")
    void testSaveBookWithException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Connection failed"));

        assertThrows(RuntimeException.class, () -> bookDAO.save(testBook));
    }

    @Test
    @DisplayName("Should find book by ID")
    void testFindById() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(testBook.getId());
        when(mockResultSet.getString("title")).thenReturn(testBook.getTitle());
        when(mockResultSet.getString("author")).thenReturn(testBook.getAuthor());
        when(mockResultSet.getString("isbn")).thenReturn(testBook.getIsbn());
        when(mockResultSet.getDouble("price")).thenReturn(testBook.getPrice());
        when(mockResultSet.getInt("quantity")).thenReturn(testBook.getQuantity());
        when(mockResultSet.getString("category")).thenReturn(testBook.getCategory());

        Book result = bookDAO.findById(testBook.getId());

        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals(testBook.getAuthor(), result.getAuthor());
    }

    @Test
    @DisplayName("Should return null when book not found")
    void testFindByIdNotFound() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Book result = bookDAO.findById(999);

        assertNull(result);
    }

    @Test
    @DisplayName("Should update book successfully")
    void testUpdateBook() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = bookDAO.update(testBook);

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, testBook.getTitle());
        verify(mockPreparedStatement).setInt(7, testBook.getId());
    }

    @Test
    @DisplayName("Should delete book successfully")
    void testDeleteBook() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = bookDAO.delete(testBook.getId());

        assertTrue(result);
        verify(mockPreparedStatement).setInt(1, testBook.getId());
    }

    @Test
    @DisplayName("Should search books by title")
    void testSearchBooks() throws SQLException {
        String searchTerm = "Test";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(testBook.getId());
        when(mockResultSet.getString("title")).thenReturn(testBook.getTitle());
        when(mockResultSet.getString("author")).thenReturn(testBook.getAuthor());

        List<Book> results = bookDAO.searchBooks(searchTerm);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testBook.getTitle(), results.get(0).getTitle());
    }
}
