package com.kerneldc.education.studentNotesService.dto.transformer;

import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.UserPreferenceDto;
import com.kerneldc.education.studentNotesService.dto.ui.SchoolYearUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.UserPreferenceUiDto;

public class UserPreferenceTransformer {

	private UserPreferenceTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	  }

	public static UserPreferenceDto entityToDto(UserPreference userPreference) {
		UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
		BeanUtils.copyProperties(userPreference, userPreferenceDto);
		SchoolYearDto schoolYearDto = new SchoolYearDto();
		BeanUtils.copyProperties(userPreference.getSchoolYear(), schoolYearDto);
		userPreferenceDto.setSchoolYearDto(schoolYearDto);
		return userPreferenceDto;
	}

	public static UserPreferenceUiDto entityToUiDto(UserPreference userPreference) {
		UserPreferenceUiDto userPreferenceUiDto = new UserPreferenceUiDto();
		BeanUtils.copyProperties(userPreference, userPreferenceUiDto);
		SchoolYearUiDto schoolYearUiDto = new SchoolYearUiDto();
		BeanUtils.copyProperties(userPreference.getSchoolYear(), schoolYearUiDto);
		userPreferenceUiDto.setSchoolYearUiDto(schoolYearUiDto);
		return userPreferenceUiDto;
	}

	public static UserPreference dtoToEntity(UserPreferenceDto userPreferenceDto) {
		UserPreference userPreference = new UserPreference();
		BeanUtils.copyProperties(userPreferenceDto, userPreference);
		SchoolYear schoolYear = new SchoolYear();
		BeanUtils.copyProperties(userPreferenceDto.getSchoolYearDto(), schoolYear);
		userPreference.setSchoolYear(schoolYear);
		return userPreference;
	}
}
