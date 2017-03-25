package com.kerneldc.education.studentNotesService.exception.ResponseStatus;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

public class SnResponseStatusType extends AbstractResponseStatusType {

	public SnResponseStatusType(Family family, int statusCode, String reasonPhrase) {
		super(family, statusCode, reasonPhrase);
	}

	public SnResponseStatusType(Status status, String reasonPhrase) {
		super(status, reasonPhrase);
	}

}
