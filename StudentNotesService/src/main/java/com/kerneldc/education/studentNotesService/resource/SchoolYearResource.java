package com.kerneldc.education.studentNotesService.resource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;
import com.kerneldc.education.studentNotesService.exception.RowNotFoundException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;

@Component
@Path("/StudentNotesService/schoolYear")
public class SchoolYearResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private SchoolYearRepository schoolYearRepository;

	public SchoolYearResource() {
		LOGGER.info("Initialized ...");
	}
	
	@GET
	public String hello() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return "Hello";
	}

	@GET
	@Path("/getAllSchoolYears")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.SchoolYearExtended.class)
	public List<SchoolYear> getAllSchoolYears() {
		
		LOGGER.debug("begin ...");
		List<SchoolYear> schoolYears;
		try {
			schoolYears = schoolYearRepository.findAllByOrderBySchoolYearAsc();
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return schoolYears;
	}

//	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
//	// curl -H -i http://localhost:8080/StudentNotesService/getStudentById/1
//	@GET
//	@Path("/getSchoolYearById/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	@JsonView(View.SchoolYearExtended.class)
//	public SchoolYear getSchoolYearById(
//		@PathParam("id") Long id) throws RowNotFoundException {
//		
//		LOGGER.debug("begin ...");
//		SchoolYear schoolYear;
//		try {
//			Iterator<SchoolYear> iterator = schoolYearRepository.getStudentsBySchoolYearId(id).iterator();
//			if (iterator.hasNext()) {
//				schoolYear = schoolYearRepository.getStudentsBySchoolYearId(id).iterator().next();
//			} else {
//				schoolYear = null;
//			}
//		} catch (RuntimeException e) {
//			String errorMessage = String.format("Encountered exception while looking up student id %s. Exception is: %s", id, e.getClass().getSimpleName());
//			LOGGER.error(errorMessage);
//			throw new SnsRuntimeException(errorMessage);
//		}
//		if (schoolYear == null) {
//			String errorMessage = String.format("School year id %s not found", id);
//			LOGGER.debug(errorMessage);
//			throw new RowNotFoundException(errorMessage);
//		}
//		LOGGER.debug("end ...");
//		return schoolYear;
//	}

	@GET
	@Path("/getStudentsBySchoolYearId/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.SchoolYearExtended.class)
	public SchoolYear getStudentsBySchoolYearId(
		@PathParam("id") Long id) throws RowNotFoundException {
		
		LOGGER.debug("begin ...");
		Set<SchoolYear> schoolYears = new HashSet<>();
		try {
			schoolYears = schoolYearRepository.getStudentsBySchoolYearId(id);
			Assert.isTrue(schoolYears.size() == 1);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return schoolYears.iterator().next();
	}
}
