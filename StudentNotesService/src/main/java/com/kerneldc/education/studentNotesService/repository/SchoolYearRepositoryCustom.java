package com.kerneldc.education.studentNotesService.repository;

import java.util.Set;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;

public interface SchoolYearRepositoryCustom {

	/**
	 * Uses the NamedEntityGraph defined on the entity to retrieve a SchoolYear
	 * @param id
	 * @return
	 */
	Set<SchoolYear> getStudentsBySchoolYearId(Long id);
	/**
	 * Uses the NamedEntityGraph defined on the entity to retrieve all Students
	 * @return a list of all students with their noteSet ordered by lastName, firstName and timestamp
	 */
//	List<Student> getAllStudents();
//	
//	Set<Student> getLatestActiveStudents(int limit);
//	Set<Student> getStudentsByTimestampRange(Timestamp fromTimestamp, Timestamp toTimestamp);
//	
//	List<Student> getStudentsByListOfIds(List<Long> studentIds);
}
