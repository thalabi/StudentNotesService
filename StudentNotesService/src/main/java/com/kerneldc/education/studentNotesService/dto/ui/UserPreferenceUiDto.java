package com.kerneldc.education.studentNotesService.dto.ui;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPreferenceUiDto extends AbstractBaseUiDto {

	private static final long serialVersionUID = 1L;

	private String username;
	@JsonProperty(value="schoolYear")
	private SchoolYearUiDto schoolYearUiDto;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public SchoolYearUiDto getSchoolYearUiDto() {
		return schoolYearUiDto;
	}
	public void setSchoolYearUiDto(SchoolYearUiDto schoolYearUiDto) {
		this.schoolYearUiDto = schoolYearUiDto;
	}
}
