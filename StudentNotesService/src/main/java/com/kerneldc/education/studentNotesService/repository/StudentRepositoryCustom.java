package com.kerneldc.education.studentNotesService.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;

public interface StudentRepositoryCustom {

	/**
	 * Uses the NamedEntityGraph defined on the entity to retrieve a Student
	 * @param id
	 * @return
	 */
	Student getStudentById(Long id);
	/**
	 * Uses the NamedEntityGraph defined on the entity to retrieve all Students
	 * @return a list of all students with their noteSet ordered by lastName, firstName and timestamp
	 */
	List<Student> getAllStudents();
	
	Set<Student> getLatestActiveStudents(int limit);
	Set<Student> getLatestActiveStudents(String username, int limit);
	Set<Student> getStudentsByTimestampRange(Timestamp fromTimestamp, Timestamp toTimestamp);
	
	List<Student> getStudentsByListOfIds(List<Long> studentIds);
	SchoolYear getStudentsByUsernameInUserPreference(String username);
}
