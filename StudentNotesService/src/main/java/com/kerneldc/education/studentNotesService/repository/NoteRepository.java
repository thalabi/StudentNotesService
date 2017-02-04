package com.kerneldc.education.studentNotesService.repository;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotesService.domain.Note;

public interface NoteRepository extends CrudRepository<Note, Long> {

}
