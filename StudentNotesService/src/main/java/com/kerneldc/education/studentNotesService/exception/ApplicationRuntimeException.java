package com.kerneldc.education.studentNotesService.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ApplicationRuntimeException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public ApplicationRuntimeException(Response response) {
		super(response);
	}
}
