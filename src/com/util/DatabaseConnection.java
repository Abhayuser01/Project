package com.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/BankDatabase";
                String user = "root";
                String password = "rps@123";
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
