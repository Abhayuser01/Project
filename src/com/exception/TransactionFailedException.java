package com.exception;

public class TransactionFailedException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionFailedException(String message) {
        super("\u001B[31mTransaction Failed: " + message + "\u001B[0m"); // Red error message
    }
}
