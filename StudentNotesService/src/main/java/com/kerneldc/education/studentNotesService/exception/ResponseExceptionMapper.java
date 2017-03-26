package com.kerneldc.education.studentNotesService.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kerneldc.education.studentNotesService.constants.Constants;
import com.kerneldc.education.studentNotesService.exception.ResponseStatus.SnResponseStatusType;
import com.kerneldc.education.studentNotesService.security.exception.AuthenticationException;

@Provider
public class ResponseExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		if (exception instanceof SnsException || exception instanceof SnsRuntimeException) {
			ObjectNode errorMessageJson = JsonNodeFactory.instance.objectNode();
			errorMessageJson.put("errorMessage", exception.getMessage());
			// if the exception is AuthenticationException, set the status code to UNAUTHORIZED
			// otherwise to SN_EXCEPTION_RESPONSE_STATUS_CODE
			if (exception instanceof AuthenticationException) {
				return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageJson).build();
			} else {
				SnResponseStatusType status = new SnResponseStatusType(
						Family.SERVER_ERROR, Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE,
						Constants.SN_EXCEPTION_RESPONSE_REASON_PHRASE);
				return Response.status(status).entity(errorMessageJson).build();
			}
		} else {
			return null;
		}
	}

}
