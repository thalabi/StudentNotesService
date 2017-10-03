package com.kerneldc.education.studentNotesService.resource;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.bean.TimestampRange;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.transformer.SchoolYearTransformer;
import com.kerneldc.education.studentNotesService.dto.transformer.StudentTransformer;
import com.kerneldc.education.studentNotesService.exception.RowNotFoundException;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.service.PdfStudentNotesReportService;

@Component
@Path("/StudentNotesService")
public class StudentNotesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private SchoolYearRepository schoolYearRepository;

	@Autowired
	private PdfStudentNotesReportService pdfStudentNotesReportService;
	
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
		List<Student> students;
		try {
			students = studentRepository.getAllStudents();
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return students;
	}

	@GET
	@Path("/getAllStudentDtos")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentDto> getAllStudentDtos() {
		
		LOGGER.debug("begin ...");
		List<Student> students;
		try {
			students = studentRepository.getAllStudents();
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		List<StudentDto> studentDtos = new ArrayList<>();
 		for (Student student : students) {
 			StudentDto studentDto = StudentTransformer.entityToDto(student);
 			studentDtos.add(studentDto);
 		}		
		LOGGER.debug("end ...");
		return studentDtos;
	}

	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
	// curl -H -i http://localhost:8080/StudentNotesService/getStudentById/1
	@GET
	@Path("/getStudentById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.Default.class)
	public Student getStudentById(
		@PathParam("id") Long id) throws RowNotFoundException {
		
		LOGGER.debug("begin ...");
		Student student;
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

	@GET
	@Path("/getStudentDtoById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public StudentDto getStudentDtoById(
		@PathParam("id") Long id) throws RowNotFoundException {
		
		LOGGER.debug("begin ...");
		Student student;
		StudentDto studentDto;
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
		studentDto = StudentTransformer.entityToDto(student);
		LOGGER.debug("end ...");
		return studentDto;
	}

    // curl -i -H "Content-Type: application/json" -X POST -d '{"id":2,"firstName":"xxxxxxxxxxxxxxxx","lastName":"halabi","grade":"GR-4","noteList":[]}' http://localhost:8080/StudentNotesService
    @POST
	@Path("/saveStudent")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.Default.class)
    public Student saveStudent(
    	Student student) {

    	LOGGER.debug("begin ...");
    	Student savedSudent;
    	try {
    		savedSudent = studentRepository.save(student);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
    	LOGGER.debug("end ...");
    	return savedSudent;
    }
	
    @POST
	@Path("/saveStudentDto")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public StudentDto saveStudentDto(
    	StudentDto studentDto) {

    	LOGGER.debug("begin ...");
    	Student student = StudentTransformer.dtoToEntity(studentDto);
    	Student savedStudent;
    	StudentDto savedStudentDto;
    	try {
    		savedStudent = studentRepository.save(student);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
    	savedStudentDto = StudentTransformer.entityToDto(savedStudent);
    	sortNoteList(savedStudentDto.getNoteDtoList());
    	LOGGER.debug("savedStudentDto: {}", savedStudentDto);
    	LOGGER.debug("end ...");
    	return savedStudentDto;
    }
	
	@DELETE
	@Path("/deleteStudentById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteStudentById(
		@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
    	try {
			studentRepository.delete(id);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return "";
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
	public Response pdfAll() throws SnsException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getAllStudents());
		byte[] pdfByteArray = pdfStudentNotesReportService.generateReport(students);
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}

	@GET
	@Path("/getLatestActiveStudents/{username}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Student> getLatestActiveStudents(
		@PathParam("username") String username,
		@PathParam("limit") int limit) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
		return studentRepository.getLatestActiveStudents(username, limit);
	}

	@GET
	@Path("/getLatestActiveStudentDtos/{username}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<StudentDto> getLatestActiveStudentDtos(
		@PathParam("username") String username,
		@PathParam("limit") int limit) {
		
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
 		Set<Student> students = studentRepository.getLatestActiveStudents(username, limit);
 		Set<StudentDto> studentDtos = new LinkedHashSet<>();
 		for (Student student : students) {
 			StudentDto studentDto = StudentTransformer.entityToDto(student);
 			studentDtos.add(studentDto);
 		}
 		return studentDtos;
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
	public Response pdfStudentsByTimestampRange(TimestampRange timestampRange) throws SnsException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getStudentsByTimestampRange(timestampRange.getFromTimestamp(), timestampRange.getToTimestamp()));
		byte[] pdfByteArray = null;
		if (students.getStudentList().size() != 0) {
			pdfByteArray = pdfStudentNotesReportService.generateReport(students);
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
	public Response pdfStudentsByStudentIds(List<Long> studentIds) throws SnsException {

		LOGGER.debug("begin ...");
		Students students = new Students();
		students.setStudentList(studentRepository.getStudentsByListOfIds(studentIds));
		byte[] pdfByteArray = null;
		if (students.getStudentList().size() != 0) {
			pdfByteArray = pdfStudentNotesReportService.generateReport(students);
		}
		// TODO print an empty pdf when there are no students returned
		LOGGER.debug("end ...");
		return Response.ok(pdfByteArray).build();
	}

	@GET
	@Path("/getStudentsBySchoolYearFromUserPreference/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(View.SchoolYearExtended.class)
	public SchoolYear getStudentsBySchoolYearFromUserPreference(
		@PathParam("username") String username) {
		
		LOGGER.debug("begin ...");
		SchoolYear schoolYear = null;
		try {
			schoolYear = studentRepository.getStudentsByUsernameInUserPreference(username);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return schoolYear;
	}

	@GET
	@Path("/getStudentDtosBySchoolYearFromUserPreference/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	//@JsonView(View.SchoolYearExtended.class)
	public SchoolYearDto getStudentDtosBySchoolYearFromUserPreference(
		@PathParam("username") String username) {
		
		LOGGER.debug("begin ...");
		SchoolYear schoolYear = null;
		try {
			schoolYear = studentRepository.getStudentsByUsernameInUserPreference(username);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		SchoolYearDto schoolYearDto = SchoolYearTransformer.entityToDto(schoolYear);
		LOGGER.debug("end ...");
		return schoolYearDto;
	}
	
	@GET
	@Path("/getStudentDtosInSchoolYear/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentDto> getStudentDtosInSchoolYear(
		@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
		List<StudentDto> students = null;
		try {
			students = studentRepository.getStudentDtosInSchoolYear(id);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return students;
	}
	
	@GET
	@Path("/getStudentDtosNotInSchoolYear/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentDto> getStudentDtosNotInSchoolYear(
		@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
		List<StudentDto> students = null;
		try {
			students = studentRepository.getStudentDtosNotInSchoolYear(id);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return students;
	}
	
	
	@POST
	@Path("/saveRemoveStudentsToFromSchoolYear")
    @Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public String saveRemoveStudentsToFromSchoolYear(
		SaveRemoveStudentsToFromSchoolYearVO saveRemoveStudentsToFromSchoolYearVO) {
		
		LOGGER.debug("begin ...");
		SchoolYear schoolYear = schoolYearRepository.findOne(saveRemoveStudentsToFromSchoolYearVO.getSchoolYearId());
		LOGGER.debug("saveRemoveStudentsToFromSchoolYearVO: {}", saveRemoveStudentsToFromSchoolYearVO);
		List<Student> oldSchoolYearStudents = saveRemoveStudentsToFromSchoolYearVO.getOldSchoolYearStudents();
		List<Student> newSchoolYearStudents = saveRemoveStudentsToFromSchoolYearVO.getNewSchoolYearStudents();
		List<Student> copyOfNewSchoolYearStudents = new ArrayList<>(newSchoolYearStudents);
		newSchoolYearStudents.removeAll(oldSchoolYearStudents);
		oldSchoolYearStudents.removeAll(copyOfNewSchoolYearStudents);
		LOGGER.debug("Students to be added: {}", newSchoolYearStudents);
		LOGGER.debug("Students to be removed: {}", oldSchoolYearStudents);
		for (Student student : newSchoolYearStudents) {
			student = studentRepository.findOne(student.getId());
			student.addSchoolYear(schoolYear);
			studentRepository.save(student);
		}
		for (Student student : oldSchoolYearStudents) {
			student = studentRepository.findOne(student.getId());
			student.removeSchoolYear(schoolYear);
			studentRepository.save(student);
		}
		LOGGER.debug("end ...");
		return "";
	}

	private void sortNoteList(List<NoteDto> noteListDto) {
		Comparator<NoteDto> comparator = new Comparator<NoteDto>() {
		    @Override
		    public int compare(NoteDto left, NoteDto right) {
		    	return Long.valueOf(left.getTimestamp().getTime()).compareTo(right.getTimestamp().getTime());
		    }
		};
		Collections.sort(noteListDto, comparator);
	}
}
