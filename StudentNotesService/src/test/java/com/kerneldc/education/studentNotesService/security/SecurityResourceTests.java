package com.kerneldc.education.studentNotesService.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.kerneldc.education.studentNotesService.StudentNotesApplication;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.util.JwtTokenUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecurityResourceTests {

	private static final String BASE_URI = "/StudentNotesService/Security";
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Test
    public void testHello() {
        String hello = testRestTemplate.getForObject(BASE_URI, String.class);
        assertEquals("Hello", hello);
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
		ObjectMapper objectMapper = new ObjectMapper();
		//objectMapper.enableDefaultTyping();
		User newUser = objectMapper.treeToValue(newJsonUser, User.class);
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
