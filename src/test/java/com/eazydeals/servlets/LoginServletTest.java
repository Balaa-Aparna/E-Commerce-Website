package com.eazydeals.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import jakarta.servlet.http.*;
import jakarta.servlet.*;

import com.eazydeals.dao.UserDao;
import com.eazydeals.entities.User;
import com.eazydeals.helper.ConnectionProvider;
import com.eazydeals.servlets.LoginServlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

public class LoginServletTest {

    private LoginServlet loginServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setUp() throws Exception {
        loginServlet = new LoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @Test
    public void testUserLogin_Success() throws Exception {
        // Mock input parameters
        when(request.getParameter("login")).thenReturn("user");
        when(request.getParameter("user_email")).thenReturn("test786@gmail.com");
        when(request.getParameter("user_password")).thenReturn("abc123");
        when(request.getSession()).thenReturn(session);

        // Mock UserDao behavior
        UserDao userDao = mock(UserDao.class);
        User user = new User(); // Mock a valid user object
        user.setUserEmail("test786@gmail.com"); // Mock the email
        user.setUserPassword("abc123"); // Mock the password
        when(userDao.getUserByEmailPassword("test786@gmail.com", "abc123")).thenReturn(user);

        // reflection to call the package-private method
        Method doPostMethod = LoginServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class,
                HttpServletResponse.class);
        doPostMethod.setAccessible(true); // Make the method accessible
        doPostMethod.invoke(loginServlet, request, response); // Invoke the method

        // ArgumentCaptor to capture the session attribute
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(session).setAttribute(eq("activeUser"), userCaptor.capture()); // Capture the user object passed to
                                                                              // setAttribute

        // compare the fields (email and password) Manually
        User capturedUser = userCaptor.getValue();
        assertNotNull(capturedUser);
        assertEquals(user.getUserEmail(), capturedUser.getUserEmail()); // Compare the email
        assertEquals(user.getUserPassword(), capturedUser.getUserPassword()); // Compare the password

        verify(response).sendRedirect("index.jsp");
    }

    @Test
    public void testUserLogin_Failure() throws Exception {
        // Mock input parameters
        when(request.getParameter("login")).thenReturn("user");
        when(request.getParameter("user_email")).thenReturn("test786@example.com");
        when(request.getParameter("user_password")).thenReturn("wrongpassword");
        when(request.getSession()).thenReturn(session);

        // Mock UserDao behavior
        UserDao userDao = mock(UserDao.class);
        when(userDao.getUserByEmailPassword("test786@example.com", "wrongpassword")).thenReturn(null);

        // reflection to call the package-private method
        Method doPostMethod = LoginServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class,
                HttpServletResponse.class);
        doPostMethod.setAccessible(true); // Make the method accessible
        doPostMethod.invoke(loginServlet, request, response); // Invoke the method

        // an error message was set in the session Verification
        verify(session).setAttribute(eq("message"), any());
        verify(response).sendRedirect("login.jsp");
    }

    @Test
    public void testWeakPassword() throws Exception {
        // Mock input parameters for weak password (short password)
        when(request.getParameter("login")).thenReturn("user");
        when(request.getParameter("user_email")).thenReturn("test786@gmail.com");
        when(request.getParameter("user_password")).thenReturn("123"); // Weak password
        when(request.getSession()).thenReturn(session);

        // Use reflection to call the package-private doPost method
        Method doPostMethod = LoginServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class,
                HttpServletResponse.class);
        doPostMethod.setAccessible(true); // Make the method accessible
        doPostMethod.invoke(loginServlet, request, response);
        // Verify that an error message is set in the session
        verify(session).setAttribute(eq("message"), any());
        // Verify the redirect to the login page (login.jsp)
        verify(response).sendRedirect("login.jsp");
    }
}
