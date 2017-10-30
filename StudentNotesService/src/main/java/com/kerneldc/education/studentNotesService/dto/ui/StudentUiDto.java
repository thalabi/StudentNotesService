package com.kerneldc.education.studentNotesService.dto.ui;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentUiDto extends AbstractBaseUiDto {

	private static final long serialVersionUID = 1L;
	
	public static final String PROPERTY_ID = "id";

	private String firstName;
	private String lastName;
	private GradeUiDto gradeUiDto;
	@JsonProperty(value="noteSet")
	@XmlElementWrapper(name="notes")
	@XmlElement(name="note")
	private Set<NoteUiDto> noteUiDtoSet = new LinkedHashSet<>();
	@JsonProperty(value="schoolYear")
	private SchoolYearUiDto schoolYearUiDto;
	
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
	public GradeUiDto getGradeUiDto() {
		return gradeUiDto;
	}
	public void setGradeUiDto(GradeUiDto gradeUiDto) {
		this.gradeUiDto = gradeUiDto;
	}
	public Set<NoteUiDto> getNoteUiDtoSet() {
		return noteUiDtoSet;
	}
	public void setNoteUiDtoSet(Set<NoteUiDto> noteUiDtoSet) {
		this.noteUiDtoSet = noteUiDtoSet;
	}
//	public String getSchoolYear() {
//		return schoolYear;
//	}
//	public void setSchoolYear(String schoolYear) {
//		this.schoolYear = schoolYear;
//	}
	public SchoolYearUiDto getSchoolYearUiDto() {
		return schoolYearUiDto;
	}
	public void setSchoolYearUiDto(SchoolYearUiDto schoolYearUiDto) {
		this.schoolYearUiDto = schoolYearUiDto;
	}
	// utility methods
	public void addNoteUiDto(NoteUiDto noteUiDto) {
//		if (noteUiDtoSet == null) {
//			noteUiDtoSet = new LinkedHashSet<>();
//		}
		noteUiDtoSet.add(noteUiDto);
	}
}
