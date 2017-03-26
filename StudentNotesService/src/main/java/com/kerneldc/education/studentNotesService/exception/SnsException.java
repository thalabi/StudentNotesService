package com.kerneldc.education.studentNotesService.exception;

public class SnsException extends Exception {

	private static final long serialVersionUID = 1L;

	public SnsException() {
	}

	public SnsException(String message) {
		super(message);
	}

	public SnsException(Throwable cause) {
		super(cause);
	}

	public SnsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SnsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
