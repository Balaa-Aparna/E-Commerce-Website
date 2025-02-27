package com.eazydeals.helper;

import java.sql.Connection;

import java.sql.DriverManager;
import jakarta.servlet.http.HttpServlet;



public class ConnectionProvider extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Connection connection;

	public static Connection getConnection() {

		try {
			if (connection == null) {
				Class.forName("com.mysql.cj.jdbc.Driver");
                String url = System.getenv("DB_URL");
                String user = System.getenv("DB_USER");
                String password = System.getenv("DB_PASSWORD");
				connection = DriverManager.getConnection(url, user, password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

}
