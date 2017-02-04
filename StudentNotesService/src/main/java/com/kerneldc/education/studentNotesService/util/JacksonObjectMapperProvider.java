package com.kerneldc.education.studentNotesService.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to return Jackson object mapper so that Jersey does not use JAXB (XML annotations)
 *
 */
@Provider
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {

	final ObjectMapper jacksonObjectMapper;
	 
    public JacksonObjectMapperProvider() {
        jacksonObjectMapper = new ObjectMapper();
    }
 
    @Override
    public ObjectMapper getContext(Class<?> type) {
    	return jacksonObjectMapper;
    }
 
}
