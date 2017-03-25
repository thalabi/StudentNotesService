package com.kerneldc.education.studentNotesService.exception;

public class SnException extends Exception {

	private static final long serialVersionUID = 1L;

	public SnException() {
	}

	public SnException(String message) {
		super(message);
	}

	public SnException(Throwable cause) {
		super(cause);
	}

	public SnException(String message, Throwable cause) {
		super(message, cause);
	}

	public SnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
