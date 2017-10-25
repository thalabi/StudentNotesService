package com.kerneldc.education.studentNotesService.dto.transformer;

import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.dto.GradeDto;

public class GradeTransformer {

	private GradeTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	  }

	public static GradeDto entityToDto(Grade grade) {
		GradeDto gradeDto = new GradeDto();
		BeanUtils.copyProperties(grade, gradeDto);
		return gradeDto;
	}

	public static Grade dtoToEntity(GradeDto gradeDto) {
		Grade grade = new Grade();
		BeanUtils.copyProperties(gradeDto, grade);
		return grade;
	}
}
