package com.kerneldc.education.studentNotesService.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class RowNotFoundException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public RowNotFoundException(Response response) {
		super(response);
	}
}
