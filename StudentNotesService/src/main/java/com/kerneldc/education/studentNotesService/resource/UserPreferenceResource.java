package com.kerneldc.education.studentNotesService.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;
import com.kerneldc.education.studentNotesService.exception.RowNotFoundException;
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
	@Path("/getByUsername/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.SchoolYearExtended.class)
	public UserPreference getByUsername(
		@PathParam("username") String username) throws RowNotFoundException {
		
		LOGGER.debug("begin ...");
		UserPreference userPreference = null;
		try {
			userPreference = userPreferenceRepository.findByUsername(username).get(0);
			LOGGER.debug("userPreference.getUsername(): {}", userPreference.getUsername());
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		LOGGER.debug("userPreference: {}", userPreference);
		return userPreference;
	}

//	@GET
//	@Path("/getAllSchoolYears")
//	@Produces(MediaType.APPLICATION_JSON)
//	@JsonView(View.Default.class)
//	public List<SchoolYear> getAllSchoolYears() {
//		
//		LOGGER.debug("begin ...");
//		List<SchoolYear> schoolYears;
//		try {
//			//schoolYears = userPreferenceRepository.findAllByOrderBySchoolYearAsc();
//			schoolYears = userPreferenceRepository.findAllByOrderByEndDateDesc();
//		} catch (RuntimeException e) {
//			LOGGER.error("Exception encountered: {}", e);
//			throw new SnsRuntimeException(e.getClass().getSimpleName());
//		}
//		LOGGER.debug("end ...");
//		return schoolYears;
//	}

//	@POST
//	@Path("/getLatestActiveStudentsBySchoolYearId")
//	@Produces(MediaType.APPLICATION_JSON)
//	@JsonView(View.SchoolYearExtended.class)
//	public SchoolYear getLatestActiveStudentsBySchoolYearId(SchoolYearIdAndLimit schoolYearIdAndLimit) {
//		
//		LOGGER.debug("begin ...");
//		LOGGER.debug("schoolYearIdAndLimitBean: {}", schoolYearIdAndLimit);
//		Set<SchoolYear> schoolYears = new HashSet<>();
//		try {
//			schoolYears = userPreferenceRepository.getLatestActiveStudentsBySchoolYearId(schoolYearIdAndLimit.getSchoolYearId(), schoolYearIdAndLimit.getLimit());
//			LOGGER.debug("schoolYears.size(): {}", schoolYears.size());
//			Assert.isTrue(schoolYears.size() == 1);
//		} catch (RuntimeException e) {
//			throw new SnsRuntimeException(e.getClass().getSimpleName());
//		}
//		LOGGER.debug("end ...");
//		return schoolYears.iterator().next();
//	}
//
//    @POST
//	@Path("/saveSchoolYear")
//    @Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	@JsonView(View.Default.class)
//    public SchoolYear saveSchoolYear(SchoolYear schoolYear) {
//
//    	LOGGER.debug("begin ...");
//    	SchoolYear savedSchoolYear;
//    	try {
//    		savedSchoolYear = userPreferenceRepository.save(schoolYear);
//		} catch (RuntimeException e) {
//			LOGGER.error("Exception encountered: {}", e);
//			throw new SnsRuntimeException(e.getClass().getSimpleName());
//		}
//    	LOGGER.debug("end ...");
//    	return savedSchoolYear;
//    }
//	
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
