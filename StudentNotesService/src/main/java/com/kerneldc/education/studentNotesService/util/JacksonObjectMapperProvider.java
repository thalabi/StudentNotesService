package com.kerneldc.education.studentNotesService.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * Class to return Jackson object mapper configured to use JAXB annotations first and Jackson annotations second
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
        jacksonObjectMapper.registerModule(getJaxbAnnotationModule());
        jacksonObjectMapper.registerModule(new Hibernate5Module());
        
        jacksonObjectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    }
 
    private JaxbAnnotationModule getJaxbAnnotationModule() {
        JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule();
        jaxbAnnotationModule.setPriority(JaxbAnnotationModule.Priority.SECONDARY);
        return jaxbAnnotationModule;
    }
    
    @Override
    public ObjectMapper getContext(Class<?> type) {
    	return jacksonObjectMapper;
    }
 
}
