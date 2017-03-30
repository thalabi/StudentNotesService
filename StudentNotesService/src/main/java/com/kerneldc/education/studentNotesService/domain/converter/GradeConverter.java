package com.kerneldc.education.studentNotesService.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.kerneldc.education.studentNotesService.bean.Grade;

@Converter
public class GradeConverter implements AttributeConverter<Grade, String> {

	@Override
	public String convertToDatabaseColumn(Grade grade) {
		if (grade == null) {
			return null;
		}
		return grade.getValue();
	}

	@Override
	public Grade convertToEntityAttribute(String value) {
		if (value == null) {
			return null;
		}
		return Grade.fromValue(value);
	}

}
