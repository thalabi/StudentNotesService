package com.kerneldc.education.studentNotesService.repository;

import java.util.List;

import com.kerneldc.education.studentNotesService.domain.Student;

public interface StudentRepositoryCustom {

	Student getStudentById(Long id);
	/**
	 * @return a list of all students with their noteSet ordered by lastName, firstName and timestamp
	 */
	List<Student> getAllStudents();
	
	// not used, remove
	Student updateStudent(Student student);
}
