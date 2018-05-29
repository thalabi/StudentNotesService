package com.kerneldc.education.studentNotesService.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.transformer.SchoolYearTransformer;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.repository.util.RepositoryUtils;
import com.kerneldc.education.studentNotesService.resource.vo.SaveRemoveStudentsToFromSchoolYearVo;

@Component
@Path("/StudentNotesService/schoolYear")
public class SchoolYearResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private SchoolYearRepository schoolYearRepository;

	@Autowired
	private StudentRepository studentRepository;

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
	@Path("/getAllSchoolYearUiDtos")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SchoolYearDto> getAllSchoolYearDtos() {
		
		LOGGER.debug("begin ...");
		List<SchoolYearDto> schoolYearDtos = new ArrayList<>();
		try {
			//schoolYears = schoolYearRepository.findAllByOrderBySchoolYearAsc();
			List<SchoolYear> schoolYears = schoolYearRepository.findAllByOrderByEndDateDesc();
			for (SchoolYear schoolYear : schoolYears) {
				SchoolYearDto schoolYearDto = SchoolYearTransformer.entityToDto(schoolYear);
				schoolYearDtos.add(schoolYearDto);
			}
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return schoolYearDtos;
	}
    @POST
	@Path("/saveSchoolYearUiDto")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SchoolYearDto saveSchoolYearDto(SchoolYearDto schoolYearDto) throws SnsException {

    	LOGGER.debug("begin ...");
		SchoolYear schoolYear = RepositoryUtils.getAndCheckEntityVersion(schoolYearDto.getId(), schoolYearDto.getVersion(), schoolYearRepository, SchoolYear.class);
    	schoolYear.setSchoolYear(schoolYearDto.getSchoolYear());
    	schoolYear.setStartDate(schoolYearDto.getStartDate());
    	schoolYear.setEndDate(schoolYearDto.getEndDate());
    	SchoolYear savedSchoolYear;
    	SchoolYearDto savedSchoolYearDto;
    	try {
    		savedSchoolYear = schoolYearRepository.save(schoolYear);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
    	savedSchoolYearDto = SchoolYearTransformer.entityToDto(savedSchoolYear);
    	LOGGER.debug("end ...");    	
    	return savedSchoolYearDto;
    }

    @DELETE
	@Path("/deleteSchoolYearById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSchoolYearById(@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
    	try {
    		// Check if there are students enrolled in this year
    		LOGGER.debug("id: {}", id);
    		SchoolYear schoolYearToDelete = schoolYearRepository.getById(id);
    		if (schoolYearToDelete == null) throw new NotFoundException();
    		if (!/* not */schoolYearToDelete.getStudentSet().isEmpty()) throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>());
    		schoolYearRepository.delete(id);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
		LOGGER.debug("end ...");
		return "";
	}
	
	@POST
	@Path("/saveRemoveStudentsToFromSchoolYear")
    @Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public String saveRemoveStudents(
		SaveRemoveStudentsToFromSchoolYearVo saveRemoveStudentsToFromSchoolYearVO) {
		
		LOGGER.debug("begin ...");
		SchoolYear schoolYear = schoolYearRepository.findOne(saveRemoveStudentsToFromSchoolYearVO.getSchoolYearId());
		LOGGER.debug("saveRemoveStudentsToFromSchoolYearVO: {}", saveRemoveStudentsToFromSchoolYearVO);
		List<Long> oldSchoolYearStudentIds = saveRemoveStudentsToFromSchoolYearVO.getOldSchoolYearStudentIds();
		List<Long> newSchoolYearStudentIds = saveRemoveStudentsToFromSchoolYearVO.getNewSchoolYearStudentIds();
		List<Long> copyOfNewSchoolYearStudents = new ArrayList<>(newSchoolYearStudentIds);
		newSchoolYearStudentIds.removeAll(oldSchoolYearStudentIds);
		oldSchoolYearStudentIds.removeAll(copyOfNewSchoolYearStudents);
		LOGGER.debug("Student ids to be added: {}", newSchoolYearStudentIds);
		LOGGER.debug("Student ids to be removed: {}", oldSchoolYearStudentIds);
		for (Long id : newSchoolYearStudentIds) {
			Student student = studentRepository.findOne(id);
			student.addSchoolYear(schoolYear);
		}
		for (Long id : oldSchoolYearStudentIds) {
			Student student = studentRepository.findOne(id);
			student.removeSchoolYear(schoolYear);
		}
		schoolYearRepository.save(schoolYear);
		LOGGER.debug("end ...");
		return "";
	}
}
