package com.service;

import com.DAO.ITransactionDAO;
import com.entity.Transaction;
import com.exception.TransactionFailedException;
import com.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
            System.out.println("\u001B[31mInvalid transfer amount.\u001B[0m"); // Red error
            return false;
        }

        try {
            conn.setAutoCommit(false); // Move inside try block for safety

            // Check sender's balance
            String checkBalanceQuery = "SELECT Balance FROM Accounts WHERE Account_ID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkBalanceQuery);
            checkStmt.setInt(1, fromAccountID);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                throw new TransactionFailedException("Sender account does not exist.");
            }

            double senderBalance = rs.getDouble("Balance");
            if (senderBalance < amount) {
                throw new TransactionFailedException("Insufficient balance in sender's account.");
            }

            // Check receiver account existence
            checkStmt.setInt(1, toAccountID);
            ResultSet rs2 = checkStmt.executeQuery();
            if (!rs2.next()) {
                throw new TransactionFailedException("Receiver account does not exist.");
            }

            // Deduct from sender
            String deductQuery = "UPDATE Accounts SET Balance = Balance - ? WHERE Account_ID = ?";
            PreparedStatement deductStmt = conn.prepareStatement(deductQuery);
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, fromAccountID);
            int rowsUpdatedSender = deductStmt.executeUpdate();

            if (rowsUpdatedSender == 0) {
                throw new TransactionFailedException("Failed to deduct funds from sender.");
            }

            // Add to receiver
            String addQuery = "UPDATE Accounts SET Balance = Balance + ? WHERE Account_ID = ?";
            PreparedStatement addStmt = conn.prepareStatement(addQuery);
            addStmt.setDouble(1, amount);
            addStmt.setInt(2, toAccountID);
            int rowsUpdatedReceiver = addStmt.executeUpdate();

            if (rowsUpdatedReceiver == 0) {
                throw new TransactionFailedException("Failed to add funds to receiver.");
            }

            // Record transactions
            String transactionQuery = "INSERT INTO Transactions (Account_ID, Amount, Transaction_Type, Transaction_date) VALUES (?, ?, ?, ?)";
            PreparedStatement transactionStmt = conn.prepareStatement(transactionQuery);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            // Sender transaction (Withdrawal)
            transactionStmt.setInt(1, fromAccountID);
            transactionStmt.setDouble(2, -amount);
            transactionStmt.setString(3, "Withdrawal");
            transactionStmt.setTimestamp(4, timestamp);
            transactionStmt.executeUpdate();

            // Receiver transaction (Deposit)
            transactionStmt.setInt(1, toAccountID);
            transactionStmt.setDouble(2, amount);
            transactionStmt.setString(3, "Deposit");
            transactionStmt.setTimestamp(4, timestamp);
            transactionStmt.executeUpdate();

            conn.commit();
            System.out.println("\u001B[32mTransfer successful!\u001B[0m"); // Green text for success
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("\u001B[31mSQL Error: " + e.getMessage() + "\u001B[0m");
            return false;
        } catch (TransactionFailedException e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m"); // Red text for failure
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
    public List<Transaction> getMonthlyStatement(int accountID) {
        List<Transaction> allTransactions = getTransactionHistory(accountID);
        
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1);
        
        return allTransactions.stream()
            .filter(t -> t.getTimestamp().toLocalDateTime().isAfter(firstDayOfMonth))
            .collect(Collectors.toList());
    }


}
