package com.exception;

public class InsufficienFund extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InsufficienFund(String message) {
		super("Insufficient Fund: "+ message);
	}

}
