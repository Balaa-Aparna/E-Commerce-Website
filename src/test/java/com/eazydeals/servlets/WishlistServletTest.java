package com.eazydeals.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eazydeals.helper.ConnectionProvider;

@ExtendWith(MockitoExtension.class)
class WishlistServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    private WishlistServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new WishlistServlet();
    }

    @Test
    void testDoGet_AddToWishlist() throws ServletException, IOException, SQLException {
        // Arrange
        when(request.getParameter("uid")).thenReturn("1");
        when(request.getParameter("pid")).thenReturn("2");
        when(request.getParameter("op")).thenReturn("add");

        try (MockedStatic<ConnectionProvider> mockedConnectionProvider = mockStatic(ConnectionProvider.class)) {
            mockedConnectionProvider.when(ConnectionProvider::getConnection).thenReturn(connection);

            // Mock the prepareStatement method on the connection
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

            // Mock the executeUpdate method on the preparedStatement
            when(preparedStatement.executeUpdate()).thenReturn(1);

            // Act
            servlet.doGet(request, response);

            // Assert
            verify(response).sendRedirect("products.jsp");
            verify(connection).prepareStatement(anyString());
            verify(preparedStatement).executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testDoGet_RemoveFromWishlist() throws ServletException, IOException, SQLException {
        // Arrange
        when(request.getParameter("uid")).thenReturn("1");
        when(request.getParameter("pid")).thenReturn("2");
        when(request.getParameter("op")).thenReturn("remove");

        try (MockedStatic<ConnectionProvider> mockedConnectionProvider = mockStatic(ConnectionProvider.class)) {
            mockedConnectionProvider.when(ConnectionProvider::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            // Act
            servlet.doGet(request, response);

            // Assert
            verify(response).sendRedirect("products.jsp");
            verify(connection).prepareStatement(anyString());
            verify(preparedStatement).executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testDoGet_DeleteFromWishlist() throws ServletException, IOException, SQLException {
        // Arrange
        when(request.getParameter("uid")).thenReturn("1");
        when(request.getParameter("pid")).thenReturn("2");
        when(request.getParameter("op")).thenReturn("delete");

        try (MockedStatic<ConnectionProvider> mockedConnectionProvider = mockStatic(ConnectionProvider.class)) {
            mockedConnectionProvider.when(ConnectionProvider::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            // Act
            servlet.doGet(request, response);

            // Assert
            verify(response).sendRedirect("profile.jsp");
            verify(connection).prepareStatement(anyString());
            verify(preparedStatement).executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testDoPost() throws ServletException, IOException, SQLException {
        // Arrange
        WishlistServlet spyServlet = spy(servlet);
        doNothing().when(spyServlet).doGet(request, response);

        // Act
        spyServlet.doPost(request, response);

        // Assert
        verify(spyServlet).doGet(request, response);
    }

    @Test
    void testDoGet_InvalidOperation() throws ServletException, IOException, SQLException {
        // Arrange
        when(request.getParameter("uid")).thenReturn("1");
        when(request.getParameter("pid")).thenReturn("2");
        when(request.getParameter("op")).thenReturn("invalid");

        // Act
        servlet.doGet(request, response);

        // Assert
        //If the op is invalid, there is no redirect happening
        verify(response, never()).sendRedirect(anyString());
    }
}
