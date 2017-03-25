package com.kerneldc.education.studentNotesService.exception;

public class SnRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SnRuntimeException() {
	}

	public SnRuntimeException(String message) {
		super(message);
	}

	public SnRuntimeException(Throwable cause) {
		super(cause);
	}

	public SnRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SnRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
