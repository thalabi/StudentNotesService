package com.kerneldc.education.studentNotesService.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;

@Converter
public class GradeConverter implements AttributeConverter<GradeEnum, String> {

	@Override
	public String convertToDatabaseColumn(GradeEnum grade) {
		if (grade == null) {
			return null;
		}
		return grade.getValue();
	}

	@Override
	public GradeEnum convertToEntityAttribute(String value) {
		if (value == null) {
			return null;
		}
		return GradeEnum.fromValue(value);
	}

}
