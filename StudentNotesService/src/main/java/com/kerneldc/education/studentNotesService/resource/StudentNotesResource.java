package com.kerneldc.education.studentNotesService.resource;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.repository.NoteRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.service.StudentNotesReportService;

@Component
@Path("/StudentNotesService")
public class StudentNotesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private NoteRepository noteRepository;
	@Autowired
	private StudentNotesReportService studentNotesReportService;

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
	
	@GET
	@Path("/pdfAllTestFile")
	@Produces("application/pdf")
	public Response pdfAllTestFile() {

		LOGGER.debug("begin ...");
		StreamingOutput stream = new StreamingOutput() {
		    @Override
		    public void write(OutputStream os) throws IOException {
		    	Writer writer = new BufferedWriter(new OutputStreamWriter(os));

		    	BufferedInputStream pdfFile = new BufferedInputStream(new FileInputStream("C://Downloads/CGQGD_KS002015041220280700-122352.pdf"));
		    	byte[] contents = new byte[1024];
		    	int bytesRead=0;
                String stringContents;
                
                while( (bytesRead = pdfFile.read(contents)) != -1){
                       
                        stringContents = new String(contents, 0, bytesRead);
                        writer.write(stringContents);
                }
		    		
		    	pdfFile.close();
		    	writer.flush();
		    }
		};		
		LOGGER.debug("end ...");
		return Response.ok(stream).build();
	}
	
	@GET
	@Path("/pdfAll")
	@Produces("application/pdf")
	public Response pdfAll() throws JAXBException, ParserConfigurationException, SAXException, IOException, TransformerException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getAllStudents());
		byte[] pdfByteArray = studentNotesReportService.generateReport(students);
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}

	@GET
	@Path("/getLatestActiveStudents/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getLatestActiveStudents(
		@PathParam("limit") int limit) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return studentRepository.getLatestActiveStudents(limit);
	}

}