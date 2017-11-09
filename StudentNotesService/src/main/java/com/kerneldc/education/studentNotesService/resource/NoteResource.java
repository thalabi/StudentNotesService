package com.kerneldc.education.studentNotesService.resource;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.transformer.NoteTransformer;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.NoteRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.resource.vo.NoteRequestVo;

@Component
@Path("/StudentNotesService/noteResource")
public class NoteResource implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private JpaContext jpaContext;
	
	private EntityManager noteEntityManager;
	private EntityManager studentEntityManager;

	@Override
	public void afterPropertiesSet() {
		noteEntityManager = jpaContext.getEntityManagerByManagedType(Note.class);
		studentEntityManager = jpaContext.getEntityManagerByManagedType(Student.class);
	}

	@POST
	@Path("/addNote")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public NoteRequestVo addNote(
    	NoteRequestVo noteRequestVo) throws SnsException {

    	LOGGER.debug("begin ...");
		Student student = getAndCheckStudentVersion(noteRequestVo.getStudentId(), noteRequestVo.getStudentVersion());
		NoteUiDto noteUiDto = noteRequestVo.getNoteUiDto();
		Note note = NoteTransformer.uiDtoToEntity(noteUiDto);
    	try {
    		noteRepository.save(note);
    		student.getNoteSet().add(note);
    		studentRepository.save(student);
    		// Flush is needed so that note.version is set and student.version is incremented
    		studentEntityManager.flush();
    	} catch (RuntimeException e) {
    		LOGGER.error("Exception encountered: {}", e);
    		throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
    	}
		noteUiDto = NoteTransformer.entityToUiDto(note);
		noteRequestVo.setStudentVersion(student.getVersion());
		noteRequestVo.setNoteUiDto(noteUiDto);
    	LOGGER.debug("end ...");
		return noteRequestVo;
    }

    @POST
	@Path("/updateNote")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public NoteRequestVo updateNote(
    	NoteRequestVo noteRequestVo) throws SnsException {

    	LOGGER.debug("begin ...");
    	Student student = getAndCheckStudentVersion(noteRequestVo.getStudentId(), noteRequestVo.getStudentVersion());
		NoteUiDto noteUiDto = noteRequestVo.getNoteUiDto();
		Note note = getAndCheckNoteVersion(noteUiDto.getId(), noteUiDto.getVersion());
		note.setTimestamp(noteUiDto.getTimestamp());
		note.setText(noteUiDto.getText());
		Note savedNote = null;
    	try {
    		savedNote = noteRepository.save(note);
    		// Flush is needed so that note.version is incremented
    		noteEntityManager.flush();
    	} catch (RuntimeException e) {
    		LOGGER.error("Exception encountered: {}", e);
    		throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
    	}
		noteUiDto = NoteTransformer.entityToUiDto(savedNote);
		noteRequestVo.setStudentVersion(student.getVersion());
		noteRequestVo.setNoteUiDto(noteUiDto);
    	LOGGER.debug("end ...");
		return noteRequestVo;
    }

	@POST
	@Path("/deleteNote")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public NoteRequestVo deleteNote(
    	NoteRequestVo noteRequestVo) throws SnsException {

    	LOGGER.debug("begin ...");
		Student student = getAndCheckStudentVersion(noteRequestVo.getStudentId(), noteRequestVo.getStudentVersion());
		NoteUiDto noteUiDto = noteRequestVo.getNoteUiDto();
		Note note = getAndCheckNoteVersion(noteUiDto.getId(), noteUiDto.getVersion());
		boolean removed = student.getNoteSet().remove(note);
		if (!/* note */removed) {
			throw new SnsRuntimeException("Failed to remove note from noteSet");
		}
    	try {
    		studentRepository.save(student);
    		// Flush is needed so that student.version is incremented
    		studentEntityManager.flush();
    	} catch (RuntimeException e) {
    		LOGGER.error("Exception encountered: {}", e);
    		throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
    	}
		noteRequestVo.setStudentVersion(student.getVersion());
		noteRequestVo.setNoteUiDto(new NoteUiDto());
    	LOGGER.debug("end ...");
		return noteRequestVo;
    }

	private Student getAndCheckStudentVersion(Long studentId, Long studentVersion) throws SnsException {
    	Student student = studentRepository.findOne(studentId);
    	if (student == null) {
    		throw new SnsException(String.format("Student with id: %d no longer exists.", studentId));
    	}
    	if (!/* not */student.getVersion().equals(studentVersion)) {
    		throw new SnsException("Student version has changed.");
    	}
    	return student;
    }
    
    private Note getAndCheckNoteVersion(Long noteId, Long noteVersion) throws SnsException {
    	Note note = noteRepository.findOne(noteId);
    	if (note == null) {
    		throw new SnsException(String.format("Note with id: %d no longer exists.", noteId));
    	}
    	if (!/* not */note.getVersion().equals(noteVersion)) {
    		throw new SnsException("Note version has changed.");
    	}
    	return note;
    }
}
