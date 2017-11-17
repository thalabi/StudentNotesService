package com.kerneldc.education.studentNotesService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.jpa.repository.JpaContext;
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
import com.kerneldc.education.studentNotesService.constants.Constants;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.SchoolYearDto;
import com.kerneldc.education.studentNotesService.dto.transformer.SchoolYearTransformer;
import com.kerneldc.education.studentNotesService.junit.MyTestExecutionListener;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.resource.vo.SaveRemoveStudentsToFromSchoolYearVo;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.constants.SecurityConstants;
import com.kerneldc.education.studentNotesService.security.util.SimpleGrantedAuthorityMixIn;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(listeners = MyTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class SchoolYearResourceTests implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	private static final String BASE_URI = "/StudentNotesService";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	
	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private JpaContext jpaContext;
	
	private EntityManager entityManager;

	@Override
	public void afterPropertiesSet() {
		entityManager = jpaContext.getEntityManagerByManagedType(SchoolYear.class);
	}

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

//	@Test
//    public void testGetAllSchoolYears() {
//		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
//		ResponseEntity<SchoolYear[]> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/getAllSchoolYears", HttpMethod.GET, httpEntity, SchoolYear[].class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        SchoolYear[] schoolYears = response.getBody();
//        assertEquals(2, schoolYears.length);
//    }

	@Test
    public void testGetAllSchoolYearDtos() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<SchoolYearDto[]> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/getAllSchoolYearDtos", HttpMethod.GET, httpEntity, SchoolYearDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SchoolYearDto[] schoolYearDtos = response.getBody();
        assertEquals(2, schoolYearDtos.length);
    }

//	@Test
//    public void testGetStudentsBySchoolYearId() {
//		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
//		ResponseEntity<SchoolYear> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/getStudentsBySchoolYearId/1", HttpMethod.GET, httpEntity, SchoolYear.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        SchoolYear schoolYear = response.getBody();
//        assertNotNull(schoolYear);
//        assertEquals(3, schoolYear.getStudentSet().size());
//    }

	@Test
    public void testGetStudentDtosBySchoolYearId() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<SchoolYearDto> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/getStudentDtosBySchoolYearId/1", HttpMethod.GET, httpEntity, SchoolYearDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SchoolYearDto schoolYearDto = response.getBody();
        assertNotNull(schoolYearDto);
        //assertEquals(2, schoolYearDto.getStudentDtoSet().size());
    }

//	@Test
//	public void testGetLatestActiveStudentsBySchoolYearId() {
//		SchoolYearIdAndLimit schoolYearIdAndLimit = new SchoolYearIdAndLimit();
//		schoolYearIdAndLimit.setSchoolYearId(1l);
//		schoolYearIdAndLimit.setLimit(5);
//		HttpEntity<SchoolYearIdAndLimit> httpEntity = new HttpEntity<SchoolYearIdAndLimit>(schoolYearIdAndLimit,httpHeaders);
//		ResponseEntity<SchoolYear> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/getLatestActiveStudentsBySchoolYearId", HttpMethod.POST, httpEntity, SchoolYear.class);
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		SchoolYear schoolYear = response.getBody();
//		assertEquals(2, schoolYear.getStudentSet().size());
//	}

	@Test
	@DirtiesContext
	public void testSaveSchoolYearDtoForInsert() throws ParseException {
		SchoolYearDto newSchoolYearDto = new SchoolYearDto();
		newSchoolYearDto.setSchoolYear("new school year 1");
		newSchoolYearDto.setStartDate(dateFormat.parse("2027-09-01"));
		newSchoolYearDto.setEndDate(dateFormat.parse("2028-06-31"));
		HttpEntity<SchoolYearDto> httpEntity = new HttpEntity<>(newSchoolYearDto,httpHeaders);
		ResponseEntity<SchoolYearDto> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/saveSchoolYearDto", HttpMethod.POST, httpEntity, SchoolYearDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		SchoolYearDto savedSchoolYearDto = response.getBody();
		assertNotNull(savedSchoolYearDto.getId());
		assertEquals(newSchoolYearDto.getSchoolYear(), savedSchoolYearDto.getSchoolYear());
		LOGGER.debug("newSchoolYearDto.getStartDate(): {}, savedSchoolYearDto.getStartDate(): {}", newSchoolYearDto.getStartDate(), savedSchoolYearDto.getStartDate());
		assertEquals(newSchoolYearDto.getStartDate(), savedSchoolYearDto.getStartDate());
		assertEquals(newSchoolYearDto.getEndDate(), savedSchoolYearDto.getEndDate());
		assertEquals(new Long(0), savedSchoolYearDto.getVersion());
	}

	@Test
	@DirtiesContext
	public void testSaveSchoolYearDtoForUpdate() throws ParseException {
		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		SchoolYearDto schoolYearDto = SchoolYearTransformer.entityToDto(schoolYear);
		
		schoolYearDto.setSchoolYear(schoolYearDto.getSchoolYear()+" v1");

		HttpEntity<SchoolYearDto> httpEntity = new HttpEntity<>(schoolYearDto,httpHeaders);
		
		ResponseEntity<SchoolYearDto> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/saveSchoolYearDto", HttpMethod.POST, httpEntity, SchoolYearDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		SchoolYearDto savedSchoolYearDto = response.getBody();
		assertThat(savedSchoolYearDto.getId(), notNullValue());
		assertThat(schoolYearDto.getSchoolYear(), equalTo(savedSchoolYearDto.getSchoolYear()));
		assertThat(schoolYearDto.getStartDate().compareTo(savedSchoolYearDto.getStartDate()),  equalTo(0));
		assertThat(schoolYearDto.getEndDate().compareTo(savedSchoolYearDto.getEndDate()), equalTo(0));
		assertThat(savedSchoolYearDto.getVersion(), equalTo(1l));
	}

	@Test
	@DirtiesContext
	public void testDeleteSchoolYearById() throws ParseException {
		SchoolYear newSchoolYear = new SchoolYear();
		newSchoolYear.setSchoolYear("new school year 1");
		newSchoolYear.setStartDate(dateFormat.parse("2027-09-01"));
		newSchoolYear.setEndDate(dateFormat.parse("2028-06-31"));
		schoolYearRepository.save(newSchoolYear);
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/deleteSchoolYearById/"+newSchoolYear.getId(), HttpMethod.DELETE, httpEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SchoolYear shouldNotExistSchoolYear = schoolYearRepository.findOne(newSchoolYear.getId());
		assertEquals("school year should not exist", null, shouldNotExistSchoolYear);
	}
	
	@Test
	public void testDeleteSchoolYearByIdNotFound() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/deleteSchoolYearById/777777", HttpMethod.DELETE, httpEntity, String.class);
        assertEquals(Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE, response.getStatusCode().value());
	}

	@Test
	@DirtiesContext
	public void testDeleteSchoolYearByIdWithStudents() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/deleteSchoolYearById/1", HttpMethod.DELETE, httpEntity, String.class);
        assertEquals(Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE, response.getStatusCode().value());
	}
	
	@Ignore
	@Test
	public void testSaveRemoveStudents() {
		Student student1 = createStudent("fn1", "ln1");
		Student student2 = createStudent("fn2", "ln2");
		Student student3 = createStudent("fn3", "ln3");
		SchoolYear schoolYear = new SchoolYear();
		schoolYear.setSchoolYear("sy 1");
		schoolYear.setStartDate(Date.valueOf(LocalDate.of(2017, 9, 1)));
		schoolYear.setEndDate(Date.valueOf(LocalDate.of(2018, 6, 30)));
		schoolYearRepository.save(schoolYear);
		student1.addSchoolYear(schoolYear);
		student2.addSchoolYear(schoolYear);
		schoolYearRepository.save(schoolYear);
		//schoolYear.getStudentSet().size();
		assertThat(schoolYear.getStudentSet(), hasSize(2));
		SaveRemoveStudentsToFromSchoolYearVo saveRemoveStudentsToFromSchoolYearVo = new SaveRemoveStudentsToFromSchoolYearVo();
		saveRemoveStudentsToFromSchoolYearVo.setSchoolYearId(schoolYear.getId());
		List<Long> oldSchoolYearStudentIds = new ArrayList<>(Arrays.asList(student1.getId(), student2.getId()));
		saveRemoveStudentsToFromSchoolYearVo.setOldSchoolYearStudentIds(oldSchoolYearStudentIds);
		List<Long> newSchoolYearStudentIds = new ArrayList<>(Arrays.asList(student2.getId(), student3.getId()));
		saveRemoveStudentsToFromSchoolYearVo.setNewSchoolYearStudentIds(newSchoolYearStudentIds);
		
		HttpEntity<SaveRemoveStudentsToFromSchoolYearVo> httpEntity = new HttpEntity<SaveRemoveStudentsToFromSchoolYearVo>(saveRemoveStudentsToFromSchoolYearVo,httpHeaders);

		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/saveRemoveStudentsToFromSchoolYear", HttpMethod.POST, httpEntity, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		// TODO test fails here becuase of no entity manager with actual transaction
		entityManager.refresh(schoolYear);
		//schoolYearRepository.findOne(schoolYear.getId());
		assertThat(schoolYear.getStudentSet(), hasSize(2));
		assertThat(schoolYear.getStudentSet(), hasItem(student2));
		assertThat(schoolYear.getStudentSet(), hasItem(student3));
	}
	
	private Student createStudent(String firstName, String lastName) {
		Student student = new Student();
		student.setFirstName(firstName);
		student.setLastName(lastName);
		studentRepository.save(student);
		return student;
	}
}
