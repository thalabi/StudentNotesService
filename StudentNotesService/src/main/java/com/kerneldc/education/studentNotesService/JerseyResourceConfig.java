package com.kerneldc.education.studentNotesService;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.kerneldc.education.studentNotesService.exception.ResponseSnsExceptionMapper;
import com.kerneldc.education.studentNotesService.exception.ResponseSnsRuntimeExceptionMapper;
import com.kerneldc.education.studentNotesService.resource.NoteResource;
import com.kerneldc.education.studentNotesService.resource.PrintResource;
import com.kerneldc.education.studentNotesService.resource.SchoolYearResource;
import com.kerneldc.education.studentNotesService.resource.StudentNotesResource;
import com.kerneldc.education.studentNotesService.resource.UserPreferenceResource;
import com.kerneldc.education.studentNotesService.security.resource.SecurityResource;
import com.kerneldc.education.studentNotesService.util.JacksonObjectMapperProvider;

public class JerseyResourceConfig extends ResourceConfig {

	private static final int JERSEY_LOGGING_MAX_ENTITY_SIZE = 2048;
	public JerseyResourceConfig() {

		// Create Jersey configuration with Jackson object mapper provider
		super (/*JacksonObjectMapperProvider.class*/);

		// Logging
		register(new LoggingFeature(
				java.util.logging.Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
				java.util.logging.Level.INFO,
				LoggingFeature.Verbosity.PAYLOAD_TEXT,
				JERSEY_LOGGING_MAX_ENTITY_SIZE));
		
		// Specifying a package does not work due to Jersey classpath scanning limitations
		// See: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-1.4-Release-Notes#jersey-classpath-scanning-limitations
		// Listing of Jersey resources is required as a workaround
		//
		//packages(true, "com.kerneldc.HeroServiceSpringBoot.resource");
		register(StudentNotesResource.class);
		register(SecurityResource.class);
		register(SchoolYearResource.class);
		register(UserPreferenceResource.class);
		
		register(NoteResource.class);
		register(PrintResource.class);
		
		register(ResponseSnsExceptionMapper.class);
		register(ResponseSnsRuntimeExceptionMapper.class);
	}
}
