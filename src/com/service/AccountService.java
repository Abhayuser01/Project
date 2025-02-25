package com.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


import com.DAO.IAccountDAO;
import com.entity.Account;

import com.util.DatabaseConnection;

public class AccountService implements IAccountDAO {
	 private Connection conn;
	 private static final ConcurrentHashMap<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();
	 
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

    
 // Get Lock for a specific account
    private ReentrantLock getLockForAccount(int accountID) {
        return accountLocks.computeIfAbsent(accountID, k -> new ReentrantLock());// here k is not used but it is required to write This is because the key (accountID) is already used internally by computeIfAbsent() to add the value.
    }

    // Update an account's details (e.g., balance)
    @Override
  
    public void updateAccount(Account account, String flag, double amount) {
        if (amount <= 0) {
            System.err.println("\u001B[31mInvalid transaction amount.\u001B[0m"); // Red error
            return;
        }

        if (account == null) {
            System.err.println("Transaction failed. Account ID not found.");
            return; // Prevents execution of further code
        }

        int accountID = account.getAccountID();
        ReentrantLock lock = getLockForAccount(accountID);

        lock.lock(); // Ensure only one thread modifies the account balance at a time
        try {
            conn.setAutoCommit(false); // Start transaction

            if (flag.equalsIgnoreCase("Deposit")) {
                // Update balance in Database
                String updateBalanceQuery = "UPDATE Accounts SET Balance = Balance + ? WHERE Account_id = ?";
                PreparedStatement balanceStmt = conn.prepareStatement(updateBalanceQuery);
                balanceStmt.setDouble(1, amount);
                balanceStmt.setInt(2, accountID);
                int rowsUpdated = balanceStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    conn.commit(); // Commit transaction
                    System.out.println("Deposit successful. Amount: ₹" + amount);
                    return;
                } else {
                    System.err.println("Deposit failed. Account update unsuccessful.");
                }
            } 
            else if (flag.equalsIgnoreCase("Withdrawal")) {
                // Check balance before withdrawal
                String checkBalanceQuery = "SELECT Balance FROM Accounts WHERE Account_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkBalanceQuery);
                checkStmt.setInt(1, accountID);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.err.println("Withdrawal failed. Account ID not found.");
                    return; // Prevents further execution
                }

                double currentBalance = rs.getDouble("Balance");

                if (currentBalance < amount) {
                    System.err.println("Withdrawal amount exceeds available balance.");
                    return; // Prevents further execution
                }

                // Deduct amount from database
                String updateBalanceQuery = "UPDATE Accounts SET Balance = Balance - ? WHERE Account_id = ?";
                PreparedStatement balanceStmt = conn.prepareStatement(updateBalanceQuery);
                balanceStmt.setDouble(1, amount);
                balanceStmt.setInt(2, accountID);
                int rowsUpdated = balanceStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    conn.commit();
                    System.out.println("Withdrawal successful. Amount: ₹" + amount);
                    return;
                } else {
                    System.err.println("Withdrawal failed. Account update unsuccessful.");
                }
            } 
            else {
                System.err.println("Invalid transaction type: " + flag);
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.err.println("\u001B[31mDatabase Error: " + e.getMessage() + "\u001B[0m");
        } finally {
            lock.unlock(); // Release the lock after transaction
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
