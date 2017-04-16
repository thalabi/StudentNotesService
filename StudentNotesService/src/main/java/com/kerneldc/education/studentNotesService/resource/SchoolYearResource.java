package com.kerneldc.education.studentNotesService.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
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

	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
	// curl -H -i http://localhost:8080/StudentNotesService/getStudentById/1
//	@GET
//	@Path("/getStudentById/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Student getStudentById(
//		@PathParam("id") Long id) throws RowNotFoundException {
//		
//		LOGGER.debug("begin ...");
//		Student student;
//		try {
//			student = studentRepository.getStudentById(id);
//		} catch (RuntimeException e) {
//			String errorMessage = String.format("Encountered exception while looking up student id %s. Exception is: %s", id, e.getClass().getSimpleName());
//			LOGGER.error(errorMessage);
//			throw new SnsRuntimeException(errorMessage);
//		}
//		if (student == null) {
//			String errorMessage = String.format("Student id %s not found", id);
//			LOGGER.debug(errorMessage);
//			throw new RowNotFoundException(errorMessage);
//		}
//		LOGGER.debug("end ...");
//		return student;
//	}

}
