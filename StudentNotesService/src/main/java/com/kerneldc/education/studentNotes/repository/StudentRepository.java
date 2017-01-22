package com.kerneldc.education.studentNotes.repository;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotes.domain.Student;

public interface StudentRepository extends CrudRepository<Student, Long>, StudentRepositoryCustom  {

	//Iterable<Student> findAllById();
}
