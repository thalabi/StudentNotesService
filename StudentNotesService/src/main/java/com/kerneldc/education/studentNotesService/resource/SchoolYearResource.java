package com.kerneldc.education.studentNotesService.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.bean.SchoolYearIdAndLimit;
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
	@JsonView(View.Default.class)
	public List<SchoolYear> getAllSchoolYears() {
		
		LOGGER.debug("begin ...");
		List<SchoolYear> schoolYears;
		try {
			//schoolYears = schoolYearRepository.findAllByOrderBySchoolYearAsc();
			schoolYears = schoolYearRepository.findAllByOrderByEndDateDesc();
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return schoolYears;
	}

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

	@POST
	@Path("/getLatestActiveStudentsBySchoolYearId")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.SchoolYearExtended.class)
	public SchoolYear getLatestActiveStudentsBySchoolYearId(SchoolYearIdAndLimit schoolYearIdAndLimit) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("schoolYearIdAndLimitBean: {}", schoolYearIdAndLimit);
		Set<SchoolYear> schoolYears = new HashSet<>();
		try {
			schoolYears = schoolYearRepository.getLatestActiveStudentsBySchoolYearId(schoolYearIdAndLimit.getSchoolYearId(), schoolYearIdAndLimit.getLimit());
			LOGGER.debug("schoolYears.size(): {}", schoolYears.size());
			Assert.isTrue(schoolYears.size() == 1);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return schoolYears.iterator().next();
	}

    @POST
	@Path("/saveSchoolYear")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.Default.class)
    public SchoolYear saveSchoolYear(SchoolYear schoolYear) {

    	LOGGER.debug("begin ...");
    	SchoolYear savedSchoolYear;
    	try {
    		savedSchoolYear = schoolYearRepository.save(schoolYear);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
    	LOGGER.debug("end ...");
    	return savedSchoolYear;
    }
	
	@DELETE
	@Path("/deleteSchoolYearById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSchoolYearById(@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
    	try {
    		schoolYearRepository.delete(id);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return "";
	}
	
}
