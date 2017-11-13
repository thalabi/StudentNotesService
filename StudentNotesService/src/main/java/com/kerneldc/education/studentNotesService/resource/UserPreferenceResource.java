package com.kerneldc.education.studentNotesService.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.dto.transformer.UserPreferenceTransformer;
import com.kerneldc.education.studentNotesService.dto.ui.UserPreferenceUiDto;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.UserPreferenceRepository;

@Component
@Path("/StudentNotesService/userPreference")
public class UserPreferenceResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private UserPreferenceRepository userPreferenceRepository;

	public UserPreferenceResource() {
		LOGGER.info("Initialized ...");
	}
	
	@GET
	public String hello() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return "Hello";
	}

	@GET
	@Path("/getUiDtoByUsername/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPreferenceUiDto getUiDtoByUsername(
		@PathParam("username") String username) {
		
		LOGGER.debug("begin ...");
		UserPreferenceUiDto userPreferenceUiDto = new UserPreferenceUiDto();
		try {
			UserPreference userPreference = userPreferenceRepository.findByUsername(username).get(0);
			userPreferenceUiDto = UserPreferenceTransformer.entityToUiDto(userPreference);
			LOGGER.debug("userPreferenceUiDto: {}", userPreferenceUiDto);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
		LOGGER.debug("end ...");
		return userPreferenceUiDto;
	}

    @POST
	@Path("/saveUserPreferenceUiDto")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public UserPreferenceUiDto saveUserPreferenceUiDto(
    	UserPreferenceUiDto userPreferenceUiDto) {

    	LOGGER.debug("begin ...");
    	UserPreference userPreference = UserPreferenceTransformer.uiDtoToEntity(userPreferenceUiDto);
    	UserPreferenceUiDto savedUserPreferenceUiDto;
    	try {
    		userPreference = userPreferenceRepository.save(userPreference);
    		savedUserPreferenceUiDto = UserPreferenceTransformer.entityToUiDto(userPreference);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
    	LOGGER.debug("end ...");
    	return savedUserPreferenceUiDto;
    }

}
