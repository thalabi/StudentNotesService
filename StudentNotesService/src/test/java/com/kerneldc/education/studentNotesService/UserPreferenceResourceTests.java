package com.kerneldc.education.studentNotesService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

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
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.dto.UserPreferenceDto;
import com.kerneldc.education.studentNotesService.dto.transformer.UserPreferenceTransformer;
import com.kerneldc.education.studentNotesService.junit.MyTestExecutionListener;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.UserPreferenceRepository;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.constants.SecurityConstants;
import com.kerneldc.education.studentNotesService.security.util.SimpleGrantedAuthorityMixIn;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(listeners = MyTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@Transactional
public class UserPreferenceResourceTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	private static final String BASE_URI = "/StudentNotesService";
//	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UserPreferenceRepository userPreferenceRepository;
	
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
        ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/userPreference", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello", response.getBody());
    }

	@Test
	public void testGetDtoByUsername() {
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
		String username = "TestUser";
		ResponseEntity<UserPreferenceDto> response = testRestTemplate.exchange(BASE_URI+"/userPreference/getDtoByUsername/"+username, HttpMethod.GET, httpEntity, UserPreferenceDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		UserPreferenceDto userPreferenceDto = response.getBody();
		assertThat(userPreferenceDto, notNullValue());
		LOGGER.debug("userPreference: {}", userPreferenceDto);
		assertThat(userPreferenceDto.getUsername(), equalTo(username));
		assertThat(userPreferenceDto.getId(), equalTo(1l));
		assertThat(userPreferenceDto.getSchoolYearDto().getId(), equalTo(1l));
	}

	@Test
	@DirtiesContext
	public void testSaveUserPreferenceDto() throws ParseException {
		
		UserPreference userPreference = userPreferenceRepository.findByUsername("TestUser").get(0);
		assertThat(userPreference.getSchoolYear().getId(), equalTo(1l));
		SchoolYear schoolYear = schoolYearRepository.findOne(2l);
		userPreference.setSchoolYear(schoolYear);
		
		UserPreferenceDto userPreferenceDto = UserPreferenceTransformer.entityToDto(userPreference);
		
		HttpEntity<UserPreferenceDto> httpEntity = new HttpEntity<UserPreferenceDto>(userPreferenceDto, httpHeaders);
		ResponseEntity<UserPreferenceDto> response = testRestTemplate.exchange(
				BASE_URI + "/userPreference/saveUserPreferenceDto", HttpMethod.POST, httpEntity, UserPreferenceDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		UserPreferenceDto savedUserPreferenceDto = response.getBody();
		assertThat(savedUserPreferenceDto.getSchoolYearDto().getId(), equalTo(2l));
	}
//
//	@Test
//	@DirtiesContext
//	public void testDeleteSchoolYearById() throws ParseException {
//		SchoolYear newSchoolYear = new SchoolYear();
//		newSchoolYear.setSchoolYear("new school year 1");
//		newSchoolYear.setStartDate(dateFormat.parse("2027-09-01"));
//		newSchoolYear.setEndDate(dateFormat.parse("2028-06-31"));
//		schoolYearRepository.save(newSchoolYear);
//		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
//		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/deleteSchoolYearById/"+newSchoolYear.getId(), HttpMethod.DELETE, httpEntity, String.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        SchoolYear shouldNotExistSchoolYear = schoolYearRepository.findOne(newSchoolYear.getId());
//		assertEquals("school year should not exist", null, shouldNotExistSchoolYear);
//	}
//	
//	@Test
//	public void testDeleteSchoolYearByIdNotFound() {
//		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
//		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/deleteSchoolYearById/777777", HttpMethod.DELETE, httpEntity, String.class);
//        assertEquals(Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE, response.getStatusCode().value());
//	}
//
//	@Test
//	public void testDeleteSchoolYearByIdWithStudents() {
//		HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
//		ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI+"/schoolYear/deleteSchoolYearById/1", HttpMethod.DELETE, httpEntity, String.class);
//        assertEquals(Constants.SN_EXCEPTION_RESPONSE_STATUS_CODE, response.getStatusCode().value());
//	}
}
