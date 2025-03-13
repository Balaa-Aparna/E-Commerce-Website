package com.eazydeals.servlets;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.eazydeals.entities.Message;

import java.io.IOException;

class LogoutServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    private LogoutServlet servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new LogoutServlet();
        when(request.getSession()).thenReturn(session);
    }


    @Test
    void testUserLogoutRedirectMustExecute() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("user")).thenReturn("user");
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(session).removeAttribute("activeUser");
        verify(session).setAttribute(eq("message"), any(Message.class));
        
        // Verify sendRedirect with the correct URL
        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(redirectCaptor.capture());
        
        // Ensure the test fails if assertions are removed
        String redirectUrl = redirectCaptor.getValue();
        if (redirectUrl == null || !redirectUrl.equals("login.jsp")) {
            throw new AssertionError("Redirect URL must be login.jsp and not null");
        }
    }
}
