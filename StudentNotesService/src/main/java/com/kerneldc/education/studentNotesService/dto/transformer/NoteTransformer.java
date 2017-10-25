package com.kerneldc.education.studentNotesService.dto.transformer;

import org.springframework.beans.BeanUtils;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;

public class NoteTransformer {

	private NoteTransformer() {
	    throw new IllegalStateException("Cannot instantiate a utility class.");
	  }

	public static NoteDto entityToDto(Note note) {
		NoteDto noteDto = new NoteDto();
		BeanUtils.copyProperties(note, noteDto);
		return noteDto;
	}

	public static Note dtoToEntity(NoteDto noteDto) {
		Note note = new Note();
		BeanUtils.copyProperties(noteDto, note);
		return note;
	}

	public static Note uiDtoToEntity(NoteUiDto noteUiDto) {
		Note note = new Note();
		BeanUtils.copyProperties(noteUiDto, note);
		return note;
	}
}
