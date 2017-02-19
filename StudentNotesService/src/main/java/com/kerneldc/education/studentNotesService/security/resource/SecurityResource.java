package com.kerneldc.education.studentNotesService.security.resource;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.service.JwtToken;

@Component
@Path("/StudentNotesService/Security")
public class SecurityResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private JwtToken jwtToken;
	
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
	   
//    @POST
//	@Path("/authenticate")
//    @Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//    public User authenticate(
//    	String usernameAndPassword) throws JsonParseException, JsonMappingException, IOException {
//
//    	// TODO send 401 unauthoried if authentication fails
//    	LOGGER.debug("begin ...");
//		ObjectMapper objectMapper = new ObjectMapper();
//    	User user = objectMapper.readValue(usernameAndPassword, User.class);
//    	user.setId(7l);
//    	user.setFirstName("first name");
//    	user.setLastName("last name");
//    	user.setToken("fake-jwt-token");
//    	LOGGER.debug("end ...");
//    	return user;
//    }

	@POST
	@Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public User authenticate(
    	String usernameAndPassword) throws JsonParseException, JsonMappingException, IOException {

    	// TODO send 401 unauthoried if authentication fails
    	LOGGER.debug("begin ...");
		ObjectMapper objectMapper = new ObjectMapper();
    	User user = objectMapper.readValue(usernameAndPassword, User.class);
    	user.setId(7l);
    	user.setFirstName("first name");
    	user.setLastName("last name");
    	user.setToken(jwtToken.generate(user.getUsername()));
    	LOGGER.debug("end ...");
    	return user;
    }
}
