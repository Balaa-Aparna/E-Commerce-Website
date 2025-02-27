package com.eazydeals.servlets;



import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eazydeals.dao.UserDao;
import com.eazydeals.entities.Message;
import com.eazydeals.helper.ConnectionProvider;
import com.eazydeals.helper.MailMessenger;

@ExtendWith(MockitoExtension.class)
class ChangePasswordServletTest {

	// mock objects for Http request and responses
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
//    @Mock private UserDao userDao;
//    @Mock private ConnectionProvider connectionProvider;


    private ChangePasswordServlet servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new ChangePasswordServlet();
        when(request.getSession()).thenReturn(session);
//        when(ConnectionProvider.getConnection()).thenReturn(mock(Connection.class));
    }

    @Test
    void testForgotPassword_EmailFound() throws ServletException, IOException, SQLException {
        String email = "test786@example.com";
        when(request.getHeader("referer")).thenReturn("forgot_password");
        when(request.getParameter("email")).thenReturn(email);
        
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        
        try (MockedStatic<ConnectionProvider> mockedConnectionProvider = mockStatic(ConnectionProvider.class);
             MockedStatic<MailMessenger> mockedMailMessenger = mockStatic(MailMessenger.class)) {
            
            mockedConnectionProvider.when(ConnectionProvider::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockResultSet.getString(1)).thenReturn(email);

            servlet.doPost(request, response);

            verify(session).setAttribute(eq("otp"), anyInt());
            verify(session).setAttribute("email", email);
            verify(MailMessenger.class);
            MailMessenger.sendOtp(eq(email), anyInt());
            verify(session).setAttribute(eq("message"), any(Message.class));
            verify(response).sendRedirect("otp_code.jsp");
        }
    }

    @Test
    void testForgotPassword_EmailNotFound() throws ServletException, IOException, SQLException {
        when(request.getHeader("referer")).thenReturn("forgot_password");
        when(request.getParameter("email")).thenReturn("nonexistent@example.com");
        
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        
        try (MockedStatic<ConnectionProvider> mockedConnectionProvider = mockStatic(ConnectionProvider.class)) {
            mockedConnectionProvider.when(ConnectionProvider::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
            
            servlet.doPost(request, response);
            
            verify(session).setAttribute(eq("message"), any(Message.class));
            verify(response).sendRedirect("forgot_password.jsp");
        }
    }

    @Test
    void testOtpCode_ValidOtp() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("referer")).thenReturn("otp_code");
        when(request.getParameter("code")).thenReturn("12345");
        when(session.getAttribute("otp")).thenReturn(12345);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(session).removeAttribute("otp");
        verify(response).sendRedirect("change_password.jsp");
    }

    @Test
    void testOtpCode_InvalidOtp() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("referer")).thenReturn("otp_code");
        when(request.getParameter("code")).thenReturn("12345");
        when(session.getAttribute("otp")).thenReturn(54321);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(session).setAttribute(eq("message"), any(Message.class));
        verify(response).sendRedirect("otp_code.jsp");
    }

    @Test
    void testChangePassword() throws ServletException, IOException, SQLException {
        // Arrange
        String email = "test786@example.com";
        String password = "abc123";
        when(request.getHeader("referer")).thenReturn("change_password");
        when(request.getParameter("password")).thenReturn(password);
        when(session.getAttribute("email")).thenReturn(email);

        try (MockedStatic<ConnectionProvider> mockedConnectionProvider = mockStatic(ConnectionProvider.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
            
            mockedConnectionProvider.when(ConnectionProvider::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Act
            servlet.doPost(request, response);

            // Assert
            verify(mockConnection).prepareStatement(anyString());
            verify(mockPreparedStatement).setString(1, password);
            verify(mockPreparedStatement).setString(2, email);
            verify(mockPreparedStatement).executeUpdate();
            verify(session).removeAttribute("email");
            verify(session).setAttribute(eq("message"), any(Message.class));
            verify(response).sendRedirect("login.jsp");
        }
    }

}
