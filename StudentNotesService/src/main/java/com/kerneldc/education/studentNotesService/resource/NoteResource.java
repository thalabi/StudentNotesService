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
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.transformer.NoteTransformer;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.NoteRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.repository.util.RepositoryUtils;
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
		Student student = RepositoryUtils.getAndCheckEntityVersion(noteRequestVo.getStudentId(), noteRequestVo.getStudentVersion(), studentRepository, Student.class);
		NoteDto noteDto = noteRequestVo.getNoteDto();
		Note note = NoteTransformer.dtoToEntity(noteDto);
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
		noteDto = NoteTransformer.entityToDto(note);
		noteRequestVo.setStudentVersion(student.getVersion());
		noteRequestVo.setNoteDto(noteDto);
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
    	Student student = RepositoryUtils.getAndCheckEntityVersion(noteRequestVo.getStudentId(), noteRequestVo.getStudentVersion(), studentRepository, Student.class);
		NoteDto noteDto = noteRequestVo.getNoteDto();
		Note note = RepositoryUtils.getAndCheckEntityVersion(noteDto.getId(), noteDto.getVersion(), noteRepository, Note.class);
		note.setTimestamp(noteDto.getTimestamp());
		note.setText(noteDto.getText());
		Note savedNote = null;
    	try {
    		savedNote = noteRepository.save(note);
    		// Flush is needed so that note.version is incremented
    		noteEntityManager.flush();
    	} catch (RuntimeException e) {
    		LOGGER.error("Exception encountered: {}", e);
    		throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
    	}
		noteDto = NoteTransformer.entityToDto(savedNote);
		noteRequestVo.setStudentVersion(student.getVersion());
		noteRequestVo.setNoteDto(noteDto);
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
		Student student = RepositoryUtils.getAndCheckEntityVersion(noteRequestVo.getStudentId(), noteRequestVo.getStudentVersion(), studentRepository, Student.class);
		NoteDto noteDto = noteRequestVo.getNoteDto();
		Note note = RepositoryUtils.getAndCheckEntityVersion(noteDto.getId(), noteDto.getVersion(), noteRepository, Note.class);
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
		noteRequestVo.setNoteDto(new NoteDto());
    	LOGGER.debug("end ...");
		return noteRequestVo;
    }
}
