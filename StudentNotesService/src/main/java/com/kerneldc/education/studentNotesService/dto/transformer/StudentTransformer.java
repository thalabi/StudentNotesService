package com.kerneldc.education.studentNotesService.dto.transformer;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.UserPreferenceDto;

public class StudentTransformer {

	private StudentTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	  }

	public static StudentDto entityToDto(Student student) {
		StudentDto studentDto = new StudentDto();
		BeanUtils.copyProperties(student, studentDto);
		if (Hibernate.isInitialized(student.getNoteList())) {
			List<NoteDto> noteDtoList = new ArrayList<>();
			for (Note note : student.getNoteList()) {
				NoteDto noteDto = new NoteDto();
				BeanUtils.copyProperties(note, noteDto);
				noteDtoList.add(noteDto);
			}
			studentDto.setNoteDtoList(noteDtoList);
		}
		return studentDto;
	}

//	public static UserPreference dtoToEntity(UserPreferenceDto userPreferenceDto) {
//		UserPreference userPreference = new UserPreference();
//		BeanUtils.copyProperties(userPreferenceDto, userPreference);
//		SchoolYear schoolYear = new SchoolYear();
//		BeanUtils.copyProperties(userPreferenceDto.getSchoolYearDto(), schoolYear);
//		userPreference.setSchoolYear(schoolYear);
//		return userPreference;
//	}
}
