package com.tasks;

import com.entity.Account;
import com.service.AccountService;

public class TransactionTask implements Runnable {
    private AccountService accountService;
    private Account account;
    private String transactionType;
    private double amount;

    public TransactionTask(AccountService accountService, Account account, String transactionType, double amount) {
        this.accountService = accountService;
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            accountService.updateAccount(account, transactionType, amount);
        } catch (Exception e) {
            System.out.println("\u001B[31mTransaction Failed: " + e.getMessage() + "\u001B[0m");
        }
    }
}
