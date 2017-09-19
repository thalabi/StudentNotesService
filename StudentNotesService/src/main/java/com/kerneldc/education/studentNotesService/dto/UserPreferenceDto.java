package com.kerneldc.education.studentNotesService.dto;

public class UserPreferenceDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String username;
	private SchoolYearDto schoolYearDto;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
