package com.kerneldc.education.studentNotes.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotes.domain.Note;
import com.kerneldc.education.studentNotes.domain.Student;
import com.kerneldc.education.studentNotes.repository.NoteRepository;
import com.kerneldc.education.studentNotes.repository.StudentRepository;

@Component
@Path("/StudentNotesService")
public class StudentNotesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private NoteRepository noteRepository;

	public StudentNotesResource() {
		LOGGER.info("Initialized ...");
	}
	
	@GET
	public String hello() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return "Hello";
	}

	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
	@GET
	@Path("/getAllStudents")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getAllStudents() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return studentRepository.getAllStudents();
	}

	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
	@GET
	@Path("/getAllNotes")
	@Produces(MediaType.APPLICATION_JSON)
	public Iterable<Note> getAllNotes() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return noteRepository.findAll();
	}
	
	// curl -H -i http://localhost:8080/StudentNotesService/getStudentById/1
	@GET
	@Path("/getStudentById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Student getStudentById(
		@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return studentRepository.getStudentById(id);
	}

    // curl -i -H "Content-Type: application/json" -X POST -d '{"id":2,"firstName":"xxxxxxxxxxxxxxxx","lastName":"halabi","grade":"GR-4","noteList":[]}' http://localhost:8080/StudentNotesService
    @POST
	@Path("/saveStudent")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Student saveStudent(
    	Student student) {

    	LOGGER.debug("begin ...");
    	LOGGER.debug("end ...");
    	return studentRepository.save(student);
    }
	
	@DELETE
	@Path("/deleteStudentById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	public void deleteStudentById(
		@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		studentRepository.delete(id);
	}

    @POST
	@Path("/saveNote")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Note saveNote(
    	Note note) {

    	LOGGER.debug("begin ...");
    	LOGGER.debug("end ...");
    	return noteRepository.save(note);
    }
	
	@DELETE
	@Path("/deleteNoteById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	public void deleteNoteById(
		@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		noteRepository.delete(id);
	}
}
