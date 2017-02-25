package com.kerneldc.education.studentNotesService.security.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthorityMixIn {

	public SimpleGrantedAuthorityMixIn(@JsonProperty("authority") String authority) {
	}

}
