package com.kerneldc.education.studentNotesService.exception;

public class RowNotFoundException extends SnException {

	private static final long serialVersionUID = 1L;

	public RowNotFoundException() {
	}

	public RowNotFoundException(String message) {
		super(message);
	}

	public RowNotFoundException(Throwable cause) {
		super(cause);
	}

	public RowNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RowNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
