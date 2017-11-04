package com.kerneldc.education.studentNotesService.resource;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.dto.transformer.NoteTransformer;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.NoteRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;

@Component
public class NoteResourceHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private NoteRepository noteRepository;

    @Transactional
    public NoteUiDto updateNoteTransaction(
    	NoteRequestVo noteRequestVo) throws SnsException {

    	LOGGER.debug("begin ...");
		LOGGER.debug("noteRequestVo: {}", noteRequestVo);
		// check version of student
		NoteUiDto noteUiDto = noteRequestVo.getNoteUiDto();
		Note note = noteRepository.findOne(noteUiDto.getId());
		if (!/* not */note.getVersion().equals(noteUiDto.getVersion())) {
			throw new SnsException("Note version has changed.");
		}
		note.setTimestamp(noteUiDto.getTimestamp());
		note.setText(noteUiDto.getText());
		Note savedNote = null;
    	try {
    		savedNote = noteRepository.save(note);
    	} catch (RuntimeException e) {
    		LOGGER.error("Exception encountered: {}", e);
    		throw new SnsRuntimeException(e.getClass().getSimpleName());
    	}
    	LOGGER.debug("savedNote: {}", savedNote);
		noteUiDto = NoteTransformer.entityToUiDto(savedNote);
    	LOGGER.debug("noteUiDto: {}", noteUiDto);
    	LOGGER.debug("end ...");
		return noteUiDto;
    }

}
