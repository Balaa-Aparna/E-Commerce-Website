package com.eazydeals.servlets;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testUserLogout() throws ServletException, IOException {
        when(request.getParameter("user")).thenReturn("user");

        servlet.doGet(request, response);

        verify(session).removeAttribute("activeUser");
        verify(session).setAttribute(eq("message"), any(Message.class));
        verify(response).sendRedirect("login.jsp");
    }
}
