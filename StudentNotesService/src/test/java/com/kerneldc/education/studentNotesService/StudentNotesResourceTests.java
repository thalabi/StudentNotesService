package com.kerneldc.education.studentNotesService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kerneldc.education.studentNotesService.bean.Grade;
import com.kerneldc.education.studentNotesService.bean.TimestampRange;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.junit.MyTestExecutionListener;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.constants.Constants;
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
		httpHeaders.set(Constants.AUTH_HEADER_NAME, Constants.AUTH_HEADER_SCHEMA + " " + user.getToken());
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
		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/getAllStudents", HttpMethod.GET, httpEntity, JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		Student[] students = objectMapper.treeToValue(response.getBody(), Student[].class);
		assertEquals(3, students.length);
        for (Student s: students) {
        	if (s.getId().equals(SeedDBData.s1.getId())) {
        		EqualsBuilder.reflectionEquals(SeedDBData.s1, s);
        	} else if (s.getId().equals(SeedDBData.s2.getId())) {
        			EqualsBuilder.reflectionEquals(SeedDBData.s2, s);
            	} else if (s.getId().equals(SeedDBData.s3.getId())) {
        			EqualsBuilder.reflectionEquals(SeedDBData.s3, s);
                	}
        }		
    }

	@Test
    public void testGetStudentById() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/getStudentById/1", HttpMethod.GET, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
		Student student = response.getBody();
        assertTrue(EqualsBuilder.reflectionEquals(student, SeedDBData.s1, "noteList"));
        assertTrue(student.getNoteList().size() == 3);
        assertTrue(EqualsBuilder.reflectionEquals(student.getNoteList().get(0), SeedDBData.s1.getNoteList().get(0), true));
        assertTrue(EqualsBuilder.reflectionEquals(student.getNoteList().get(1), SeedDBData.s1.getNoteList().get(1), true));
        assertTrue(EqualsBuilder.reflectionEquals(student.getNoteList().get(2), SeedDBData.s1.getNoteList().get(2), true));
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
		
		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/getStudentsByTimestampRange", HttpMethod.POST, httpEntity, JsonNode.class);
		
		Student[] students = objectMapper.treeToValue(response.getBody(), Student[].class);
		
		for (Student student: students) LOGGER.debug("student: {}", student); 
		assertEquals(2, students.length);
		
//		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/getAllNotes", HttpMethod.GET, httpEntity, String.class);
//        String expected = "[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"},{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"}]";
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(expected, response.getBody());
	}

	@Test
	public void testGetAllStudentsWithoutNotesList() throws JsonProcessingException {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/getAllStudentsWithoutNotesList", HttpMethod.GET, httpEntity, JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Student[] students = objectMapper.treeToValue(response.getBody(), Student[].class);
        LOGGER.debug("students.length: "+students.length);
        for (Student s: students) {
        	if (s.getId().equals(SeedDBData.s1.getId())) {
        		assertEquals(SeedDBData.s1.getFirstName(), s.getFirstName());
        		assertEquals(SeedDBData.s1.getLastName(), s.getLastName());
        		assertEquals(SeedDBData.s1.getGrade(), s.getGrade());
        	} else if (s.getId().equals(SeedDBData.s2.getId())) {
	        		assertEquals(SeedDBData.s2.getFirstName(), s.getFirstName());
	        		assertEquals(SeedDBData.s2.getLastName(), s.getLastName());
	        		assertEquals(SeedDBData.s2.getGrade(), s.getGrade());
            	} else if (s.getId().equals(SeedDBData.s3.getId())) {
            		assertEquals(SeedDBData.s3.getFirstName(), s.getFirstName());
            		assertEquals(SeedDBData.s3.getLastName(), s.getLastName());
            		assertEquals(SeedDBData.s3.getGrade(), s.getGrade());
                	}
        		
        }
	}

	@Test
	@DirtiesContext
    public void testSaveStudentChangeFirstLastNameAndGrade() {
		
        Student student = studentRepository.getStudentById(2l);
        student.setFirstName(student.getFirstName()+" v1");
        student.setLastName(student.getLastName()+" v1");
        
        HttpEntity<Student> httpEntity = new HttpEntity<Student>(student,httpHeaders);
        ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Student newStudent = response.getBody();
        assertTrue(newStudent.getId().equals(student.getId()) &&
        		newStudent.getFirstName().equals(student.getFirstName()) &&
        		newStudent.getLastName().equals(student.getLastName()) &&
        		newStudent.getGrade().equals(student.getGrade()) &&
        		newStudent.getVersion().equals(student.getVersion()+1) &&
        		newStudent.getNoteList().size() == student.getNoteList().size());
    }

	@Test
	public void testSaveStudentAddNote() {
		
		Student student = new Student();
		student = SerializationUtils.clone(SeedDBData.s3);
		Note newNote = new Note();
		newNote.setTimestamp(new Timestamp(1481403630843l));
		newNote.setText("note new note");
		student.setNoteList(
				new ArrayList<Note>(student.getNoteList())
		);
		student.getNoteList().add(newNote);
		
		
		
		HttpEntity<Student> httpEntity = new HttpEntity<Student>(student,httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Student savedStudent = response.getBody();
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent, student, "noteList", "version"));
        assertTrue(savedStudent.getNoteList().size() == 3);
        assertTrue(savedStudent.getVersion().equals(student.getVersion()+1));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(0), student.getNoteList().get(0), true));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(1), student.getNoteList().get(1), true));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(2), newNote, "id", "version"));
        assertTrue(savedStudent.getNoteList().get(2).getId().compareTo(0l) > 0 &&
        		savedStudent.getNoteList().get(2).getVersion().compareTo(0l) == 0);
    }
	
	@Test
	public void testSaveStudentDeleteNote() throws JsonProcessingException {
		
		Student student = new Student();
		student.setFirstName("new first name with notes t07testSaveStudentDeleteNote");
		student.setLastName("new last name with notes t07testSaveStudentDeleteNote");
		student.setGrade(Grade.TWO);
		Note note1 = new Note();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse("2017-01-22 20:08", formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText("new note1 text t07testSaveStudentDeleteNote");

		student.getNoteList().add(note1);
		studentRepository.save(student);
		
		student.getNoteList().remove(0);
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

		Student newStudent = objectMapper.treeToValue(response.getBody(), Student.class);
		assertTrue(
			newStudent.getNoteList().size() == 0);
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
		Note note = student.getNoteList().get(2);
		note.setText(note.getText()+" modified");
		HttpEntity<Student> httpEntity = new HttpEntity<Student>(student,httpHeaders);
		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Student savedStudent = response.getBody();
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent, student, "noteList", "version"));
        assertTrue(savedStudent.getNoteList().size() == 3);
        assertTrue(savedStudent.getVersion().equals(student.getVersion()+1));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(0), student.getNoteList().get(0), true));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(1), student.getNoteList().get(1), true));
        assertTrue(EqualsBuilder.reflectionEquals(savedStudent.getNoteList().get(2), student.getNoteList().get(2), "version"));
        assertTrue(savedStudent.getNoteList().get(2).getVersion().equals(student.getNoteList().get(2).getVersion()+1));
    }

	@Test
	@DirtiesContext
	public void testDeleteStudentById() {
		
		testRestTemplate.delete(BASE_URI+"/deleteStudentById/1");
		//TODO check the repository that the row is deleted
		assertEquals(true, true);
	}

	@Test
	@DirtiesContext
	public void testSaveStudentAddStudent() {
		
		Student student = new Student();
		student.setFirstName("new first name");
		student.setLastName("new last name");
		student.setGrade(Grade.ONE);
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		Student newStudent = response.getBody();
		
		assertTrue(student.getFirstName().equals(newStudent.getFirstName()) &&
				student.getLastName().equals(newStudent.getLastName()) &&
				student.getGrade().equals(newStudent.getGrade()) &&
				newStudent.getId().compareTo(0l) > 0 &&
				newStudent.getVersion().equals(0l));
	}

	@Test
	@DirtiesContext
	public void testSaveStudentAddStudentWithNotes() {
		
		Student student = new Student();
		student.setFirstName("new first name with notes");
		student.setLastName("new last name with notes");
		student.setGrade(Grade.TWO);
		Note note1 = new Note();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse("2017-01-22 20:08", formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText("new note1 text");
		Note note2 = new Note();
		LocalDateTime timestamp2 = LocalDateTime.parse("2017-01-22 20:18", formatter);
		note2.setTimestamp(Timestamp.valueOf(timestamp2));
		note2.setText("new note2 text");

		student.setNoteList(Arrays.asList(note1, note2));
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		ResponseEntity<Student> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, Student.class);
		
		Student newStudent = response.getBody();
		
		assertTrue(student.getFirstName().equals(newStudent.getFirstName()) &&
				student.getLastName().equals(newStudent.getLastName()) &&
				student.getGrade().equals(newStudent.getGrade()) &&
				newStudent.getId().compareTo(0l) > 0 &&
				newStudent.getVersion().equals(0l) &&
				newStudent.getNoteList().size() == 2);

		boolean foundNoteOne = false;
		boolean foundNoteTwo = false;
		for (Note note: newStudent.getNoteList()) {
			if (!foundNoteOne) {
				foundNoteOne = note.getId().compareTo(0l) > 0 &&
						note.getTimestamp().equals(note1.getTimestamp()) &&
						note.getText().equals(note1.getText()) &&
						note.getVersion().equals(0l);
			} else if (!foundNoteTwo) {
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
	@DirtiesContext
	public void testDeleteNoteById() {
		
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/deleteNoteById/2",
				HttpMethod.DELETE, httpEntity, String.class);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
}
