package com.kerneldc.education.studentNotesService.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String firstName;
	private String lastName;
//	private GradeEnum grade;
	@JsonProperty(value="gradeSet")
	private Set<GradeDto> gradeDtoSet;
	//@XmlElement(name="noteList")
	@JsonProperty(value="noteSet")
	private Set<NoteDto> noteDtoSet;
	//@XmlElement(name="schoolYearSet")
	@JsonProperty(value="schoolYearSet")
	private Set<SchoolYearDto> schoolYearDtoSet;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
//	public GradeEnum getGrade() {
//		return grade;
//	}
//	public void setGrade(GradeEnum grade) {
//		this.grade = grade;
//	}
	public Set<GradeDto> getGradeDtoSet() {
		return gradeDtoSet;
	}
	public void setGradeDtoSet(Set<GradeDto> gradeDtoSet) {
		this.gradeDtoSet = gradeDtoSet;
	}
	public Set<NoteDto> getNoteDtoSet() {
		return noteDtoSet;
	}
	public void setNoteDtoSet(Set<NoteDto> noteDtoSet) {
		this.noteDtoSet = noteDtoSet;
	}
	public Set<SchoolYearDto> getSchoolYearDtoSet() {
		return schoolYearDtoSet;
	}
	public void setSchoolYearDtoSet(Set<SchoolYearDto> schoolYearDtoSet) {
		this.schoolYearDtoSet = schoolYearDtoSet;
	}
}
