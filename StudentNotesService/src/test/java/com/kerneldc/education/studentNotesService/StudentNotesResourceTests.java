package com.kerneldc.education.studentNotesService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.bean.TimestampRange;
import com.kerneldc.education.studentNotesService.constants.Constants;
import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.ui.GradeUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.SchoolYearUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;
import com.kerneldc.education.studentNotesService.junit.MyTestExecutionListener;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.constants.SecurityConstants;
import com.kerneldc.education.studentNotesService.security.util.SimpleGrantedAuthorityMixIn;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(listeners = MyTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class StudentNotesResourceTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	private static final String BASE_URI = "/StudentNotesService";
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private SchoolYearRepository schoolYearRepository;

	@Value("${webapp.username}")
	private String username;
	@Value("${webapp.password}")
	private String password;

	private HttpHeaders httpHeaders;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private User user;
	
	/**
	 * Login and retrieve JWT token and add it to httpHeaders
	 * 
	 * @throws JsonProcessingException
	 */
	@PostConstruct
	public void setUp() throws JsonProcessingException {
		LOGGER.debug("begin ...");
		
		ObjectNode credentialsJsonNode = JsonNodeFactory.instance.objectNode();
		credentialsJsonNode.put("username", username);
		credentialsJsonNode.put("password", password);

		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(credentialsJsonNode,httpHeaders);

		
		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/Security/authenticate", HttpMethod.POST, httpEntity, JsonNode.class);
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		
		JsonNode newJsonUser = response.getBody();
		System.out.println(newJsonUser);

		objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
		user = objectMapper.treeToValue(newJsonUser, User.class);
		LOGGER.debug("user.getToken(): {}", user.getToken());
		httpHeaders.set(SecurityConstants.AUTH_HEADER_NAME, SecurityConstants.AUTH_HEADER_SCHEMA + " " + user.getToken());
		LOGGER.debug("end ...");
	}
	
	@Test
    public void testHello() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello", response.getBody());
    }

	@Test
    public void testGetVersion() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(BASE_URI+"/getVersion", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mock-version", response.getBody());
    }

	@Test
    public void testGetAllStudents() throws JsonProcessingException {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<Student[]> response = testRestTemplate.exchange(BASE_URI+"/getAllStudents", HttpMethod.GET, httpEntity, Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		Student[] students = response.getBody();
        List<Student> allSeedDBStudents = new ArrayList<Student>(Arrays.asList(SeedDBData.s1,SeedDBData.s2,SeedDBData.s3));
        List<Student> studentsList = new ArrayList<Student>(Arrays.asList(students));
        assertEquals(allSeedDBStudents.size(), studentsList.size());
        assertTrue(allSeedDBStudents.removeAll(studentsList));
        assertEquals(0, allSeedDBStudents.size());
    }

	@Test
    public void testGetAllStudentDtos() throws JsonProcessingException {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<StudentDto[]> response = testRestTemplate.exchange(BASE_URI+"/getAllStudentDtos", HttpMethod.GET, httpEntity, StudentDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		StudentDto[] students = response.getBody();
        List<Student> allSeedDBStudents = new ArrayList<>(Arrays.asList(SeedDBData.s1,SeedDBData.s2,SeedDBData.s3));
        List<StudentDto> studentsList = new ArrayList<>(Arrays.asList(students));
        assertEquals(allSeedDBStudents.size(), studentsList.size());
        // TODO fix to create DTO seed data
        assertTrue(allSeedDBStudents.removeAll(studentsList));
        assertEquals(0, allSeedDBStudents.size());
    }

	@Test
    public void testGetStudentById() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/getStudentById/1", HttpMethod.GET, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		Student student = response.getBody();
        assertEquals(SeedDBData.s1, student);
    }
	
	@Test
    public void testGetStudentByIdNotFound() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/getStudentById/777777", HttpMethod.GET, httpEntity, Student.class);
        assertEquals(Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE, response.getStatusCode().value());
    }
	
	@Test
    public void testGetStudentsByTimestampRange() throws JsonProcessingException {
		TimestampRange timestampRange = new TimestampRange();
		LocalDate fromLocalDate = LocalDate.of(2016, 1, 1);
		LocalDate toLocalDate = LocalDate.of(2017, 12, 31);
		timestampRange.setFromTimestamp(Timestamp.valueOf(fromLocalDate.atStartOfDay()));
		timestampRange.setToTimestamp(Timestamp.valueOf(toLocalDate.atStartOfDay()));
		
		JsonNode jsonTimestampRange = objectMapper.valueToTree(timestampRange);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonTimestampRange,httpHeaders);
		
		ResponseEntity<Student[]> response = testRestTemplate.exchange(BASE_URI+"/getStudentsByTimestampRange", HttpMethod.POST, httpEntity, Student[].class);
		
		List<Student> studentsInTimestampRange = Arrays.asList(response.getBody());

		List<Note> notesInTimestampRange = studentsInTimestampRange.stream()
				.map(student->student.getNoteSet())
				.flatMap(note->note.stream()).collect(Collectors.toList());
		System.out.println("-------------------------->"+notesInTimestampRange.size());
		assertTrue(
			notesInTimestampRange.stream().allMatch(
					note->note.getTimestamp().compareTo(timestampRange.getFromTimestamp()) >= 0 &&
							note.getTimestamp().compareTo(timestampRange.getToTimestamp()) <= 0)
			);
		
		
		List<Student> allStudents = studentRepository.getAllStudents();
		List<Note> notesNotInTimestampRange = allStudents.stream()
				.map(student->student.getNoteSet())
				.flatMap(note->note.stream()).collect(Collectors.toList());
		boolean someRemoved = notesNotInTimestampRange.removeAll(notesInTimestampRange);
		System.out.println("someRemoved: "+someRemoved);
		System.out.println("notesInTimestampRange: "+notesInTimestampRange);
		System.out.println("notesNotInTimestampRange: "+notesNotInTimestampRange);
		System.out.println("==========================>"+notesNotInTimestampRange.size());
		assertTrue(
				notesNotInTimestampRange.stream().allMatch(
						note->note.getTimestamp().compareTo(timestampRange.getFromTimestamp()) == -1 &&
								note.getTimestamp().compareTo(timestampRange.getToTimestamp()) == 1)
				);
	}

	@Test
	public void testGetAllStudentsWithoutNotesList() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<Student[]> response = testRestTemplate.exchange(BASE_URI+"/getAllStudentsWithoutNotesList", HttpMethod.GET, httpEntity, Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Student[] students = response.getBody();
        List<Student> allSeedDBStudents = new ArrayList<Student>(Arrays.asList(SeedDBData.s1,SeedDBData.s2,SeedDBData.s3));
        for (Student s: allSeedDBStudents) {
        	s.setNoteSet(null);
        }
        List<Student> studentsList = new ArrayList<Student>(Arrays.asList(students));
        assertEquals(allSeedDBStudents.size(), studentsList.size());
        assertTrue(allSeedDBStudents.removeAll(studentsList));
        assertEquals(0, allSeedDBStudents.size());
	}

	@Test
	@DirtiesContext
    public void testSaveStudentUiDtoWithFirstAndLastName() {
		
		StudentUiDto studentUiDto = new StudentUiDto();
		studentUiDto.setFirstName("first name");
		studentUiDto.setLastName("last name");
		HttpEntity<StudentUiDto> httpEntity = new HttpEntity<StudentUiDto>(studentUiDto,httpHeaders);
		ResponseEntity<StudentUiDto> response = testRestTemplate.exchange(BASE_URI+"/saveStudentUiDto", HttpMethod.POST, httpEntity, StudentUiDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		StudentUiDto savedStudentUiDto = response.getBody();
		assertThat(savedStudentUiDto.getId(), notNullValue());
		assertThat(savedStudentUiDto.getFirstName(), equalTo(studentUiDto.getFirstName()));
		assertThat(savedStudentUiDto.getLastName(), equalTo(studentUiDto.getLastName()));
		//assertThat(savedStudentUiDto.getGradeEnum(), equalTo(studentUiDto.getGradeEnum()));
		assertThat(savedStudentUiDto.getNoteUiDtoSet(), equalTo(studentUiDto.getNoteUiDtoSet()));
		assertThat(savedStudentUiDto.getVersion(), equalTo(0l));
    }

	@Test
	@DirtiesContext
	public void testSaveStudentAddNote() {
		
		Student student = new Student();
		student = SerializationUtils.clone(SeedDBData.s3);
		Note newNote = new Note();
		newNote.setTimestamp(new Timestamp(1481403630843l));
		newNote.setText("note new note");
		student.setNoteSet(
				new HashSet<>(student.getNoteSet())
		);
		student.getNoteSet().add(newNote);
		
		
		
		HttpEntity<Student> httpEntity = new HttpEntity<Student>(student,httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Student savedStudent = response.getBody();
        assertTrue(savedStudent.equals(student));
        assertTrue(savedStudent.getNoteSet().size() == 3);
        assertTrue(savedStudent.getVersion().equals(student.getVersion()+1));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteSet().iterator().next(), student.getNoteSet().iterator().next(), true));
//        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(1), student.getNoteList().get(1), true));
//        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(2), newNote, "id", "version"));
//        assertTrue(savedStudent.getNoteList().get(2).getId().compareTo(0l) > 0 &&
//        		savedStudent.getNoteList().get(2).getVersion().compareTo(0l) == 0);
    }
	
	@Test
	@DirtiesContext
    public void testSaveStudentUiDtoWithOneNote() {
		
		StudentUiDto studentUiDto = new StudentUiDto();
		studentUiDto.setFirstName("first name one nore");
		studentUiDto.setLastName("last name one nore");
		NoteUiDto noteUiDto = new NoteUiDto();
		noteUiDto.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		noteUiDto.setText("new note");
		studentUiDto.addNoteUiDto(noteUiDto);

		HttpEntity<StudentUiDto> httpEntity = new HttpEntity<StudentUiDto>(studentUiDto,httpHeaders);
		ResponseEntity<StudentUiDto> response = testRestTemplate.exchange(BASE_URI+"/saveStudentUiDto", HttpMethod.POST, httpEntity, StudentUiDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		StudentUiDto savedStudentUiDto = response.getBody();
		assertThat(savedStudentUiDto.getId(), notNullValue());
		assertThat(savedStudentUiDto.getFirstName(), equalTo(studentUiDto.getFirstName()));
		assertThat(savedStudentUiDto.getLastName(), equalTo(studentUiDto.getLastName()));
		//assertThat(savedStudentUiDto.getGradeEnum(), equalTo(studentUiDto.getGradeEnum()));
		assertThat(savedStudentUiDto.getVersion(), equalTo(0l));
		assertThat(savedStudentUiDto.getNoteUiDtoSet(), hasSize(studentUiDto.getNoteUiDtoSet().size()));
		NoteUiDto sabedNoteUiDto = savedStudentUiDto.getNoteUiDtoSet().iterator().next();
		assertThat(sabedNoteUiDto.getId(), notNullValue());
		assertThat(sabedNoteUiDto.getTimestamp(), equalTo(noteUiDto.getTimestamp()));
		assertThat(sabedNoteUiDto.getText(), equalTo(noteUiDto.getText()));
		assertThat(sabedNoteUiDto.getVersion(), equalTo(0l));
    }

	@Test
	public void testSaveStudentDeleteNote() {
		
		Student student = new Student();
		student.setFirstName("new first name with notes t07testSaveStudentDeleteNote");
		student.setLastName("new last name with notes t07testSaveStudentDeleteNote");
		//student.setGrade(GradeEnum.TWO);
		Note note1 = new Note();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse("2017-01-22 20:08", formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText("new note1 text t07testSaveStudentDeleteNote");

		student.getNoteSet().add(note1);
		studentRepository.save(student);
		
		student.getNoteSet().remove(0);
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

		Student newStudent = response.getBody();
		assertTrue(
			newStudent.getNoteSet().size() == 0);
    }
	
	@Test
	@DirtiesContext
	public void testSaveStudentModifyNote() {
		
//		String data = "{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified\"}]}";
//		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);
//
//		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, String.class);
//		String expected = "{\"version\":1,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":1,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified\"}]}";
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(expected, response.getBody());
        
		Student student = new Student();
		student = SerializationUtils.clone(SeedDBData.s1);
		Iterator<Note> ni = student.getNoteSet().iterator();
		ni.next();
		ni.next();
		Note note = ni.next();
		note.setText(note.getText()+" modified");
		HttpEntity<Student> httpEntity = new HttpEntity<Student>(student,httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Student savedStudent = response.getBody();
        assertTrue(savedStudent.equals(student));
        assertTrue(savedStudent.getNoteSet().size() == 3);
        assertTrue(savedStudent.getVersion().equals(student.getVersion()+1));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteSet().iterator().next(), student.getNoteSet().iterator().next(), true));
//        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(1), student.getNoteList().get(1), true));
//        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(2), student.getNoteList().get(2), "version"));
//        assertTrue(savedStudent.getNoteList().get(2).getVersion().equals(student.getNoteList().get(2).getVersion()+1));
    }

	@Test
	@DirtiesContext
	public void testDeleteStudentById() {
		
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/deleteStudentById/3", HttpMethod.DELETE, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		Student student = studentRepository.getStudentById(3l);
		assertEquals("student should not exist", null, student);
	}

	@Test
	public void testDeleteStudentByIdNotFound() {
		
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/deleteStudentById/777777", HttpMethod.DELETE, httpEntity, Student.class);
        assertEquals(Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE, response.getStatusCode().value());
	}

	@Test
	@DirtiesContext
	public void testSaveStudentAddStudent() {
		
		Student student = new Student();
		student.setFirstName("new first name");
		student.setLastName("new last name");
		//student.setGrade(GradeEnum.ONE);
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		Student newStudent = response.getBody();
		
		assertTrue(student.getFirstName().equals(newStudent.getFirstName()) &&
				student.getLastName().equals(newStudent.getLastName()) &&
				//student.getGrade().equals(newStudent.getGrade()) &&
				newStudent.getId().compareTo(0l) > 0 &&
				newStudent.getVersion().equals(0l));
	}

	@Test
	@DirtiesContext
	public void testSaveStudentAddStudentWithNotes() {
		
		Student student = new Student();
		student.setFirstName("new first name with notes");
		student.setLastName("new last name with notes");
		Grade grade = new Grade();
		grade.setGradeEnum(GradeEnum.TWO);
		grade.setStudent(student);
		grade.setSchoolYear(schoolYearRepository.findOne(1l));
		student.getGradeSet().add(grade);

		//student.setGrade(GradeEnum.TWO);
		Note note1 = new Note();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse("2017-01-22 20:08", formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText("new note1 text");
		Note note2 = new Note();
		LocalDateTime timestamp2 = LocalDateTime.parse("2017-01-22 20:18", formatter);
		note2.setTimestamp(Timestamp.valueOf(timestamp2));
		note2.setText("new note2 text");

		student.setNoteSet(new HashSet<>(Arrays.asList(note1, note2)));
		//JsonNode jsonStudent = objectMapper.valueToTree(student);
		String jsonStudent = "";
		try {
			jsonStudent = objectMapper.writerWithView(View.GradeExtended.class).writeValueAsString(student);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Unable to serialize student object to JSON, exception: "+e.getMessage());
		}

		HttpEntity<String> httpEntity = new HttpEntity<String>(jsonStudent,httpHeaders);

		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		
		Student newStudent = response.getBody();
		
		assertTrue(student.getFirstName().equals(newStudent.getFirstName()) &&
				student.getLastName().equals(newStudent.getLastName()) &&
				//student.getGrade().equals(newStudent.getGrade()) &&
				newStudent.getId().compareTo(0l) > 0 &&
				newStudent.getVersion().equals(0l) &&
				newStudent.getNoteSet().size() == 2);

		boolean foundNoteOne = false;
		boolean foundNoteTwo = false;
		for (Note note: newStudent.getNoteSet()) {
			if (!foundNoteOne) {
				foundNoteOne = note.getId().compareTo(0l) > 0 &&
						note.getTimestamp().equals(note1.getTimestamp()) &&
						note.getText().equals(note1.getText()) &&
						note.getVersion().equals(0l);
			}
			if (!foundNoteTwo) {
				foundNoteTwo = note.getId().compareTo(0l) > 0 &&
						note.getTimestamp().equals(note2.getTimestamp()) &&
						note.getText().equals(note2.getText()) &&
						note.getVersion().equals(0l);
			}
		}
		
		assertTrue(foundNoteOne && foundNoteTwo);
	}

	@Test
    public void testGetLatestActiveStudents() {
		//TODO
	}

	@Test
    public void testPdfAll() {
		//TODO
	}
	@Test
    public void t12testPdfStudentsByTimestampRange() {
		//TODO
	}
	
	@Test
	public void testResourceDoesNotExist() {
		
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/ResourceDoesNotExist",
				HttpMethod.GET, httpEntity, String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testNoteEquality() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);

		ResponseEntity<Student> response1a = testRestTemplate.exchange(BASE_URI+"/getStudentById/1", HttpMethod.GET, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response1a.getStatusCode());
		Note note1a = response1a.getBody().getNoteSet().iterator().next();
		
		ResponseEntity<Student> response1b = testRestTemplate.exchange(BASE_URI+"/getStudentById/1", HttpMethod.GET, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response1b.getStatusCode());
		Note note1b = response1b.getBody().getNoteSet().iterator().next();

		assertEquals(note1a.getId(), note1b.getId());
		assertEquals(note1a.getTimestamp(), note1b.getTimestamp());
		assertEquals(note1a.getText(), note1b.getText());
		assertEquals(note1a.getVersion(), note1b.getVersion());

		assertEquals(note1a, note1b);
	}
	
	@Test
	public void testGetStudentDtosInSchoolYear() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<StudentDto[]> response = testRestTemplate.exchange(BASE_URI+"/getStudentDtosInSchoolYear/1", HttpMethod.GET, httpEntity, StudentDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		StudentDto[] students = response.getBody();
		assertThat(students.length, equalTo(2));
	}
	@Test
	public void testGetStudentDtosNotInSchoolYear() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<StudentDto[]> response = testRestTemplate.exchange(BASE_URI+"/getStudentDtosNotInSchoolYear/1", HttpMethod.GET, httpEntity, StudentDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		StudentDto[] students = response.getBody();
		assertThat(students.length, equalTo(1));
	}
	
	@Test
	@DirtiesContext
    public void testSaveStudentChangeFirstLastNameAndGrade() {
		
        Student student = studentRepository.getStudentById(2l);
        student.setFirstName(student.getFirstName()+" v1");
        student.setLastName(student.getLastName()+" v1");
        LOGGER.debug("student: {}", student);

		String studentJson = "";
		try {
			studentJson = objectMapper.writerWithView(View.Default.class).writeValueAsString(student);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Unable to serialize student object to JSON");
		}

		HttpEntity<String> httpEntity = new HttpEntity<>(studentJson,httpHeaders);
        ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Student newStudent = response.getBody();
        assertTrue(newStudent.getId().equals(student.getId()) &&
        		newStudent.getFirstName().equals(student.getFirstName()) &&
        		newStudent.getLastName().equals(student.getLastName()) &&
        		//newStudent.getGrade().equals(student.getGrade()) &&
        		newStudent.getVersion().equals(student.getVersion()+1) &&
        		newStudent.getNoteSet().size() == student.getNoteSet().size());
    }

	@Test
	@DirtiesContext
	@Commit
    public void testSaveStudentUiDtoWithGrade() {
		
		StudentUiDto studentUiDto = new StudentUiDto();
		studentUiDto.setFirstName("first name");
		studentUiDto.setLastName("last name");
		GradeUiDto gradeUiDto = new GradeUiDto();
		gradeUiDto.setGradeEnum(GradeEnum.SEVEN);
		studentUiDto.setGradeUiDto(gradeUiDto);
		SchoolYearUiDto schoolYearUiDto = new SchoolYearUiDto();
		schoolYearUiDto.setId(2l);
		schoolYearUiDto.setVersion(0l);
		studentUiDto.setSchoolYearUiDto(schoolYearUiDto);
		HttpEntity<StudentUiDto> httpEntity = new HttpEntity<StudentUiDto>(studentUiDto,httpHeaders);
		ResponseEntity<StudentUiDto> response = testRestTemplate.exchange(BASE_URI+"/saveStudentUiDto", HttpMethod.POST, httpEntity, StudentUiDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		StudentUiDto savedStudentUiDto = response.getBody();
		assertThat(savedStudentUiDto.getId(), notNullValue());
		assertThat(savedStudentUiDto.getFirstName(), equalTo(studentUiDto.getFirstName()));
		assertThat(savedStudentUiDto.getLastName(), equalTo(studentUiDto.getLastName()));
		//assertThat(savedStudentUiDto.getGradeEnum(), equalTo(studentUiDto.getGradeEnum()));
		assertThat(savedStudentUiDto.getNoteUiDtoSet(), equalTo(studentUiDto.getNoteUiDtoSet()));
		//assertThat(savedStudentUiDto.getSchoolYear(), equalTo(studentUiDto.getSchoolYear()));
		assertThat(savedStudentUiDto.getVersion(), equalTo(0l));
    }

	@Test
	public void testgetStudentsByUsername() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<StudentUiDto[]> response = testRestTemplate.exchange(BASE_URI+"/getStudentsByUsername/TestUser", HttpMethod.GET, httpEntity, StudentUiDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        StudentUiDto[] students = response.getBody();
		assertThat(students.length, equalTo(3));
	}
}
