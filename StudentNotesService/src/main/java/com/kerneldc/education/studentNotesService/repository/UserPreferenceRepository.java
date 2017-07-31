package com.kerneldc.education.studentNotesService.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.UserPreference;

public interface UserPreferenceRepository extends CrudRepository<UserPreference, Long> {

	List<UserPreference> findByUsername(String username);
}
