package com.DAO;

import com.entity.Account;

public interface IAccountDAO {
	 void createAccount(Account account);
	  Account getAccountByID(int account_ID);
	  void updateAccount(Account account,String flag,double amount);
	   void deleteAccount(int account_ID);

}
