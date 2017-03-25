package com.kerneldc.education.studentNotesService.exception.ResponseStatus;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

public class AbstractResponseStatusType implements StatusType {

	public AbstractResponseStatusType(final Family family, final int statusCode, final String reasonPhrase) {
		super();

		this.family = family;
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	protected AbstractResponseStatusType(final Status status, final String reasonPhrase) {
		this(status.getFamily(), status.getStatusCode(), reasonPhrase);
	}

	@Override
	public Family getFamily() {
		return family;
	}

	@Override
	public String getReasonPhrase() {
		return reasonPhrase;
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	private final Family family;
	private final int statusCode;
	private final String reasonPhrase;
}
