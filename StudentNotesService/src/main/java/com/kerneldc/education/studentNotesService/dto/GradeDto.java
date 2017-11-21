package com.kerneldc.education.studentNotesService.dto;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kerneldc.education.studentNotesService.bean.GradeEnum;

public class GradeDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	@JsonProperty(value="grade")
	@XmlElement(name="grade")
	private GradeEnum gradeEnum;
	
	public GradeEnum getGradeEnum() {
		return gradeEnum;
	}
	public void setGradeEnum(GradeEnum gradeEnum) {
		this.gradeEnum = gradeEnum;
	}
}
