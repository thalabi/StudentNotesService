package com.kerneldc.education.studentNotesService.dto.transformer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.UserPreferenceDto;

import io.jsonwebtoken.lang.Collections;

public class StudentTransformer {

	private StudentTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	  }

	// TODO convert schoolYearSet
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

	// TODO convert schoolYearSet
	public static Student dtoToEntity(StudentDto studentDto) {
		Student student = new Student();
		BeanUtils.copyProperties(studentDto, student);
		if (CollectionUtils.isNotEmpty(studentDto.getNoteDtoList())) {
			List<Note> noteList = new ArrayList<>();
			for (NoteDto noteDto : studentDto.getNoteDtoList()) {
				noteList.add(NoteTransformer.dtoToEntity(noteDto));
			}
			student.setNoteList(noteList);
		}
		return student;
	}
}
