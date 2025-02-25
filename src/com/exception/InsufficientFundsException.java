package com.exception;

public class InsufficientFundsException extends Exception{
	public InsufficientFundsException(String message) {
		super("Insufficient Fund: "+ message);
	}

}
