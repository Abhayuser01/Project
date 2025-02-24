package com.service;

import com.DAO.ITransactionDAO;
import com.entity.Transaction;
import com.exception.TransactionFailedException;
import com.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionService implements ITransactionDAO {
    private Connection conn;
   
    public HashMap<Integer, Double> accountBalanceMap; // Fast lookup map
// Niti Mam here hashMap is public but it would be  better to use private specifier here
  
    public TransactionService() {
        this.conn = DatabaseConnection.getConnection();
        this.accountBalanceMap = new HashMap<>();
        loadAccountBalances(); // Load balances on startup
    }
    
    private void loadAccountBalances() {
        String query = "SELECT Customer_id, Balance FROM Accounts";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int customerId = rs.getInt("Customer_id");
                double balance = rs.getDouble("Balance");
                accountBalanceMap.put(customerId, balance);
            }
            System.out.println("Account balances loaded successfully.");
        } catch (SQLException e) {
            System.out.println("Error loading account balances: " + e.getMessage());
        }
    }

    // Function to handle fund transfer between two accounts
    public boolean transferFunds(int fromAccountID, int toAccountID, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid transfer amount.");
            return false;
        }

        try {
            conn.setAutoCommit(false);

            // Check sender's balance
            String checkBalanceQuery = "SELECT Balance FROM Accounts WHERE Account_ID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkBalanceQuery);
            checkStmt.setInt(1, fromAccountID);
            ResultSet rs = checkStmt.executeQuery();
            
            if(!rs.next()) {
            	throw new TransactionFailedException("Account not exist");
            }


            if (!rs.next() || rs.getDouble("Balance") < amount) {
                System.out.println("Insufficient balance.");
                conn.rollback();
                return false;
            }
          

            // Deduct from sender
            String deductQuery = "UPDATE Accounts SET Balance = Balance - ? WHERE Account_ID = ?";
            PreparedStatement deductStmt = conn.prepareStatement(deductQuery);
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, fromAccountID);
            deductStmt.executeUpdate();

            // Add to receiver
            String addQuery = "UPDATE Accounts SET Balance = Balance + ? WHERE Account_ID = ?";
            PreparedStatement addStmt = conn.prepareStatement(addQuery);
            addStmt.setDouble(1, amount);
            addStmt.setInt(2, toAccountID);
            addStmt.executeUpdate();

            // Record transaction
            String transactionQuery = "INSERT INTO Transactions (Account_ID, Amount, Transaction_Type, Transaction_date) VALUES (?, ?, ?, ?)";
            PreparedStatement transactionStmt = conn.prepareStatement(transactionQuery);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            transactionStmt.setInt(1, fromAccountID);
            transactionStmt.setDouble(2, -amount);
            transactionStmt.setString(3, "Withdrawal");
            transactionStmt.setTimestamp(4, timestamp);
            transactionStmt.executeUpdate();

            transactionStmt.setInt(1, toAccountID);
            transactionStmt.setDouble(2, amount);
            transactionStmt.setString(3, "Deposit");
            transactionStmt.setTimestamp(4, timestamp);
            transactionStmt.executeUpdate();

            conn.commit();
            System.out.println("Transfer successful.");
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } catch (TransactionFailedException e) {
			// TODO Auto-generated catch block
        	System.out.println(e.getMessage());
			return false;
		} finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to retrieve transaction history
    public List<Transaction> getTransactionHistory(int accountID) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM Transactions WHERE Account_ID = ? ORDER BY Transaction_date DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("Transaction_ID"),
                        rs.getInt("Account_ID"),
                        rs.getDouble("Amount"),
                        rs.getString("Transaction_type"),
                        rs.getTimestamp("Transaction_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
