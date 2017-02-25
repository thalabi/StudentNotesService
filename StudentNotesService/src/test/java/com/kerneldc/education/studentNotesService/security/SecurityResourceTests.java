package com.kerneldc.education.studentNotesService.security;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.StudentNotesApplication;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.constants.Constants;
import com.kerneldc.education.studentNotesService.security.util.JwtTokenUtil;
import com.kerneldc.education.studentNotesService.security.util.SimpleGrantedAuthorityMixIn;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecurityResourceTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	private static final String BASE_URI = "/StudentNotesService/Security";
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Test
    public void testHelloWithNoCredentials() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(BASE_URI, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

	@Test
    public void testHelloWithCredentials() throws JsonProcessingException {

		String username = "thalabi";
		String password = "xxxxxxxxxxxxxxxx";
		String usernameAndPassword = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(usernameAndPassword,httpHeaders);

		JsonNode newJsonUser = testRestTemplate.postForObject(BASE_URI+"/authenticate", httpEntity, JsonNode.class);
		System.out.println(newJsonUser);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
		User user = objectMapper.treeToValue(newJsonUser, User.class);
		LOGGER.debug("user.getToken(): {}", user.getToken());
		
		//String hello = testRestTemplate.getForObject(BASE_URI, String.class);
		httpHeaders.set(Constants.AUTH_HEADER_NAME, user.getToken());
		httpHeaders.forEach((k,v)->{LOGGER.debug("header: {}, content: {}", k, v);});
		httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(BASE_URI, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello", response.getBody());
    }

	@Test
	public void testAuthenticate() throws JsonProcessingException {
		
		String username = "thalabi";
		String password = "xxxxxxxxxxxxxxxx";
		String usernameAndPassword = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(usernameAndPassword,httpHeaders);

		JsonNode newJsonUser = testRestTemplate.postForObject(BASE_URI+"/authenticate", httpEntity, JsonNode.class);
		System.out.println(newJsonUser);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
		User newUser = objectMapper.treeToValue(newJsonUser, User.class);
		Assert.assertTrue(
			newUser.getId().compareTo(0l) > 0 &&
			newUser.getUsername().equals(username) &&
			newUser.getPassword().equals(password) &&
			StringUtils.isNotBlank(newUser.getFirstName()) &&
			StringUtils.isNotBlank(newUser.getLastName()) &&
			newUser.getToken().length() > 0
			);
		Assert.assertTrue(
			jwtTokenUtil.getUsernameFromToken(newUser.getToken()).equals(username)
			);
    }

//	@Test
//	public void testAuthenticateWithValidJwt() throws JsonProcessingException {
//		
//		String username = "thalabi";
//		String password = "xxxxxxxxxxxxxxxx";
//		String usernameAndPassword = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
//		
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> httpEntity = new HttpEntity<String>(usernameAndPassword,httpHeaders);
//
//		JsonNode newJsonUser = testRestTemplate.postForObject(BASE_URI+"/authenticateWithValidJwt", httpEntity, JsonNode.class);
//		ObjectMapper objectMapper = new ObjectMapper();
//		User newUser = objectMapper.treeToValue(newJsonUser, User.class);
//		Assert.assertTrue(
//			newUser.getId().equals(7l) &&
//			newUser.getUsername().equals(username) &&
//			newUser.getPassword().equals(password) &&
//			newUser.getFirstName().equals("first name") &&
//			newUser.getLastName().equals("last name") &&
//			newUser.getToken().length() > 0
//			);
//		Assert.assertTrue(
//			jwtTokenUtil.getUsernameFromToken(newUser.getToken()).equals(username)
//			);
//    }

}
