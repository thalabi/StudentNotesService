package com.kerneldc.education.studentNotesService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPreferenceDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	private String username;
	@JsonProperty(value="schoolYear")
	private SchoolYearDto schoolYearDto;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public SchoolYearDto getSchoolYearDto() {
		return schoolYearDto;
	}
	public void setSchoolYearDto(SchoolYearDto schoolYearDto) {
		this.schoolYearDto = schoolYearDto;
	}
}
