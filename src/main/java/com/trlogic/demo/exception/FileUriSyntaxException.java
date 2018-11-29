package com.trlogic.demo.exception;


	
	@SuppressWarnings("serial")
	public class FileUriSyntaxException extends RuntimeException {
	    public FileUriSyntaxException(String message) {
	        super(message);
	    }

	    public FileUriSyntaxException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
	
	

