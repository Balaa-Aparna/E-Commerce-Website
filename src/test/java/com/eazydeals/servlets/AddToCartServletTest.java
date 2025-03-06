package com.eazydeals.servlets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.eazydeals.dao.CartDao;
import com.eazydeals.entities.Cart;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.h2.jdbcx.JdbcDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

@RunWith(MockitoJUnitRunner.class)
public class AddToCartServletTest {

    private DataSource dataSource;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        // Setting up the in-memory H2 database
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:eazydeals;DB_CLOSE_DELAY=-1");
        dataSource = ds;

        // Establishing the connection
        connection = dataSource.getConnection();
        Statement stmt = connection.createStatement();
         
        

        // Run the DDL queries
        stmt.execute("CREATE TABLE IF NOT EXISTS \"user\" (userid INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100), email VARCHAR(45), password VARCHAR(45), phone VARCHAR(20), gender VARCHAR(20), registerdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, address VARCHAR(250), city VARCHAR(100), pincode VARCHAR(10), state VARCHAR(100))");
        stmt.execute("CREATE TABLE IF NOT EXISTS category (cid INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100), image VARCHAR(100))");
        stmt.execute("CREATE TABLE IF NOT EXISTS product (pid INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(250) NOT NULL, description VARCHAR(500), price VARCHAR(20) NOT NULL, quantity INT, discount INT, image VARCHAR(100), cid INT, FOREIGN KEY (cid) REFERENCES category(cid))");
        stmt.execute("CREATE TABLE IF NOT EXISTS cart (id INT PRIMARY KEY AUTO_INCREMENT, uid INT, pid INT, quantity INT, FOREIGN KEY (pid) REFERENCES product(pid), FOREIGN KEY (uid) REFERENCES \"user\"(userid))");
        stmt.execute("CREATE TABLE IF NOT EXISTS admin (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100), email VARCHAR(100), password VARCHAR(50), phone VARCHAR(20))");

        
        

        
        // Insert mock data into the tables
        stmt.execute("INSERT INTO \"user\" (userid, name, email, password, phone, gender, registerdate, address, city, pincode, state) VALUES (1, 'Anirudh Kumar', 'test786@gmail.com', 'abc123', '7546254260', 'Male', '2023-09-24 05:22:11', 'KN nagar', 'Patna', '401980', 'Bihar')");
        stmt.execute("INSERT INTO category (cid, name, image) VALUES (1, 'Mobiles', 'mobiles.jpeg')");
        stmt.execute("INSERT INTO product (pid, name, description, price, quantity, discount, image, cid) VALUES (1, 'SAMSUNG Galaxy F14 5G', 'Some description', '18490.0', 10, 15, 'phone1.jpeg', 1)");
        stmt.execute("INSERT INTO admin (id, name, email, password, phone) VALUES (1, 'Anirudh kumar', 'test@gmail.com', 'abc123', '7755632012')");


    }

    @Test
    public void testAddToCart() throws Exception {
        // Simulate adding a product to the cart using the Cart entity
        int userId = 1; // User ID from inserted mock data
        int productId = 1; // Product ID from inserted mock data
        int quantity = 1; // Quantity of the product

        // Create a Cart object with the above details
        Cart cart = new Cart(userId, productId, quantity);
        
        // Create a mock connection to avoid interacting with a real database
        Connection mockConnection = mock(Connection.class);

        // Create the CartDao instance with the mock connection
        CartDao cartDao = new CartDao(mockConnection);

        // Mock the behavior of the connection to simulate successful execution of the query
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeUpdate()).thenReturn(1);  // Simulate 1 row affected (successful addition)

        // Call the method under test with the Cart object
        boolean result = cartDao.addToCart(cart);
        
        // Assert that the result of adding to the cart is true
        assertTrue(result);

        // Verify that the method was called once
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockStmt, times(1)).executeUpdate();
    }


    @Test
    public void testGetProductFromCart() throws Exception {
        // Insert a product into the cart
        Statement insertStmt = connection.createStatement();
        insertStmt.execute("INSERT INTO cart (uid, pid, quantity) VALUES (1, 1, 1)");
        
        // Test to retrieve product from the cart
        Statement stmt = connection.createStatement();
        
        // Select from the cart table and verify the data
        ResultSet rs = stmt.executeQuery("SELECT * FROM cart WHERE uid = 1");
        
        // Assert that the cart has the correct data
        if (rs.next()) {
            assertEquals(1, rs.getInt("uid"));
            assertEquals(1, rs.getInt("pid"));
            assertEquals(1, rs.getInt("quantity"));
        } else {
            fail("Cart is empty for user with id 1");
        }
    }

    @Test
    public void testProductExistsInDatabase() throws Exception {
        // Test if a product exists in the database
        Statement stmt = connection.createStatement();
        
        // Query the product table
        ResultSet rs = stmt.executeQuery("SELECT * FROM product WHERE pid = 1");
        
        // Check if the product is present
        if (rs.next()) {
            assertEquals("SAMSUNG Galaxy F14 5G", rs.getString("name"));
            assertEquals("18490.0", rs.getString("price"));
            assertEquals(10, rs.getInt("quantity"));
        } else {
            fail("Product not found with id 1");
        }
    }
    
    @After
    public void tearDown() throws Exception {
        // Clean up after each test by truncating the tables to reset the state
        Statement stmt = connection.createStatement();
        stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");


        stmt.execute("TRUNCATE TABLE \"user\"");
        stmt.execute("TRUNCATE TABLE category");
        stmt.execute("TRUNCATE TABLE product");
        stmt.execute("TRUNCATE TABLE cart");
        stmt.execute("TRUNCATE TABLE admin");

        stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");



        // Close the connection to the in-memory H2 database
        connection.close();
    }
}
