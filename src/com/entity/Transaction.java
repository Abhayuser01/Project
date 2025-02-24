package com.entity;

import java.sql.Timestamp;

public class Transaction {
	  private int transactionID;
	    private int accountID;
	    private double amount;
	    private String type; // "deposit" or "withdrawal"
	    private Timestamp timestamp;

	    // Constructors
	    public Transaction() {}

	    public Transaction(int transactionID, int accountID, double amount, String type, Timestamp timestamp) {
	        this.transactionID = transactionID;
	        this.accountID = accountID;
	        this.amount = amount;
	        this.type = type;
	        this.timestamp = timestamp;
	    }

	    // Getters and Setters
	    public int getTransactionID() {
	        return transactionID;
	    }

	    public void setTransactionID(int transactionID) {
	        this.transactionID = transactionID;
	    }

	    public int getAccountID() {
	        return accountID;
	    }

	    public void setAccountID(int accountID) {
	        this.accountID = accountID;
	    }

	    public double getAmount() {
	        return amount;
	    }

	    public void setAmount(double amount) {
	        this.amount = amount;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public Timestamp getTimestamp() {
	        return timestamp;
	    }

	    public void setTimestamp(Timestamp timestamp) {
	        this.timestamp = timestamp;
	    }

	    // toString Method
	    @Override
	    public String toString() {
	        return "Transaction{" +
	                "transactionID=" + transactionID +
	                ", accountID=" + accountID +
	                ", amount=" + amount +
	                ", type='" + type + '\'' +
	                ", timestamp=" + timestamp +
	                '}';
	    }

}
