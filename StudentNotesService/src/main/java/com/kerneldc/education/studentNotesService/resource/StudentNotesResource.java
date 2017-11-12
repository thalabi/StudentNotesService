package com.kerneldc.education.studentNotesService.resource;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;
import com.kerneldc.education.studentNotesService.dto.NoteDto;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.transformer.NoteTransformer;
import com.kerneldc.education.studentNotesService.dto.transformer.SchoolYearTransformer;
import com.kerneldc.education.studentNotesService.dto.transformer.StudentTransformer;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;
import com.kerneldc.education.studentNotesService.exception.RowNotFoundException;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;
import com.kerneldc.education.studentNotesService.repository.GradeRepository;
import com.kerneldc.education.studentNotesService.repository.NoteRepository;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.resource.vo.PrintRequestVo;
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
	private NoteRepository noteRepository;

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

//	@GET
//	@Path("/getStudentDtoById/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public StudentDto getStudentDtoById(
//		@PathParam("id") Long id) throws RowNotFoundException {
//		
//		LOGGER.debug("begin ...");
//		Student student;
//		StudentDto studentDto;
//		try {
//			student = studentRepository.getStudentById(id);
//		} catch (RuntimeException e) {
//			String errorMessage = String.format("Encountered exception while looking up student id %s. Exception is: %s", id, e.getClass().getSimpleName());
//			LOGGER.error(errorMessage);
//			throw new SnsRuntimeException(errorMessage);
//		}
//		if (student == null) {
//			String errorMessage = String.format("Student id %s not found", id);
//			LOGGER.debug(errorMessage);
//			throw new RowNotFoundException(errorMessage);
//		}
//		studentDto = StudentTransformer.entityToDto(student);
//		LOGGER.debug("end ...");
//		return studentDto;
//	}

    // curl -i -H "Content-Type: application/json" -X POST -d '{"id":2,"firstName":"xxxxxxxxxxxxxxxx","lastName":"halabi","grade":"GR-4","noteList":[]}' http://localhost:8080/StudentNotesService
    @POST
	@Path("/addStudentDetails")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public StudentUiDto addStudentDetails(
    	StudentUiDto studentUiDto) {

    	LOGGER.debug("begin ...");
		LOGGER.debug("studentUiDto: {}", studentUiDto);
		Student student = new Student();
		// firstName and lastName
		student.setFirstName(studentUiDto.getFirstName());
		student.setLastName(studentUiDto.getLastName());
		// grade
		SchoolYear schoolYear = schoolYearRepository.findOne(studentUiDto.getSchoolYearUiDto().getId());
		student.addSchoolYear(schoolYear);
		if (studentUiDto.getGradeUiDto().getGradeEnum() != null) {
    		Grade grade = new Grade();
    		grade.setStudent(student);
    		grade.setSchoolYear(schoolYear);
			grade.setGradeEnum(studentUiDto.getGradeUiDto().getGradeEnum());
			student.getGradeSet().add(grade);
		}
    	Student savedSudent;
    	try {
    		savedSudent = studentRepository.save(student);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
    	StudentUiDto savedStudentUiDto =StudentTransformer.entityToUiDto(savedSudent);
    	LOGGER.debug("end ...");
    	return savedStudentUiDto;
    }
	
    @POST
	@Path("/updateStudentDetails")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public StudentUiDto updateStudentDetails(
    	StudentUiDto studentUiDto) {

    	LOGGER.debug("begin ...");
    	//Student student = StudentTransformer.uiDtoToEntity(studentUiDto);
		Student student;
		if (studentUiDto.getId() != null) {
			student = studentRepository.getStudentByIdWithNoteListAndGradeList(studentUiDto.getId());
			checkVersion(studentUiDto.getVersion(), student.getVersion());
		} else {
			student = new Student();
		}
		// firstName and lastName
		student.setFirstName(studentUiDto.getFirstName());
		student.setLastName(studentUiDto.getLastName());
		// grade
		SchoolYear schoolYear = schoolYearRepository.findOne(studentUiDto.getSchoolYearUiDto().getId());
		if (studentUiDto.getGradeUiDto().getGradeEnum() != null) {
			Grade grade = getStudentGradeForYear(student, schoolYear);
			Set<Grade> gradeSet = student.getGradeSet();
			//LOGGER.debug("gradeSet: {}", gradeSet);
			gradeSet.remove(grade);
			grade.setGradeEnum(studentUiDto.getGradeUiDto().getGradeEnum());
			gradeSet.add(grade);
		}
    	Student savedSudent;
    	try {
    		//schoolYearRepository.save(student.getSchoolYearSet().iterator().next());
    		savedSudent = studentRepository.save(student);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
    	StudentUiDto savedStudentUiDto =StudentTransformer.entityToUiDto(savedSudent);
    	LOGGER.debug("end ...");
    	return savedStudentUiDto;
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
//    		for (SchoolYear schoolYear : schoolYearSet) {
//    			student.removeSchoolYear(schoolYear);
//    		}
			studentRepository.delete(id);
		} catch (RuntimeException e) {
			LOGGER.error("Exception encountered: {}", e);
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return "";
	}
	
//    @POST
//	@Path("/updateStudentNotes")
//    @Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//    @Transactional
//    public StudentUiDto updateStudentNotes(
//    	StudentUiDto studentUiDto) {
//
//    	LOGGER.debug("begin ...");
//		LOGGER.debug("studentUiDto: {}", studentUiDto);
//		Student student = studentRepository.getStudentByIdWithNoteListAndGradeList(studentUiDto.getId());
//		Set<Note> noteSet = student.getNoteSet();
//		SchoolYear schoolYear = schoolYearRepository.findOne(studentUiDto.getSchoolYearUiDto().getId());
//		Set<Note> notesNotInSchoolYear = getNotesNotInSchoolYear(noteSet, schoolYear);
//		Set<NoteUiDto> noteUiDtoSet = studentUiDto.getNoteUiDtoSet();
//		Set<Note> newNotes = new HashSet<>(noteUiDtoSet.size());
//		for (NoteUiDto noteUiDto : noteUiDtoSet) {
//			Note note = new Note();
//			note.setTimestamp(noteUiDto.getTimestamp());
//			note.setText(noteUiDto.getText());
//			newNotes.add(note);
//		}
//		student.setNoteSet(notesNotInSchoolYear);
//		student.getNoteSet().addAll(newNotes);
//    	//LOGGER.debug("student: {}", student);
//    	Student savedSudent;
//    	try {
//    		savedSudent = studentRepository.save(student);
//		} catch (RuntimeException e) {
//			LOGGER.error("Exception encountered: {}", e);
//			throw new SnsRuntimeException(e.getClass().getSimpleName());
//		}
//    	StudentUiDto savedStudentUiDto = StudentTransformer.entityToUiDto(savedSudent);
//    	Set<NoteUiDto> noteUiDtosNotInSchoolYear = getNoteUiDtosNotInSchoolYear(savedStudentUiDto.getNoteUiDtoSet(), schoolYear);
//    	savedStudentUiDto.getNoteUiDtoSet().removeAll(noteUiDtosNotInSchoolYear);
//    	savedStudentUiDto.setNoteUiDtoSet(sortNoteUiDtoSet(savedStudentUiDto.getNoteUiDtoSet()));
//    	savedStudentUiDto.setSchoolYearUiDto(studentUiDto.getSchoolYearUiDto());
//    	savedStudentUiDto.setGradeUiDto(studentUiDto.getGradeUiDto());
//    	LOGGER.debug("end ...");
//    	return savedStudentUiDto;
//    }

//    @POST
//	@Path("/updateNote")
//    @Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//    public NoteUiDto updateNote(
//    	NoteRequestVo noteRequestVo) throws SnsException {
//
//    	LOGGER.debug("begin ...");
//		LOGGER.debug("noteRequestVo: {}", noteRequestVo);
//		// check version of student
//		NoteUiDto noteUiDto = noteRequestVo.getNoteUiDto();
//		Note note = noteRepository.findOne(noteUiDto.getId());
//		if (!/* not */note.getVersion().equals(noteUiDto.getVersion())) {
//			throw new SnsException("Note version has changed.");
//		}
//		note.setTimestamp(noteUiDto.getTimestamp());
//		note.setText(noteUiDto.getText());
//		Note savedNote = null;
//    	try {
//    		savedNote = noteRepository.save(note);
//    	} catch (RuntimeException e) {
//    		LOGGER.error("Exception encountered: {}", e);
//    		throw new SnsRuntimeException(e.getClass().getSimpleName());
//    	}
//    	LOGGER.debug("savedNote: {}", savedNote);
//		noteUiDto = NoteTransformer.entityToUiDto(savedNote);
//    	LOGGER.debug("noteUiDto: {}", noteUiDto);
//    	LOGGER.debug("end ...");
//		return noteUiDto;
//    }

//    private Set<Note> getNotesNotInSchoolYear(Set<Note> noteSet, SchoolYear schoolYear) {
//    	Set<Note> notesNotInSchoolYear = new LinkedHashSet<>();
//    	for (Note note : noteSet) {
//    		if (note.getTimestamp().before(schoolYear.getStartDate()) || note.getTimestamp().after(schoolYear.getEndDate())) {
//    			notesNotInSchoolYear.add(note);
//    		}
//    	}
//    	return notesNotInSchoolYear;
//    }
//    
//    private Set<NoteUiDto> getNoteUiDtosNotInSchoolYear(Set<NoteUiDto> noteUiDtoSet, SchoolYear schoolYear) {
//    	Set<NoteUiDto> notesNotInSchoolYear = new LinkedHashSet<>();
//    	for (NoteUiDto noteUiDto : noteUiDtoSet) {
//    		if (noteUiDto.getTimestamp().before(schoolYear.getStartDate()) || noteUiDto.getTimestamp().after(schoolYear.getEndDate())) {
//    			notesNotInSchoolYear.add(noteUiDto);
//    		}
//    	}
//    	return notesNotInSchoolYear;
//    }
    
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
	@Path("/getLatestActiveStudentDtos/{username}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<StudentDto> getLatestActiveStudentDtos(
		@PathParam("username") String username,
		@PathParam("limit") int limit) {
		
		LOGGER.debug("begin ...");
 		Set<Student> students = new HashSet<>();//studentRepository.getLatestActiveStudents(username, limit);
 		Set<StudentDto> studentDtos = new LinkedHashSet<>();
 		for (Student student : students) {
 			StudentDto studentDto = StudentTransformer.entityToDto(student);
 			studentDtos.add(studentDto);
 		}
		LOGGER.debug("end ...");
 		return studentDtos;
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
	@Path("/getStudentGraphBySchoolYear/{schoolYearId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentUiDto> getStudentGraphBySchoolYear(
		@PathParam("schoolYearId") Long schoolYearId) {
		
		LOGGER.debug("begin ...");
		List<StudentUiDto> studentUiDtoList = null;
		try {
			studentUiDtoList = studentRepository.getStudentGraphBySchoolYear(schoolYearId);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return studentUiDtoList;
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
	
	@GET
	@Path("/getStudentsInSchoolYear/{schoolYearId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentUiDto> getStudentsInSchoolYear(
		@PathParam("schoolYearId") Long schoolYearId) throws SnsException {

		LOGGER.debug("begin ...");
		List<StudentUiDto> studentUiDtoList = null;
		try {
			studentUiDtoList = studentRepository.getStudentsInSchoolYear(schoolYearId);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return studentUiDtoList;
	}
	
	@GET
	@Path("/getStudentsNotInSchoolYear/{schoolYearId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StudentUiDto> getStudentsNotInSchoolYear(
		@PathParam("schoolYearId") Long schoolYearId) throws SnsException {

		LOGGER.debug("begin ...");
		List<StudentUiDto> studentUiDtoList = null;
		try {
			studentUiDtoList = studentRepository.getStudentsNotInSchoolYear(schoolYearId);
		} catch (RuntimeException e) {
			throw new SnsRuntimeException(e.getClass().getSimpleName());
		}
		LOGGER.debug("end ...");
		return studentUiDtoList;
	}
	
	private void sortNoteSet(Set<NoteDto> noteSetDto) {
		Comparator<NoteDto> comparator = new Comparator<NoteDto>() {
		    @Override
		    public int compare(NoteDto left, NoteDto right) {
		    	return Long.valueOf(left.getTimestamp().getTime()).compareTo(right.getTimestamp().getTime());
		    }
		};
		List<NoteDto> noteDtoList = new ArrayList<>(noteSetDto);
		LOGGER.debug("before sort");
		for (NoteDto noteDto : noteDtoList) {
			LOGGER.debug("noteDto.getTimestamp(): {}", noteDto.getTimestamp());
		}
		Collections.sort(noteDtoList, comparator);
		LOGGER.debug("after sort");
		for (NoteDto noteDto : noteDtoList) {
			LOGGER.debug("noteDto.getTimestamp(): {}", noteDto.getTimestamp());
		}
		noteSetDto.clear();
		noteSetDto.addAll(new LinkedHashSet<>(noteDtoList));
	}	
	
	private Set<NoteUiDto> sortNoteUiDtoSet(Set<NoteUiDto> noteUiDtoSet) {
		Comparator<NoteUiDto> comparator = new Comparator<NoteUiDto>() {
		    @Override
		    public int compare(NoteUiDto left, NoteUiDto right) {
		    	return Long.valueOf(left.getTimestamp().getTime()).compareTo(right.getTimestamp().getTime());
		    }
		};
		TreeSet<NoteUiDto> sortedSet = new TreeSet<>(comparator);
		sortedSet.addAll(noteUiDtoSet);
		return sortedSet;
	}	
}
