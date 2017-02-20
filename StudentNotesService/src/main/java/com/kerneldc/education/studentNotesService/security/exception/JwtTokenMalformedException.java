package com.kerneldc.education.studentNotesService.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenMalformedException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public JwtTokenMalformedException(String msg) {
		super(msg);
	}

	public JwtTokenMalformedException(String msg, Throwable t) {
		super(msg, t);
	}

}
