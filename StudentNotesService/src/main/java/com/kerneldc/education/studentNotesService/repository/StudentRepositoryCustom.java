package com.kerneldc.education.studentNotesService.repository;

import java.util.List;

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
	
	// not used, remove
	Student updateStudent(Student student);
}
