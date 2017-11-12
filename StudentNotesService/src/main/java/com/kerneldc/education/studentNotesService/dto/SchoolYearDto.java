package com.kerneldc.education.studentNotesService.dto;

import java.util.Date;

public class SchoolYearDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String schoolYear;
	private Date startDate;
	private Date endDate;
	
	//@XmlElement(name="studentSet")
	// The above is not recognized during unit testing
//	@JsonProperty(value="studentSet")
//	private Set<StudentDto> studentDtoSet;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
//	public Set<StudentDto> getStudentDtoSet() {
//		return studentDtoSet;
//	}
//	public void setStudentDtoSet(Set<StudentDto> studentDtoSet) {
//		this.studentDtoSet = studentDtoSet;
//	}
}
