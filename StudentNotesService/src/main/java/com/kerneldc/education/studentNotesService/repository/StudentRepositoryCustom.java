package com.kerneldc.education.studentNotesService.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;

public interface StudentRepositoryCustom {

	/**
	 * Uses the NamedEntityGraph defined on the entity to retrieve a Student
	 * @param id
	 * @return
	 */
	Student getStudentById(Long id);
	Student getStudentByIdWithGradeList(Long id);
	Student getStudentByIdWithNoteListAndGradeList(Long id);
	/**
	 * Uses the NamedEntityGraph defined on the entity to retrieve all Students
	 * @return a list of all students with their noteSet ordered by lastName, firstName and timestamp
	 */
	List<Student> getAllStudents();
	
	Set<Student> getLatestActiveStudents(int limit);
	Set<Student> getLatestActiveStudents(String username, int limit);
	List<StudentUiDto> getStudentsByTimestampRange(Long schoolYearId, Timestamp fromTimestamp, Timestamp toTimestamp);
	
	List<Student> getStudentsByListOfIds(List<Long> studentIds);
	SchoolYear getStudentsByUsernameInUserPreference(String username);
	
	List<StudentDto> getStudentDtosInSchoolYear(Long schoolYearId);
	List<StudentDto> getStudentDtosNotInSchoolYear(Long schoolYearId);
	//List<StudentUiDto> getStudentsByUsername(String username);
	List<StudentUiDto> getStudentsBySchoolYearIdAndListOfIds(Long schoolYearId, List<Long> studentIds);
	List<StudentUiDto> getStudentGraphBySchoolYear(Long schoolYearId);
	List<StudentUiDto> getStudentsInSchoolYear(Long schoolYearId);
	List<StudentUiDto> getStudentsNotInSchoolYear(Long schoolYearId);
}
