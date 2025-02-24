package com.entity;

public class Account {
	  private int accountID;
	    private int customerID;
	    private String accountType;
	    private double balance;

	    // Constructors
	    public Account() {}

	    public Account(int accountID, int customerID, String accountType, double balance) {
	        this.accountID = accountID;
	        this.customerID = customerID;
	        this.accountType = accountType;
	        this.balance = balance;
	    }

	    // Getters and Setters
	    public int getAccountID() {
	        return accountID;
	    }

	    public void setAccountID(int accountID) {
	        this.accountID = accountID;
	    }

	    public int getCustomerID() {
	        return customerID;
	    }

	    public void setCustomerID(int customerID) {
	        this.customerID = customerID;
	    }

	    public String getAccountType() {
	        return accountType;
	    }

	    public void setAccountType(String accountType) {
	        this.accountType = accountType;
	    }

	    public double getBalance() {
	        return balance;
	    }

	    public void setBalance(double balance) {
	        this.balance = balance;
	    }

	    // toString Method
	    @Override
	    public String toString() {
	        return "Account{" +
	                "accountID=" + accountID +
	                ", customerID=" + customerID +
	                ", accountType='" + accountType + '\'' +
	                ", balance=" + balance +
	                '}';
	    }

}
