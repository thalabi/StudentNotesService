package com.kerneldc.education.studentNotesService.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;

public interface GradeRepository extends CrudRepository<Grade, Long> {

	List<Grade> findByStudentAndSchoolYear(Student student, SchoolYear schoolYear);
}
