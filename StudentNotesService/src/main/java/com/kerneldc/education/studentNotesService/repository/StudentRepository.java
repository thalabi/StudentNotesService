package com.kerneldc.education.studentNotesService.repository;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.Student;

public interface StudentRepository extends CrudRepository<Student, Long>, StudentRepositoryCustom  {

	Iterable<Student> findAllByOrderByFirstNameAscLastNameAsc();
}
