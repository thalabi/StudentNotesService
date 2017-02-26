package com.kerneldc.education.studentNotesService.security.resource;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.service.AuthenticationService;

@Component
@Path("/StudentNotesService/Security")
public class SecurityResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private AuthenticationService authenticationService;
	
	public SecurityResource() {
		LOGGER.info("Initialized ...");
	}
	
	@GET
	public String hello() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return "Hello";
	}

    // curl -i -H "Content-Type: application/json" -X POST -d '{"username":"thalabi","password":"xxxxxxxxxxxxxxxx"}' http://localhost:8080/StudentNotesService/Security/authenticate
	   
	/**
	 * Authenticates the supplied username and password and return a user object with the
	 * generated jwt token
	 * @param usernameAndPassword in json format
	 * @return User object including the generated jwt token
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@POST
	@Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(
    	String usernameAndPassword) {

    	LOGGER.debug("begin ...");
		ObjectMapper objectMapper = new ObjectMapper();
    	User user;
		try {
			user = objectMapper.readValue(usernameAndPassword, User.class);
	    	user = authenticationService.authenticate(user);
		} catch (IOException | UsernameNotFoundException | BadCredentialsException e) {
			LOGGER.warn("Authentication failed for credentials found in request: {}", usernameAndPassword);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
    	LOGGER.debug("end ...");

    	return Response.ok(user).build();
    }
	
}
