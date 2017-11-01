package com.kerneldc.education.studentNotesService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;

public interface SchoolYearRepository extends CrudRepository<SchoolYear, Long>, SchoolYearRepositoryCustom {
	List<SchoolYear> findAllByOrderBySchoolYearAsc();
	List<SchoolYear> findAllByOrderByEndDateDesc();
	@EntityGraph(value = "SchoolYear.studentSet", type = EntityGraphType.LOAD)
	SchoolYear getById(Long id);
}
