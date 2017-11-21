package com.kerneldc.education.studentNotesService.transformer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Ignore;
import org.junit.Test;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.transformer.StudentTransformer;

public class StudentTransformerTests {

	@Ignore
	@Test
	public void testDtoToEntity_NoNotes_Success() {
		StudentDto studentDto = getSimpleStudentDto();
		Student student = StudentTransformer.dtoToEntity(studentDto);
		checkSimpleStudentDtoProperties(student, studentDto);
	}

	@Ignore
	@Test
	public void testDtoToEntity_OneNote_Success() {
		StudentDto studentDto = getSimpleStudentDto();
		NoteDto noteDto1 = new NoteDto();
		noteDto1.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		noteDto1.setText("text 1");
		studentDto.setNoteDtoSet(new HashSet<>(Arrays.asList(noteDto1)));
		Student student = StudentTransformer.dtoToEntity(studentDto);
		assertThat(student.getNoteSet(), hasSize(1));
		Note note1 = student.getNoteSet().iterator().next();
		assertThat(note1.getTimestamp(), equalTo(noteDto1.getTimestamp()));
		assertThat(note1.getText(), equalTo(noteDto1.getText()));
		checkSimpleStudentDtoProperties(student, studentDto);
	}
	
	private void checkSimpleStudentDtoProperties(Student student, StudentDto studentDto) {
		assertThat(student.getFirstName(), equalTo(studentDto.getFirstName()));
		assertThat(student.getLastName(), equalTo(studentDto.getLastName()));
		assertThat(student.getGradeSet(), hasSize(1));
		//Grade grade = student.getGradeSet().iterator().next();
		//assertThat(grade.getGradeEnum(), equalTo(studentUiDto.getGradeEnum()));
		assertThat(student.getSchoolYearSet(), hasSize(1));
		//SchoolYear schoolYear = student.getSchoolYearSet().iterator().next();
		//assertThat(schoolYear.getSchoolYear(), equalTo(studentUiDto.getSchoolYear()));
	}
	public static StudentDto getSimpleStudentDto() {
		StudentDto studentDto = new StudentDto();
		studentDto.setFirstName("first name");
		studentDto.setLastName("last name");
		//studentUiDto.setGradeEnum(GradeEnum.JK);
		//studentUiDto.setSchoolYear("2016-2017");
		return studentDto;
	}
}
