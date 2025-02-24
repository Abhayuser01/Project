package com.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.DAO.IAccountDAO;
import com.entity.Account;
import com.util.DatabaseConnection;

public class AccountService implements IAccountDAO {
	 private Connection conn;
	 public  AccountService() {
	        conn = DatabaseConnection.getConnection();
	    }

    @Override
    public void createAccount(Account account) {
        String query = "INSERT INTO Accounts (Customer_ID, Account_Type, Balance) VALUES (?, ?, ?)";

        try (
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, account.getCustomerID());
            pstmt.setString(2, account.getAccountType());
            pstmt.setDouble(3, account.getBalance());

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        account.setAccountID(generatedKeys.getInt(1));
                    }
                }
               
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve an account by its ID
    @Override
    public Account getAccountByID(int Account_id) {
        String query = "SELECT * FROM Accounts WHERE Account_ID = ?";
        Account account = null;

        try (
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Account_id);
            ResultSet rs = pstmt.executeQuery();//executeQuery use when select is used

            if (rs.next()) {//rs.next() executed when executeQuery returns a resule
                account = new Account(
                        rs.getInt("Account_id"),
                        rs.getInt("customer_ID"),
                        rs.getString("account_Type"),
                        rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }

    
  

    // Update an account's details (e.g., balance)
    @Override
    public void updateAccount(Account account) {
        String query = "UPDATE Accounts SET Balance = ? WHERE Account_ID = ?";

        try (
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, account.getBalance());
            pstmt.setInt(2, account.getAccountID());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Account updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete an account
    @Override
    public void deleteAccount(int account_ID) {
        String query = "DELETE FROM Accounts WHERE Account_ID = ?";

        try (
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, account_ID);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Account deleted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
