package com.kerneldc.education.studentNotesService.dto.ui;

import java.util.Date;

public class SchoolYearUiDto extends AbstractBaseUiDto {

	private static final long serialVersionUID = 1L;

	private String schoolYear;
	private Date startDate;
	private Date endDate;
	
	public String getSchoolYear() {
		return schoolYear;
	}
	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
