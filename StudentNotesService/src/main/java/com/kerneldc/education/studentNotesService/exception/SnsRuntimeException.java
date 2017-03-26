package com.kerneldc.education.studentNotesService.exception;

public class SnsRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SnsRuntimeException() {
	}

	public SnsRuntimeException(String message) {
		super(message);
	}

	public SnsRuntimeException(Throwable cause) {
		super(cause);
	}

	public SnsRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SnsRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
