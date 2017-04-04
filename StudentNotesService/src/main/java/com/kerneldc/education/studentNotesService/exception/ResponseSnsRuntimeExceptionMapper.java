package com.kerneldc.education.studentNotesService.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kerneldc.education.studentNotesService.constants.Constants;
import com.kerneldc.education.studentNotesService.exception.ResponseStatus.SnResponseStatusType;

@Provider
public class ResponseSnsRuntimeExceptionMapper implements ExceptionMapper<SnsRuntimeException> {

	@Override
	public Response toResponse(SnsRuntimeException exception) {
		ObjectNode errorMessageJson = JsonNodeFactory.instance.objectNode();
		errorMessageJson.put("errorMessage", exception.getMessage());
		SnResponseStatusType status = new SnResponseStatusType(
				Family.SERVER_ERROR, Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE,
				Constants.SN_EXCEPTION_RESPONSE_REASON_PHRASE);
		return Response.status(status).entity(errorMessageJson).build();
	}

}
