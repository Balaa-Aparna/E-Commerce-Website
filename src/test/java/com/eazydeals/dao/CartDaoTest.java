package com.eazydeals.dao;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

class CartDaoTest {

    private static Connection connection;
    private CartDao cartDao;

    @BeforeAll
    static void setupDatabase() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE cart (id INT PRIMARY KEY, uid INT, pid INT, quantity INT)");
            stmt.execute("INSERT INTO cart (id, uid, pid, quantity) VALUES (1, 101, 201, 2)");
        }
    }

    @BeforeEach
    void setup() {
        cartDao = new CartDao(connection);
    }

    @Test
    void testRemoveProduct() throws Exception {
        // Verify product exists before deletion
        try (Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM cart WHERE id = 1");
            rs.next();
            Assertions.assertEquals(1, rs.getInt(1));
        }

        // Remove product
        boolean result = cartDao.removeProduct(1);
        Assertions.assertTrue(result);

        // Verify product no longer exists
        try (Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM cart WHERE id = 1");
            rs.next();
            Assertions.assertEquals(0, rs.getInt(1));
        }
    }

    @AfterAll
    static void teardownDatabase() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
