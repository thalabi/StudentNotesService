package com.kerneldc.education.studentNote;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotes.StudentNotesApplication;
import com.kerneldc.education.studentNotes.domain.Note;
import com.kerneldc.education.studentNotes.domain.Student;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace=Replace.NONE)
//@Transactional
//@AutoConfigureTestEntityManager
public class StudentNotesApplicationTests {

	private static final String BASE_URI = "/StudentNotesService";
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Before
	public void before () throws IOException {
	}

	@Test
    public void testHello() {
        String hello = testRestTemplate.getForObject(BASE_URI, String.class);
        assertEquals("Hello", hello);
    }

	@Test
    public void testGetAllStudents() {
        String allStudents = testRestTemplate.getForObject(BASE_URI+"/getAllStudents", String.class);
        String expected = "[{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"}]},{\"version\":0,\"id\":2,\"firstName\":\"\",\"lastName\":\"halabi\",\"grade\":\"4\",\"noteList\":[]},{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"}]}]";
        assertEquals(expected, allStudents);
    }

	@Test
    public void testGetAllNotes() {
        String allNotes = testRestTemplate.getForObject(BASE_URI+"/getAllNotes", String.class);
        String expected = "[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"},{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"}]";
        assertEquals(expected, allNotes);
    }
	
	@Test
    public void testGetStudentById() {
        String allNotes = testRestTemplate.getForObject(BASE_URI+"/getStudentById/1", String.class);
        String expected = "{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"}]}";
        assertEquals(expected, allNotes);
    }

	@Test
    public void testSaveStudentChangeFirstLastNameAndGrade() {
		
		String data = "{\"id\":2,\"firstName\":\"first name v1\",\"lastName\":\"last name v2\",\"grade\":\"4 v2\",\"noteList\":[], \"version\":0}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":2,\"firstName\":\"first name v1\",\"lastName\":\"last name v2\",\"grade\":\"4 v2\",\"noteList\":[]}";
		assertEquals(expected, actual);
    }

	@Test
	public void testSaveStudentAddNote() {
		
		String data = "{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"},{\"timestamp\":1481403630843,\"text\":\"note new note\"}]}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"},{\"version\":0,\"id\":6,\"timestamp\":1481403630843,\"text\":\"note new note\"}]}";
		assertEquals(expected, actual);
    }
	
	@Test
	public void testSaveStudentDeleteNote() {
		
		String data = "{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"}]}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"}]}";
		assertEquals(expected, actual);
    }
    //String expected = 
	
	@Test
	public void testSaveStudentModifyNote() {
		
		String data = "{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified\"}]}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":1,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified\"}]}";
		assertEquals(expected, actual);
    }

	@Test
	public void testDeleteStudentById() {
		
		testRestTemplate.delete(BASE_URI+"/deleteStudentById/1");
		assertEquals(true, true);
	}

	@Test
	public void testSaveNote() {
		
		String data = "{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified via saveNote method\"}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveNote", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3 modified via saveNote method\"}";
		assertEquals(expected, actual);
    }

	@Test
	public void testDeleteNoteById() {
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>("",httpHeaders);
		Map<String,Object> urlVariables = new HashMap<>();
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/deleteNoteById/2",
				HttpMethod.DELETE, httpEntity, String.class, urlVariables);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testSaveStudentAddStudent() {
		
		Student student = new Student();
		student.setFirstName("new first name");
		student.setLastName("new last name");
		student.setGrade("1");
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonStudent = objectMapper.valueToTree(student);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		JsonNode newJsonStudent = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, JsonNode.class);
		
		JsonNode newStudent = null;
		newStudent = objectMapper.convertValue(newJsonStudent, JsonNode.class);
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
	public void testSaveStudentAddStudentWithNotes() {
		
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

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(jsonStudent,httpHeaders);

		JsonNode newJsonStudent = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, JsonNode.class);
		
		JsonNode newStudent = null;
		newStudent = objectMapper.convertValue(newJsonStudent, JsonNode.class);
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

}
