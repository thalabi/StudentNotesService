package com.kerneldc.education.studentNotesService.dto.ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kerneldc.education.studentNotesService.bean.GradeEnum;

public class GradeUiDto extends AbstractBaseUiDto {

	private static final long serialVersionUID = 1L;

	@JsonProperty(value="grade")
	private GradeEnum gradeEnum;
	
	public GradeEnum getGradeEnum() {
		return gradeEnum;
	}
	public void setGradeEnum(GradeEnum gradeEnum) {
		this.gradeEnum = gradeEnum;
	}
}
