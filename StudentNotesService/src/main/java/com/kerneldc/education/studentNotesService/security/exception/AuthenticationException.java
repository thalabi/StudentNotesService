package com.kerneldc.education.studentNotesService.security.exception;

import com.kerneldc.education.studentNotesService.exception.SnsException;

public class AuthenticationException extends SnsException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException() {
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
