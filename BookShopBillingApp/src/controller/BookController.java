package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import dao.BookDAO;
import model.Book;

/**
 * Controller for book-related operations
 */
public class BookController extends BaseController {
    
    /**
     * Handle book listing with search and pagination
     */
    public void handleBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            String search = getParameter(request, "search", "");
            int page = getIntParameter(request, "page", 1);
            int pageSize = 10;

            List<Book> books = bookDAO.getAllBooks();
            List<Book> filtered = filterBooks(books, search);

            // Pagination
            int totalBooks = filtered.size();
            int totalPages = (int) Math.ceil((double) totalBooks / pageSize);
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalBooks);
            
            List<Book> pagedBooks = filtered.subList(
                fromIndex < totalBooks ? fromIndex : 0, toIndex);

            // Set attributes
            request.setAttribute("books", pagedBooks);
            request.setAttribute("search", search);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalBooks", totalBooks);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading books");
        }
        request.getRequestDispatcher("/jsp/books.jsp").forward(request, response);
    }
    
    /**
     * Handle book search with advanced filters
     */
    public void handleSearchBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            
            String searchTerm = getParameter(request, "search", "");
            String category = getParameter(request, "category", "");
            String language = getParameter(request, "language", "");
            String publisher = getParameter(request, "publisher", "");
            double minPrice = getDoubleParameter(request, "minPrice", 0.0);
            double maxPrice = getDoubleParameter(request, "maxPrice", Double.MAX_VALUE);

            List<Book> allBooks = bookDAO.getAllBooks();
            List<Book> filteredBooks = filterBooksAdvanced(allBooks, searchTerm, category, 
                                                          language, publisher, minPrice, maxPrice);

            request.setAttribute("books", filteredBooks);
            request.setAttribute("search", searchTerm);
            request.setAttribute("category", category);
            request.setAttribute("language", language);
            request.setAttribute("publisher", publisher);
            request.setAttribute("minPrice", request.getParameter("minPrice"));
            request.setAttribute("maxPrice", request.getParameter("maxPrice"));
            request.setAttribute("totalBooks", filteredBooks.size());

        } catch (Exception e) {
            handleException(request, response, e, "searching books");
        }
        request.getRequestDispatcher("/jsp/books.jsp").forward(request, response);
    }
    
    /**
     * Handle adding a new book (Admin only)
     */
    public void handleAddBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can add books.");
            return;
        }
        
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                Book book = createBookFromRequest(request);
                BookDAO bookDAO = new BookDAO();
                boolean success = bookDAO.addBook(book);
                
                if (success) {
                    request.setAttribute("success", "Book added successfully.");
                } else {
                    request.setAttribute("error", "Failed to add book.");
                }
            } catch (Exception e) {
                handleException(request, response, e, "adding book");
            }
        }
        request.getRequestDispatcher("/jsp/add-book.jsp").forward(request, response);
    }
    
    /**
     * Handle editing a book (Admin only)
     */
    public void handleEditBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can edit books.");
            return;
        }
        
        BookDAO bookDAO = new BookDAO();
        String idParam = request.getParameter("id");
        
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/controller/books");
            return;
        }
        
        int bookId = Integer.parseInt(idParam);
        
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                Book book = bookDAO.getBookById(bookId);
                if (book != null) {
                    updateBookFromRequest(book, request);
                    boolean updated = bookDAO.updateBook(book);
                    
                    if (updated) {
                        request.setAttribute("success", "Book updated successfully.");
                    } else {
                        request.setAttribute("error", "Failed to update book.");
                    }
                }
            } catch (Exception e) {
                handleException(request, response, e, "updating book");
            }
        }
        
        Book book = bookDAO.getBookById(bookId);
        request.setAttribute("book", book);
        request.getRequestDispatcher("/jsp/edit-book.jsp").forward(request, response);
    }
    
    /**
     * Handle deleting a book (Admin only)
     */
    public void handleDeleteBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can delete books.");
            return;
        }
        
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int bookId = Integer.parseInt(idParam);
                BookDAO bookDAO = new BookDAO();
                bookDAO.deleteBook(bookId);
            } catch (Exception e) {
                handleException(request, response, e, "deleting book");
            }
        }
        response.sendRedirect(request.getContextPath() + "/controller/books");
    }
    
    /**
     * Handle viewing a single book
     */
    public void handleViewBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BookDAO bookDAO = new BookDAO();
        String idParam = request.getParameter("id");
        
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/controller/books");
            return;
        }
        
        try {
            int bookId = Integer.parseInt(idParam);
            Book book = bookDAO.getBookById(bookId);
            request.setAttribute("book", book);
        } catch (Exception e) {
            handleException(request, response, e, "viewing book");
        }
        
        request.getRequestDispatcher("/jsp/view-book.jsp").forward(request, response);
    }
    
    /**
     * Handle store page (for customers)
     */
    public void handleStore(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            
            // Collect unique categories
            java.util.Set<String> categories = new java.util.HashSet<>();
            for (Book book : books) {
                if (book.getCategory() != null && !book.getCategory().trim().isEmpty()) {
                    categories.add(book.getCategory());
                }
            }
            
            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading store");
        }
        request.getRequestDispatcher("/jsp/store.jsp").forward(request, response);
    }
    
    // Helper methods
    
    private List<Book> filterBooks(List<Book> books, String search) {
        List<Book> filtered = new ArrayList<>();
        for (Book book : books) {
            boolean matches = true;
            if (search != null && !search.trim().isEmpty()) {
                String s = search.trim().toLowerCase();
                matches &= book.getTitle().toLowerCase().contains(s)
                        || book.getAuthor().toLowerCase().contains(s)
                        || (book.getCategory() != null && book.getCategory().toLowerCase().contains(s));
            }
            if (matches) {
                filtered.add(book);
            }
        }
        return filtered;
    }
    
    private List<Book> filterBooksAdvanced(List<Book> books, String searchTerm, String category, 
                                          String language, String publisher, double minPrice, double maxPrice) {
        List<Book> filtered = new ArrayList<>();
        for (Book book : books) {
            boolean matches = true;

            // Search term filter
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String term = searchTerm.trim().toLowerCase();
                matches &= (book.getTitle() != null && book.getTitle().toLowerCase().contains(term)) ||
                        (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(term)) ||
                        (book.getIsbn() != null && book.getIsbn().toLowerCase().contains(term));
            }

            // Category filter
            if (category != null && !category.trim().isEmpty() && !"ALL".equals(category)) {
                matches &= category.equals(book.getCategory());
            }

            // Language filter
            if (language != null && !language.trim().isEmpty() && !"ALL".equals(language)) {
                matches &= language.equals(book.getLanguage());
            }

            // Publisher filter
            if (publisher != null && !publisher.trim().isEmpty()) {
                matches &= (book.getPublisher() != null
                        && book.getPublisher().toLowerCase().contains(publisher.toLowerCase()));
            }

            // Price range filter
            if (book.getPrice() < minPrice || book.getPrice() > maxPrice) {
                matches = false;
            }

            if (matches) {
                filtered.add(book);
            }
        }
        return filtered;
    }
    
    private Book createBookFromRequest(HttpServletRequest request) {
        Book book = new Book();
        book.setTitle(request.getParameter("title"));
        book.setAuthor(request.getParameter("author"));
        book.setIsbn(request.getParameter("isbn"));
        book.setPrice(getDoubleParameter(request, "price", 0.0));
        book.setQuantity(getIntParameter(request, "quantity", 0));
        book.setCategory(request.getParameter("category"));
        book.setPublisher(request.getParameter("publisher"));
        book.setPublicationYear(getIntParameter(request, "publicationYear", 0));
        book.setLanguage(getParameter(request, "language", "English"));
        return book;
    }
    
    private void updateBookFromRequest(Book book, HttpServletRequest request) {
        book.setTitle(request.getParameter("title"));
        book.setAuthor(request.getParameter("author"));
        book.setIsbn(request.getParameter("isbn"));
        book.setPrice(getDoubleParameter(request, "price", 0.0));
        book.setQuantity(getIntParameter(request, "quantity", 0));
        book.setCategory(request.getParameter("category"));
        book.setPublisher(request.getParameter("publisher"));
        book.setPublicationYear(getIntParameter(request, "publicationYear", 0));
        book.setLanguage(getParameter(request, "language", "English"));
    }
} 