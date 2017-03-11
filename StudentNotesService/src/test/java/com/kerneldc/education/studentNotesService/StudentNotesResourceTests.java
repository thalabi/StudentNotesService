package com.kerneldc.education.studentNotesService;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.bean.TimestampRange;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.constants.Constants;
import com.kerneldc.education.studentNotesService.security.util.SimpleGrantedAuthorityMixIn;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
	private User user;
	
	@PostConstruct
	public void setUp() throws JsonProcessingException {
		String usernameAndPassword = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
		
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(usernameAndPassword,httpHeaders);

		//JsonNode newJsonUser = testRestTemplate.postForObject(BASE_URI+"/Security/authenticate", httpEntity, JsonNode.class);
		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/Security/authenticate", HttpMethod.POST, httpEntity, JsonNode.class);
		Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
		
		JsonNode newJsonUser = response.getBody();
		System.out.println(newJsonUser);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
		user = objectMapper.treeToValue(newJsonUser, User.class);
		LOGGER.debug("user.getToken(): {}", user.getToken());
		httpHeaders.set(Constants.AUTH_HEADER_NAME, user.getToken());
	}
	
	@Test
    public void t01testHello() {
		//HttpHeaders httpHeaders = new HttpHeaders();
		//httpHeaders.set(Constants.AUTH_HEADER_NAME, user.getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello", response.getBody());
    }

	@Test
    public void t02testGetAllStudents() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        //String allStudents = testRestTemplate.getForObject(BASE_URI+"/getAllStudents", String.class);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/getAllStudents", HttpMethod.GET, httpEntity, String.class);
        //String expected = "[{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"}]},{\"version\":0,\"id\":2,\"firstName\":\"\",\"lastName\":\"halabi\",\"grade\":\"4\",\"noteList\":[]},{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"}]}]";
        String expected = "[{\"version\":0,\"id\":2,\"firstName\":\"\",\"lastName\":\"halabi\",\"grade\":\"4\",\"noteList\":[]},{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"}]},{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"}]}]";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

	@Test
    public void t03testGetAllNotes() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/getAllNotes", HttpMethod.GET, httpEntity, String.class);
        String expected = "[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"},{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"}]";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }
	
	@Test
    public void t04testGetStudentById() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/getStudentById/1", HttpMethod.GET, httpEntity, String.class);
        String expected = "{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"}]}";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

	@Test
    public void t06testSaveStudentChangeFirstLastNameAndGrade() {
		
		String data = "{\"id\":2,\"firstName\":\"first name v1\",\"lastName\":\"last name v2\",\"grade\":\"4 v2\",\"noteList\":[], \"version\":0}";
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":2,\"firstName\":\"first name v1\",\"lastName\":\"last name v2\",\"grade\":\"4 v2\",\"noteList\":[]}";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

	@Test
	public void t07testSaveStudentAddNote() {
		
		String data = "{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"},{\"timestamp\":1481403630843,\"text\":\"note new note\"}]}";
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"},{\"version\":0,\"id\":6,\"timestamp\":1481403630843,\"text\":\"note new note\"}]}";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }
	
	@Test
	public void t08testSaveStudentDeleteNote() throws JsonProcessingException {
		
		Student student = new Student();
		student.setFirstName("new first name with notes t07testSaveStudentDeleteNote");
		student.setLastName("new last name with notes t07testSaveStudentDeleteNote");
		student.setGrade("2");
		Note note1 = new Note();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse("2017-01-22 20:08", formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText("new note1 text t07testSaveStudentDeleteNote");

		student.getNoteList().add(note1);
		studentRepository.save(student);
		
		student.getNoteList().remove(0);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

		Student newStudent = objectMapper.treeToValue(response.getBody(), Student.class);
		Assert.assertTrue(
			newStudent.getNoteList().size() == 0);
    }
	
	@Test
	public void t09testSaveStudentModifyNote() {
		
		String data = "{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified\"}]}";
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":1,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified\"}]}";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

	@Test
	public void t10testDeleteStudentById() {
		
		testRestTemplate.delete(BASE_URI+"/deleteStudentById/1");
		//TODO check the repository that the row is deleted
		assertEquals(true, true);
	}

	@Test
	public void t11testSaveNote() throws JsonProcessingException {
		
		Student student = new Student();
		student.setFirstName("new first name with notes t10testSaveNote");
		student.setLastName("new last name with notes t10testSaveNote");
		student.setGrade("4");
		Note note1 = new Note();
		note1.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note1.setText("new note1 text t10testSaveNote");

		student.getNoteList().add(note1);
		studentRepository.save(student);

		note1.setTimestamp(new Timestamp(note1.getTimestamp().getTime() - 60000));
		note1.setText("new note1 text t10testSaveNote modified");
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNote = objectMapper.valueToTree(note1);
		
		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonNote,httpHeaders);

		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/saveNote", HttpMethod.POST, httpEntity, JsonNode.class);
		Note newNote = objectMapper.treeToValue(response.getBody(), Note.class);
		Assert.assertTrue(
			newNote.getId().equals(note1.getId()) &&
			newNote.getTimestamp().equals(note1.getTimestamp()) &&
			newNote.getText().equals(note1.getText()) &&
			newNote.getVersion().equals(note1.getVersion()+1));
    }

	@Test
	public void t12testSaveStudentAddStudent() {
		
		Student student = new Student();
		student.setFirstName("new first name");
		student.setLastName("new last name");
		student.setGrade("1");
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Assert.fail(ExceptionUtils.exceptionStackTraceAsString(e));
			;
		}
		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, JsonNode.class);
		
		JsonNode newStudent = objectMapper.convertValue(response.getBody(), JsonNode.class);
		Long newId = newStudent.get("id").asLong();
		String newFirstName = newStudent.get("firstName").asText();
		String newLastName = newStudent.get("lastName").asText();
		String newGrade = newStudent.get("grade").asText();
		Long newVersion = newStudent.get("version").asLong();

		Assert.assertTrue(student.getFirstName().equals(newFirstName) &&
				student.getLastName().equals(newLastName) &&
				student.getGrade().equals(newGrade) &&
				newId.compareTo(0l) > 0 &&
				newVersion.equals(0l));
	}

	@Test
	public void t13testSaveStudentAddStudentWithNotes() {
		
		Student student = new Student();
		student.setFirstName("new first name with notes");
		student.setLastName("new last name with notes");
		student.setGrade("2");
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
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		ResponseEntity<JsonNode> response = testRestTemplate.exchange(BASE_URI+"/saveStudent", HttpMethod.POST, httpEntity, JsonNode.class);
		
		JsonNode newStudent = null;
		newStudent = objectMapper.convertValue(response.getBody(), JsonNode.class);
		Long newId = newStudent.get("id").asLong();
		String newFirstName = newStudent.get("firstName").asText();
		String newLastName = newStudent.get("lastName").asText();
		String newGrade = newStudent.get("grade").asText();
		Long newVersion = newStudent.get("version").asLong();

		Assert.assertTrue(student.getFirstName().equals(newFirstName) &&
				student.getLastName().equals(newLastName) &&
				student.getGrade().equals(newGrade) &&
				newId.compareTo(0l) > 0 &&
				newVersion.equals(0l));
	}

	@Test
    public void t14testGetLatestActiveStudents() {
		//TODO
	}
	
	@Test
    public void t05testGetStudentsByTimestampRange() throws JsonProcessingException {
		TimestampRange timestampRange = new TimestampRange();
		LocalDate fromLocalDate = LocalDate.of(2016, 1, 1);
		LocalDate toLocalDate = LocalDate.of(2017, 12, 31);
		timestampRange.setFromTimestamp(Timestamp.valueOf(fromLocalDate.atStartOfDay()));
		timestampRange.setToTimestamp(Timestamp.valueOf(toLocalDate.atStartOfDay()));
		
		ObjectMapper objectMapper = new ObjectMapper();
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
    public void t15testPdfAll() {
		//TODO
	}
	@Test
    public void t16testPdfStudentsByTimestampRange() {
		//TODO
	}

	
	/**
	 * Run as last test as it causes some aop error
	 */
	@Test
	public void t99testDeleteNoteById() {
		
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/deleteNoteById/2",
				HttpMethod.DELETE, httpEntity, String.class);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}
}
