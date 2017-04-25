package com.kerneldc.education.studentNotesService.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

/**
 * Class to return Jackson object mapper so that Jersey does not use JAXB (XML annotations)
 * and registers Hibernate5Module with Jackson so that it can handle Hibernate entities and
 * specifically prevent lazy loading exception when serializing proxied objects.
 * 
 * Also disable including all properties in views
 *
 */
@Provider
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {

	final ObjectMapper jacksonObjectMapper;
	 
    public JacksonObjectMapperProvider() {
        jacksonObjectMapper = new ObjectMapper();
        jacksonObjectMapper.registerModule(new Hibernate5Module());
        
        jacksonObjectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    }
 
    @Override
    public ObjectMapper getContext(Class<?> type) {
    	return jacksonObjectMapper;
    }
 
}
