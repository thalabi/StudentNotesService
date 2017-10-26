package com.kerneldc.education.studentNotesService.transformer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.transformer.StudentTransformer;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;

public class StudentTransformerTests {

	@Test
	public void testUiDtoToEntity_NoNotes_Success() {
		StudentUiDto studentUiDto = getSimpleStudentUiDto();
		Student student = StudentTransformer.uiDtoToEntity(studentUiDto);
		checkSimpleStudentUiDtoProperties(student, studentUiDto);	}

	@Test
	public void testUiDtoToEntity_OneNote_Success() {
		StudentUiDto studentUiDto = getSimpleStudentUiDto();
		NoteUiDto noteUiDto1 = new NoteUiDto();
		noteUiDto1.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		noteUiDto1.setText("text 1");
		studentUiDto.setNoteUiDtoSet(new HashSet<>(Arrays.asList(noteUiDto1)));
		Student student = StudentTransformer.uiDtoToEntity(studentUiDto);
		assertThat(student.getNoteSet(), hasSize(1));
		Note note1 = student.getNoteSet().iterator().next();
		assertThat(note1.getTimestamp(), equalTo(noteUiDto1.getTimestamp()));
		assertThat(note1.getText(), equalTo(noteUiDto1.getText()));
		checkSimpleStudentUiDtoProperties(student, studentUiDto);
	}
	
	private void checkSimpleStudentUiDtoProperties(Student student, StudentUiDto studentUiDto) {
		assertThat(student.getFirstName(), equalTo(studentUiDto.getFirstName()));
		assertThat(student.getLastName(), equalTo(studentUiDto.getLastName()));
		assertThat(student.getGradeSet(), hasSize(1));
		Grade grade = student.getGradeSet().iterator().next();
		//assertThat(grade.getGradeEnum(), equalTo(studentUiDto.getGradeEnum()));
		assertThat(student.getSchoolYearSet(), hasSize(1));
		SchoolYear schoolYear = student.getSchoolYearSet().iterator().next();
		//assertThat(schoolYear.getSchoolYear(), equalTo(studentUiDto.getSchoolYear()));
	}
	public static StudentUiDto getSimpleStudentUiDto() {
		StudentUiDto studentUiDto = new StudentUiDto();
		studentUiDto.setFirstName("first name");
		studentUiDto.setLastName("last name");
		//studentUiDto.setGradeEnum(GradeEnum.JK);
		//studentUiDto.setSchoolYear("2016-2017");
		return studentUiDto;
	}
}
