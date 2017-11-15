package com.kerneldc.education.studentNotesService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.UserPreference;

public interface UserPreferenceRepository extends CrudRepository<UserPreference, Long> {

	@EntityGraph(value = "UserPreference.schoolYear", type = EntityGraphType.LOAD)
	List<UserPreference> findByUsername(String username);
}
