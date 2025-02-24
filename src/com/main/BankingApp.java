package com.main;

import java.util.Scanner;
import com.DAO.IAccountDAO;
import com.service.AccountService;
import com.entity.Account;

public class BankingApp {
	 private static final Scanner scanner = new Scanner(System.in);
	    private static final IAccountDAO accountDAO = new AccountService();

	    public static void main(String[] args) {
	        while (true) {
	            System.out.println("\n===== Banking System Menu =====");
	            System.out.println("1. Create Account");
	            System.out.println("2. View Account by ID");
	           
	            System.out.println("3. Update Account Balance");
	            System.out.println("4. Delete Account");
	            System.out.println("5. Exit");
	            System.out.print("Enter your choice: ");

	            int choice = scanner.nextInt();
	            scanner.nextLine();  // Consume newline

	            switch (choice) {
	                case 1:
	                    createAccount();
	                    break;
	                case 2:
	                    viewAccountByID();
	                    break;
	               
	                case 3:
	                    updateAccountBalance();
	                    break;
	                case 4:
	                    deleteAccount();
	                    break;
	                case 5:
	                    System.out.println("Exiting the banking system. Thank you!");
	                    scanner.close();
	                    System.exit(0);
	                    break;
	                default:
	                    System.out.println("Invalid choice! Please try again.");
	            }
	        }
	    }

	    private static void createAccount() {
	        System.out.print("Enter Customer ID: ");
	        int customerID = scanner.nextInt();
	        scanner.nextLine();  // Consume newline

	        System.out.print("Enter Account Type (Savings/Checking/Loan): ");
	        String accountType = scanner.nextLine();

	        System.out.print("Enter Initial Balance: ");
	        double balance = scanner.nextDouble();

	        Account newAccount = new Account(0, customerID, accountType, balance);
	        accountDAO.createAccount(newAccount);
	        System.out.println("Account created successfully with ID: " + newAccount.getAccountID());
	    }

	    private static void viewAccountByID() {
	        System.out.print("Enter Account ID: ");
	        int Account_id = scanner.nextInt();

	        Account account = accountDAO.getAccountByID(Account_id);
	        if (account != null) {
	            System.out.println("Account Details: " + account);
	        } else {
	            System.out.println("Account not found!");
	        }
	    }

	   

	    private static void updateAccountBalance() {
	        System.out.print("Enter Account ID: ");
	        int accountID = scanner.nextInt();

	        Account account = accountDAO.getAccountByID(accountID);
	        if (account != null) {
	            System.out.print("Enter New Balance: ");
	            double newBalance = scanner.nextDouble();
	            account.setBalance(newBalance);
	            accountDAO.updateAccount(account);
	           
	        } else {
	            System.out.println("Account not found!");
	        }
	    }

	    private static void deleteAccount() {
	        System.out.print("Enter Account ID: ");
	        int accountID = scanner.nextInt();

	        Account account = accountDAO.getAccountByID(accountID);
	        if (account != null) {
	            accountDAO.deleteAccount(accountID);
	            System.out.println("Account deleted successfully.");
	        } else {
	            System.out.println("Account not found!");
	        }
	    }

}
