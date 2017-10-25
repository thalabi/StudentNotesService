package com.kerneldc.education.studentNotesService.dto.transformer;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Persistence;

import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;

public class SchoolYearTransformer {

	private SchoolYearTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	  }

	public static SchoolYearDto entityToDto(SchoolYear schoolYear) {
		SchoolYearDto schoolYearDto = new SchoolYearDto();
		BeanUtils.copyProperties(schoolYear, schoolYearDto, "studentSet");
		if (Persistence.getPersistenceUtil().isLoaded(schoolYear.getStudentSet())) {
			Set<StudentDto> studentDtos = new LinkedHashSet<>(schoolYear.getStudentSet().size());
			for (Student student : schoolYear.getStudentSet()) {
				studentDtos.add(StudentTransformer.entityToDto(student));
			}
			schoolYearDto.setStudentDtoSet(studentDtos);
		}
		return schoolYearDto;
	}

	public static SchoolYear dtoToEntity(SchoolYearDto schoolYearDto) {
		SchoolYear schoolYear = new SchoolYear();
		BeanUtils.copyProperties(schoolYearDto, schoolYear);
		return schoolYear;
	}
}
