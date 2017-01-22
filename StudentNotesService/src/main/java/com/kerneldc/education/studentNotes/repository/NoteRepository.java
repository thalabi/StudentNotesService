package com.kerneldc.education.studentNotes.repository;

import org.springframework.data.repository.CrudRepository;

import com.kerneldc.education.studentNotes.domain.Note;

public interface NoteRepository extends CrudRepository<Note, Long> {

}
