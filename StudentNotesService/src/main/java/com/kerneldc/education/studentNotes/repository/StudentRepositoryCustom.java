package com.kerneldc.education.studentNotes.repository;

import java.util.List;

import com.kerneldc.education.studentNotes.domain.Student;

public interface StudentRepositoryCustom {

	Student getStudentById(Long id);
	/**
	 * @return a list of all students with their noteSet ordered by lastName, firstName and timestamp
	 */
	List<Student> getAllStudents();
	
	// not used, remove
	Student updateStudent(Student student);
}
