package com.kerneldc.education.studentNotesService.dto;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;
	
	public static final String PROPERTY_ID = "id";

	private String firstName;
	private String lastName;
	@JsonProperty(value="grade")
	@XmlElement(name="grade")
	private GradeDto gradeDto;
	@JsonProperty(value="noteSet")
	@XmlElementWrapper(name="notes")
	@XmlElement(name="note")
	private Set<NoteDto> noteDtoSet = new LinkedHashSet<>();
	@JsonProperty(value="schoolYear")
	private SchoolYearDto schoolYearDto;
	
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
	public GradeDto getGradeDto() {
		return gradeDto;
	}
	public void setGradeDto(GradeDto gradeDto) {
		this.gradeDto = gradeDto;
	}
	public Set<NoteDto> getNoteDtoSet() {
		return noteDtoSet;
	}
	public void setNoteDtoSet(Set<NoteDto> noteDtoSet) {
		this.noteDtoSet = noteDtoSet;
	}
//	public String getSchoolYear() {
//		return schoolYear;
//	}
//	public void setSchoolYear(String schoolYear) {
//		this.schoolYear = schoolYear;
//	}
	public SchoolYearDto getSchoolYearDto() {
		return schoolYearDto;
	}
	public void setSchoolYearDto(SchoolYearDto schoolYearDto) {
		this.schoolYearDto = schoolYearDto;
	}
	// utility methods
	public void addNoteDto(NoteDto noteDto) {
//		if (noteDtoSet == null) {
//			noteDtoSet = new LinkedHashSet<>();
//		}
		noteDtoSet.add(noteDto);
	}
}
