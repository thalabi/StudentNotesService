package com.kerneldc.education.studentNotesService.dto.transformer;

import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;

public class SchoolYearTransformer {

	private SchoolYearTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	}

//	public static SchoolYearDto entityToDto(SchoolYear schoolYear) {
//		SchoolYearDto schoolYearDto = new SchoolYearDto();
//		BeanUtils.copyProperties(schoolYear, schoolYearDto, "studentSet");
//		if (Persistence.getPersistenceUtil().isLoaded(schoolYear.getStudentSet())) {
//			Set<StudentDto> studentDtos = new LinkedHashSet<>(schoolYear.getStudentSet().size());
//			for (Student student : schoolYear.getStudentSet()) {
//				studentDtos.add(StudentTransformer.entityToDto(student));
//			}
//			//schoolYearDto.setStudentDtoSet(studentDtos);
//		}
//		return schoolYearDto;
//	}

	public static SchoolYearDto entityToDto(SchoolYear schoolYear) {
		SchoolYearDto schoolYearDto = new SchoolYearDto();
		BeanUtils.copyProperties(schoolYear, schoolYearDto, "studentSet");
//		if (Persistence.getPersistenceUtil().isLoaded(schoolYear.getStudentSet())) {
//			Set<StudentDto> studentUiDtos = new LinkedHashSet<>(schoolYear.getStudentSet().size());
//			for (Student student : schoolYear.getStudentSet()) {
//				studentUiDtos.add(StudentTransformer.entityToUiDto(student));
//			}
//			//schoolYearUiDto.setStudentDtoSet(studentDtos);
//		}
		return schoolYearDto;
	}

//	public static SchoolYear dtoToEntity(SchoolYearDto schoolYearDto) {
//		SchoolYear schoolYear = new SchoolYear();
//		BeanUtils.copyProperties(schoolYearDto, schoolYear);
//		return schoolYear;
//	}

	public static SchoolYear dtoToEntity(SchoolYearDto schoolYearDto) {
		SchoolYear schoolYear = new SchoolYear();
		BeanUtils.copyProperties(schoolYearDto, schoolYear);
		return schoolYear;
	}
}
