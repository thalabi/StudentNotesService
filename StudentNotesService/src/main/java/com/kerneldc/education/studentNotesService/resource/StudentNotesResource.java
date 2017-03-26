package com.kerneldc.education.studentNotesService.resource;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.bean.TimestampRange;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.exception.RowNotFoundException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.service.StudentNotesReportService;

@Component
@Path("/StudentNotesService")
public class StudentNotesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private StudentNotesReportService studentNotesReportService;
	@Value("${version}")
	private String version;

	public StudentNotesResource() {
		LOGGER.info("Initialized ...");
	}
	
	@GET
	public String hello() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return "Hello";
	}

	@GET
	@Path("/getVersion")
	public String getVersion() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return version;
	}

	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
	@GET
	@Path("/getAllStudents")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getAllStudents() {
		
		LOGGER.debug("begin ...");
		List<Student> students = null;
		try {
			students = studentRepository.getAllStudents();
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return students;
	}

	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
	// curl -H -i http://localhost:8080/StudentNotesService/getStudentById/1
	@GET
	@Path("/getStudentById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Student getStudentById(
		@PathParam("id") Long id) throws RowNotFoundException {
		
		LOGGER.debug("begin ...");
		Student student = null;
		try {
			student = studentRepository.getStudentById(id);
		} catch (RuntimeException e) {
			String errorMessage = String.format("Encountered exception while looking up student id %s. Exception is: %s", id, e.getClass().getSimpleName());
			LOGGER.error(errorMessage);
			throw new SnsRuntimeException(errorMessage);
		}
		if (student == null) {
			String errorMessage = String.format("Student id %s not found", id);
			LOGGER.debug(errorMessage);
			throw new RowNotFoundException(errorMessage);
		}
		LOGGER.debug("end ...");
		return student;
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
		    	int bytesRead;
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
	public Set<Student> getLatestActiveStudents(
		@PathParam("limit") int limit) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return studentRepository.getLatestActiveStudents(limit);
	}

	@POST
	@Path("/getStudentsByTimestampRange")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Student> getStudentsByTimestampRange(TimestampRange timestampRange) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("timestampRange: {}", timestampRange);
		LOGGER.debug("end ...");
		return studentRepository.getStudentsByTimestampRange(timestampRange.getFromTimestamp(), timestampRange.getToTimestamp());
	}

	@POST
	@Path("/pdfStudentsByTimestampRange")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response pdfStudentsByTimestampRange(TimestampRange timestampRange) throws JAXBException, ParserConfigurationException, SAXException, IOException, TransformerException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getStudentsByTimestampRange(timestampRange.getFromTimestamp(), timestampRange.getToTimestamp()));
		byte[] pdfByteArray = null;
		if (students.getStudentList().size() != 0) {
			pdfByteArray = studentNotesReportService.generateReport(students);
		}
		// TODO print an empty pdf when there are no students returned
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}
	
	@GET
	@Path("/getAllStudentsWithoutNotesList")
	@Produces(MediaType.APPLICATION_JSON)
	public Iterable<Student> getAllStudentsWithoutNotesList() {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		//return studentRepository.findAll();
		return studentRepository.findAllByOrderByFirstNameAscLastNameAsc();
	}

	// TODO not covered by a test case
	@POST
	@Path("/pdfStudentsByStudentIds")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response pdfStudentsByStudentIds(List<Long> studentIds) throws JAXBException, ParserConfigurationException, SAXException, IOException, TransformerException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getStudentsByListOfIds(studentIds));
		byte[] pdfByteArray = null;
		if (students.getStudentList().size() != 0) {
			pdfByteArray = studentNotesReportService.generateReport(students);
		}
		// TODO print an empty pdf when there are no students returned
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}
}
