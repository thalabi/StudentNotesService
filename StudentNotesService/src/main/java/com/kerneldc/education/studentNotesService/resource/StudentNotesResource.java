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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.transformer.StudentTransformer;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.GradeRepository;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;

@Component
@Path("/StudentNotesService")
public class StudentNotesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private SchoolYearRepository schoolYearRepository;

	@Autowired
	private GradeRepository gradeRepository;

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
	// curl -H -i http://localhost:8080/StudentNotesService/getAllStudents
	// curl -H -i http://localhost:8080/StudentNotesService/getStudentById/1

    // curl -i -H "Content-Type: application/json" -X POST -d '{"id":2,"firstName":"xxxxxxxxxxxxxxxx","lastName":"halabi","grade":"GR-4","noteList":[]}' http://localhost:8080/StudentNotesService
    @POST
	@Path("/addStudentDetails")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public StudentDto addStudentDetails(
    	StudentDto studentDto) {

    	LOGGER.debug("begin ...");
		LOGGER.debug("studentDto: {}", studentDto);
		Student student = new Student();
		// firstName and lastName
		student.setFirstName(studentDto.getFirstName());
		student.setLastName(studentDto.getLastName());
		// grade
		SchoolYear schoolYear = schoolYearRepository.findOne(studentDto.getSchoolYearDto().getId());
		student.addSchoolYear(schoolYear);
		if (studentDto.getGradeDto().getGradeEnum() != null) {
    		Grade grade = new Grade();
    		grade.setStudent(student);
    		grade.setSchoolYear(schoolYear);
			grade.setGradeEnum(studentDto.getGradeDto().getGradeEnum());
			student.getGradeSet().add(grade);
		}
    	Student savedSudent;
    	try {
    		savedSudent = studentRepository.save(student);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
    	StudentDto savedStudentDto =StudentTransformer.entityToDto(savedSudent);
    	LOGGER.debug("end ...");
    	return savedStudentDto;
    }
	
    @POST
	@Path("/updateStudentDetails")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public StudentDto updateStudentDetails(
    	StudentDto studentDto) {

    	LOGGER.debug("begin ...");
    	//Student student = StudentTransformer.uiDtoToEntity(studentUiDto);
		Student student;
		if (studentDto.getId() != null) {
			student = studentRepository.getStudentByIdWithNoteListAndGradeList(studentDto.getId());
			checkVersion(studentDto.getVersion(), student.getVersion());
		} else {
			student = new Student();
		}
		// firstName and lastName
		student.setFirstName(studentDto.getFirstName());
		student.setLastName(studentDto.getLastName());
		// grade
		SchoolYear schoolYear = schoolYearRepository.findOne(studentDto.getSchoolYearDto().getId());
		if (studentDto.getGradeDto().getGradeEnum() != null) {
			Grade grade = getStudentGradeForYear(student, schoolYear);
			Set<Grade> gradeSet = student.getGradeSet();
			//LOGGER.debug("gradeSet: {}", gradeSet);
			gradeSet.remove(grade);
			grade.setGradeEnum(studentDto.getGradeDto().getGradeEnum());
			gradeSet.add(grade);
		}
    	Student savedSudent;
    	try {
    		//schoolYearRepository.save(student.getSchoolYearSet().iterator().next());
    		savedSudent = studentRepository.save(student);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
    	StudentDto savedStudentDto =StudentTransformer.entityToDto(savedSudent);
    	LOGGER.debug("end ...");
    	return savedStudentDto;
    }
	
    private void checkVersion(Long clientVersion, Long entityVersion) {
    	if (clientVersion.compareTo(entityVersion) != 0) {
    		throw new SnsRuntimeException("Entity has been updated by another resource."); 
    	}
    }
    private Grade getStudentGradeForYear(Student student, SchoolYear schoolYear) {
    	List<Grade> gradeList = gradeRepository.findByStudentAndSchoolYear(student, schoolYear);
    	if (CollectionUtils.isNotEmpty(gradeList)) {
    		return gradeList.get(0);
    	} else {
    		Grade grade = new Grade();
    		grade.setStudent(student);
    		grade.setSchoolYear(schoolYear);
    		return grade;
    	}
    }
    
    // TODO check version before deleting. Add /{version} to the url
	@DELETE
	@Path("/deleteStudentById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public String deleteStudentById(
		@PathParam("id") Long id) {
		
		LOGGER.debug("begin ...");
    	try {
    		Student student = studentRepository.getStudentById(id);
    		Set<SchoolYear> schoolYearSet = student.getSchoolYearSet();
    		while (schoolYearSet.iterator().hasNext()) {
    			student.removeSchoolYear(schoolYearSet.iterator().next());
    		}
			studentRepository.delete(id);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
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
	@Path("/getStudentsBySchoolYearFromUserPreference/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public SchoolYear getStudentsBySchoolYearFromUserPreference(
		@PathParam("username") String username) {
		
		LOGGER.debug("begin ...");
		SchoolYear schoolYear = null;
		try {
			schoolYear = studentRepository.getStudentsByUsernameInUserPreference(username);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
		LOGGER.debug("end ...");
		return schoolYear;
	}

	@GET
	@Path("/getStudentGraphBySchoolYear/{schoolYearId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentDto> getStudentGraphBySchoolYear(
		@PathParam("schoolYearId") Long schoolYearId) {
		
		LOGGER.debug("begin ...");
		List<StudentDto> studentDtoList = null;
		try {
			studentDtoList = studentRepository.getStudentGraphBySchoolYear(schoolYearId);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
		LOGGER.debug("end ...");
		return studentDtoList;
	}

	@GET
	@Path("/getStudentsInSchoolYear/{schoolYearId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentDto> getStudentsInSchoolYear(
		@PathParam("schoolYearId") Long schoolYearId) throws SnsException {

		LOGGER.debug("begin ...");
		List<StudentDto> studentDtoList = null;
		try {
			studentDtoList = studentRepository.getStudentsInSchoolYear(schoolYearId);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
		LOGGER.debug("end ...");
		return studentDtoList;
	}
	
	@GET
	@Path("/getStudentsNotInSchoolYear/{schoolYearId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentDto> getStudentsNotInSchoolYear(
		@PathParam("schoolYearId") Long schoolYearId) throws SnsException {

		LOGGER.debug("begin ...");
		List<StudentDto> studentDtoList = null;
		try {
			studentDtoList = studentRepository.getStudentsNotInSchoolYear(schoolYearId);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(ExceptionUtils.getRootCauseMessage(e));
		}
		LOGGER.debug("end ...");
		return studentDtoList;
	}
	
}
