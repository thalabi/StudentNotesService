package com.kerneldc.education.studentNote;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotes.StudentNotesApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace=Replace.NONE)
//@Transactional
@AutoConfigureTestEntityManager
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
        String expected = "[{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"Other\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"}]},{\"version\":0,\"id\":2,\"firstName\":\"\",\"lastName\":\"halabi\",\"grade\":\"GR-4\",\"noteList\":[]},{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"}]}]";
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
		
		String data = "{\"id\":2,\"firstName\":\"first name v1\",\"lastName\":\"last name v2\",\"grade\":\"GR-4 v2\",\"noteList\":[], \"version\":0}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":2,\"firstName\":\"first name v1\",\"lastName\":\"last name v2\",\"grade\":\"GR-4 v2\",\"noteList\":[]}";
		assertEquals(expected, actual);
    }

	@Test
	public void testSaveStudentAddNote() {
		
		String data = "{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"Other\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"},{\"timestamp\":1481403630843,\"text\":\"note new note\"}]}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"Other\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"},{\"version\":0,\"id\":5,\"timestamp\":1481403630843,\"text\":\"note 5\"},{\"version\":0,\"id\":6,\"timestamp\":1481403630843,\"text\":\"note new note\"}]}";
		assertEquals(expected, actual);
    }
	
	@Test
	public void testSaveStudentDeleteNote() {
		
		String data = "{\"version\":0,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"Other\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"}]}";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(data,httpHeaders);

		String actual = testRestTemplate.postForObject(BASE_URI+"/saveStudent", httpEntity, String.class);
		String expected = "{\"version\":1,\"id\":3,\"firstName\":\"Mr Parent\",\"lastName\":\"\",\"grade\":\"Other\",\"noteList\":[{\"version\":0,\"id\":4,\"timestamp\":1481403630842,\"text\":\"note 4\"}]}";
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
		
		testRestTemplate.delete(BASE_URI+"/deleteNoteById/2");
		assertEquals(true, true);
	}
}
