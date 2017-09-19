package com.kerneldc.education.studentNotesService.dto;

import java.util.List;
import java.util.Set;

import com.kerneldc.education.studentNotesService.bean.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;

public class StudentDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String firstName;
	private String lastName;
	private Grade grade;
	private List<NoteDto> noteDtoList;
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
	public Grade getGrade() {
		return grade;
	}
	public void setGrade(Grade grade) {
		this.grade = grade;
	}
	public List<NoteDto> getNoteDtoList() {
		return noteDtoList;
	}
	public void setNoteDtoList(List<NoteDto> noteDtoList) {
		this.noteDtoList = noteDtoList;
	}
	public Set<SchoolYearDto> getSchoolYearDtoSet() {
		return schoolYearDtoSet;
	}
	public void setSchoolYearDtoSet(Set<SchoolYearDto> schoolYearDtoSet) {
		this.schoolYearDtoSet = schoolYearDtoSet;
	}
}
