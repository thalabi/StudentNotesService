package com.kerneldc.education.studentNotesService.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.dto.UserPreferenceDto;
import com.kerneldc.education.studentNotesService.dto.transformer.UserPreferenceTransformer;
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
	@Path("/getDtoByUsername/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserPreferenceDto getDtoByUsername(
		@PathParam("username") String username) {
		
		LOGGER.debug("begin ...");
		UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
		try {
			UserPreference userPreference = userPreferenceRepository.findByUsername(username).get(0);
			userPreferenceDto = UserPreferenceTransformer.entityToDto(userPreference);
			LOGGER.debug("userPreferenceDto: {}", userPreferenceDto);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return userPreferenceDto;
	}

    @POST
	@Path("/saveUserPreferenceDto")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public UserPreferenceDto saveUserPreferenceDto(UserPreferenceDto userPreferenceDto) {

    	LOGGER.debug("begin ...");
    	UserPreference userPreference = UserPreferenceTransformer.dtoToEntity(userPreferenceDto);
    	UserPreferenceDto savedUserPreferenceDto;
    	try {
    		userPreference = userPreferenceRepository.save(userPreference);
    		savedUserPreferenceDto = UserPreferenceTransformer.entityToDto(userPreference);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
    	LOGGER.debug("end ...");
    	return savedUserPreferenceDto;
    }

//	@DELETE
//	@Path("/deleteSchoolYearById/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public String deleteSchoolYearById(@PathParam("id") Long id) {
//		
//		LOGGER.debug("begin ...");
//    	try {
//    		// Check if there are students enrolled in this year
//    		LOGGER.debug("id: {}", id);
//    		SchoolYear schoolYearToDelete = userPreferenceRepository.getStudentsBySchoolYearId(id);
//    		if (schoolYearToDelete == null) throw new NotFoundException();
//    		if (! schoolYearToDelete.getStudentSet().isEmpty()) throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>());
//    		userPreferenceRepository.delete(id);
//		} catch (RuntimeException e) {
//			LOGGER.error("Exception encountered: {}", e);
//			throw new SnsRuntimeException(e.getClass().getSimpleName());
//		}
//		LOGGER.debug("end ...");
//		return "";
//	}
//	
}
