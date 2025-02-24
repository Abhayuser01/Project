package com.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.exception.*;
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
    public void updateAccount(Account account ,String flag,double amount) {
    	 if (amount <= 0) {
             System.out.println("Invalid deposit amount.");
             return;
             
         }
    	 
    	if(flag.equals("Deposit")) {
    		 try {
    			 if(account==null) {
    		           	throw new TransactionFailedException("Deposit transaction failed.Account ID not found");
    		           }
    	            // Update balance in Account table
    	            String updateBalanceQuery = "UPDATE Accounts SET Balance = Balance + ? WHERE Account_id = ?";
    	            PreparedStatement balanceStmt = conn.prepareStatement(updateBalanceQuery);
    	            balanceStmt.setDouble(1, amount);
    	            balanceStmt.setInt(2, account.getAccountID());
    	            int rowsUpdated = balanceStmt.executeUpdate();

    	            if (rowsUpdated > 0) {
    	            	System.out.println("Account updated successfully. Deposited by "+amount);
    	            }
    	            
    	        } catch (SQLException e) {
    	            e.printStackTrace();
    	        } catch (TransactionFailedException e) {
					// TODO Auto-generated catch block
    	        	System.out.println(e.getMessage());
				}
    	        
    	    }
    		
    	
    	else if(flag.equals("Withdrawal")){
    		 try {
    			 
    			 if(account==null) {
  	            	throw new TransactionFailedException("Withdrawal transaction failed.Account ID not found");
  	            }
    			 if(account.getBalance()-amount<0) {
    				 throw new InsufficienFund("\u001B[31mWithdrawal amount (" + amount + ") exceeds current balance (" + account.getBalance() + ")\u001B[0m");
    				 
    			 }
 	            // Update balance in Account table
 	            String updateBalanceQuery = "UPDATE Accounts SET Balance = Balance - ? WHERE Account_id = ?";
 	            PreparedStatement balanceStmt = conn.prepareStatement(updateBalanceQuery);
 	            balanceStmt.setDouble(1, amount);
 	            balanceStmt.setInt(2, account.getAccountID());
 	            int rowsUpdated = balanceStmt.executeUpdate();

 	            if (rowsUpdated > 0) {
 	            	System.out.println("Account updated successfully. Withdrawal by "+amount);
 	            }
 	           
 	        } catch (SQLException e) {
 	            e.printStackTrace();
 	        } catch (InsufficienFund e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (TransactionFailedException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
 	        
 	    }
    	else {
    		System.out.println("Invalid Transaction Type");
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
