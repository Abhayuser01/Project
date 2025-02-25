package com.main;

import java.util.List;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.DAO.IAccountDAO;
import com.DAO.ICustomerDAO;
import com.DAO.ITransactionDAO;
import com.entity.Account;
import com.entity.Customer;
import com.entity.Transaction;
import com.service.AccountService;
import com.service.CustomerService;
import com.service.TransactionService;
import com.tasks.TransactionTask;

public class BankingApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final IAccountDAO accountDAO = new AccountService();
    private static final ICustomerDAO customerDAO = new CustomerService();
    private static final ITransactionDAO transactionDAO = new TransactionService();
    private static final AccountService accountService = new AccountService();
    private static final ExecutorService executor = Executors.newFixedThreadPool(5); // Thread pool for parallel transactions

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== Banking System Menu =====");
            System.out.println("1. Create Account");
            System.out.println("2. View Account by ID");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdrawal Money");

            System.out.println("5. Delete Account");
            System.out.println("6. Transfer Funds");
            System.out.println("7. View Transaction History");
            System.out.println("8. View Customer Details");
            System.out.println("9. Fast Lookup of Balance By HashMap");
            System.out.println("10. Generate Monthly Statement");
            System.out.println("11. Exit");
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
                    depositFunds();
                    break;
                case 4:
                    withdrawFunds();
                    break;
                
                case 5:
                    deleteAccount();
                    break;
                case 6:
                    FundsTransfer();
                    break;
                case 7:
                    viewTransactionHistory();
                    break;
                case 8:
                    viewCustomerDetails();
                    break;
                case 9:
                	viewAccountBalance();
                	break;
                case 10:
                    generateMonthlyStatement();
                    break;
                case 11:
                    System.out.println("Exiting the banking system. Thank you!");
                    scanner.close();
                    System.exit(0);
                
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
        int accountID = scanner.nextInt();

        Account account = accountDAO.getAccountByID(accountID);
        if (account != null) {
            System.out.println("Account Details: " + account);
        } else {
            System.out.println("Account not found!");
        }
    }

//    private static void updateAccountBalance() {
//    	System.out.print("Enter Transaction type Withdrawal/Deposit: ");
//    	String flag=scanner.next();
//        System.out.print("Enter Account ID: ");
//        int accountID = scanner.nextInt();
//
//        Account account = accountDAO.getAccountByID(accountID);
//       
//  
//            System.out.print("Enter Amount: ");
//            double amount = scanner.nextDouble();
//            
//            accountDAO.updateAccount(account,flag,amount);
//       
//    }
    private static void depositFunds() {
        System.out.print("Enter Account ID: ");
        int accountID = scanner.nextInt();
        System.out.print("Enter Amount to Deposit: ");
        double amount = scanner.nextDouble();

        Account account = accountService.getAccountByID(accountID);
        if (account != null) {
        	Future<?> future = executor.submit(new TransactionTask(accountService, account, "Deposit", amount));
            waitForTransaction(future);
        } else {
            System.out.println("\u001B[31mAccount not found!\u001B[0m");
        }
    }

    private static void withdrawFunds() {
        System.out.print("Enter Account ID: ");
        int accountID = scanner.nextInt();
        System.out.print("Enter Amount to Withdraw: ");
        double amount = scanner.nextDouble();

        Account account = accountService.getAccountByID(accountID);
        if (account != null) {
        	 Future<?> future = executor.submit(new TransactionTask(accountService, account, "Withdrawal", amount));
             waitForTransaction(future);
        }
         else {
            System.out.println("\u001B[31mAccount not found!\u001B[0m");
        }
    }
        private static void waitForTransaction(Future<?> future) {
            try {
                future.get(); // Waits for the transaction to complete before continuing
            } catch (Exception e) {
                System.err.println("Transaction error: " + e.getMessage());
            }
        }

    private static void deleteAccount() {
        System.out.print("Enter Account ID: ");
        int accountID = scanner.nextInt();

        Account account = accountDAO.getAccountByID(accountID);
        if (account != null) {
            accountDAO.deleteAccount(accountID);
           
        } else {
            System.out.println("Account not found!");
        }
    }

    private static void  FundsTransfer() {
        System.out.print("Enter Sender Account ID: ");
        int fromAccountID = scanner.nextInt();

        System.out.print("Enter Receiver Account ID: ");
        int toAccountID = scanner.nextInt();

        System.out.print("Enter Amount to Transfer: ");
        double amount = scanner.nextDouble();

        boolean success = transactionDAO.transferFunds(fromAccountID, toAccountID, amount);
        if (success) {
            System.out.println("Funds transferred successfully.");
        } else {
            System.err.println("Fund transfer failed. Please check balance and Receiver account details.");
        }
    }

    private static void viewTransactionHistory() {
        System.out.print("Enter Account ID: ");
        int accountID = scanner.nextInt();

        List<Transaction> transactions = transactionDAO.getTransactionHistory(accountID);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this account.");
        } else {
            System.out.println("Transaction History:");
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    private static void viewCustomerDetails() {
        System.out.print("Enter Customer ID: ");
        int customerID = scanner.nextInt();

        Customer customer = customerDAO.getCustomerByID(customerID);
        if (customer != null) {
            System.out.println("Customer Details: " + customer);
        } else {
            System.out.println("Customer not found!");
        }
    }
    private static void viewAccountBalance() {
    	TransactionService t=new TransactionService();
        System.out.print("Enter Customer ID: ");
        int customerID = scanner.nextInt();

        // Retrieve balance from HashMap instead of querying the database
        if (t.accountBalanceMap.containsKey(customerID)) {
            double balance = t.accountBalanceMap.get(customerID);
            System.out.println("Current Account Balance: ₹" + balance);
        } else {
            System.out.println("Account not found!");
        }
    }
    private static void generateMonthlyStatement() {
        System.out.print("Enter Account ID: ");
        int accountID = scanner.nextInt();
        
        List<Transaction> statement = transactionDAO.getMonthlyStatement(accountID);
        
        if (statement.isEmpty()) {
            System.out.println("No transactions found for this month.");
        } else {
            System.out.println("Monthly Statement:");
            statement.forEach(System.out::println);
        }
    }
}
