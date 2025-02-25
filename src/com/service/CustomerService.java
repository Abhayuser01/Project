package com.service;

import com.DAO.ICustomerDAO;
import com.entity.Customer;
import com.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerService implements ICustomerDAO {
    private Connection conn;

    public CustomerService() {
        conn = DatabaseConnection.getConnection();
    }

    @Override
    public Customer getCustomerByID(int customerID) {
        String query = "SELECT * FROM Customers WHERE Customer_ID = ?";
        Customer customer = null;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                customer = new Customer(
                        rs.getInt("Customer_ID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }
}
