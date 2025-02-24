package com.DAO;

import java.util.List;

import com.entity.Transaction;

public interface ITransactionDAO {
	 boolean transferFunds(int fromAccountID, int toAccountID, double amount);

	    // Method to retrieve transaction history for an account
	    List<Transaction> getTransactionHistory(int accountID);

}
