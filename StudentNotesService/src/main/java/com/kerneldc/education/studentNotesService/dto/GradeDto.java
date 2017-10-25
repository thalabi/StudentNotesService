package com.kerneldc.education.studentNotesService.dto;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;

public class GradeDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	private Long id;
	private GradeEnum grade;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public GradeEnum getGrade() {
		return grade;
	}
	public void setGrade(GradeEnum grade) {
		this.grade = grade;
	}
}
