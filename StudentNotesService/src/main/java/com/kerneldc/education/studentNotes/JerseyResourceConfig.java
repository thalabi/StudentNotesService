package com.kerneldc.education.studentNotes;

import org.glassfish.jersey.server.ResourceConfig;

import com.kerneldc.education.studentNotes.resource.StudentNotesResource;
import com.kerneldc.education.studentNotes.util.JacksonObjectMapperProvider;

public class JerseyResourceConfig extends ResourceConfig {

	public JerseyResourceConfig() {

		// Create Jersey configuration with Jackson object mapper provider
		super (JacksonObjectMapperProvider.class);

		// Specifying a package does not work due to Jersey classpath scanning limitations
		// See: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-1.4-Release-Notes#jersey-classpath-scanning-limitations
		// Listing of Jersey resources is required as a workaround
		//
		//packages(true, "com.kerneldc.HeroServiceSpringBoot.resource");
		register(StudentNotesResource.class);
		
	}
}
