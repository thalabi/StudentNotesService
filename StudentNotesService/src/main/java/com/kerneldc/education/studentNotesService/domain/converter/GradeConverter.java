package com.kerneldc.education.studentNotesService.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.kerneldc.education.studentNotesService.bean.Grades;

@Converter
public class GradeConverter implements AttributeConverter<Grades, String> {

	@Override
	public String convertToDatabaseColumn(Grades grade) {
		if (grade == null) {
			return null;
		}
		return grade.getValue();
	}

	@Override
	public Grades convertToEntityAttribute(String value) {
		if (value == null) {
			return null;
		}
		return Grades.fromValue(value);
	}

}
