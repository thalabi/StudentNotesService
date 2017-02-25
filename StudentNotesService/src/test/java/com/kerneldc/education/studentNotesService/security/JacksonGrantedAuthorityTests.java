package com.kerneldc.education.studentNotesService.security;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.util.SimpleGrantedAuthorityMixIn;

@RunWith(SpringRunner.class)
public class JacksonGrantedAuthorityTests {
	
	@Test
	public void testJacksonSerialization() throws JsonProcessingException {

		User fixture = new User();
		fixture.setId(7l);
		fixture.setUsername("username");
		fixture.setPassword("password");
		fixture.setFirstName("firstName");
		fixture.setLastName("lastName");
		fixture.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("authority1"), new SimpleGrantedAuthority("authority2"), new SimpleGrantedAuthority("authority3")));
		fixture.setToken("token");
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		JsonNode jsonNode = objectMapper.valueToTree(fixture);
		
		objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
		User result = objectMapper.treeToValue(jsonNode, User.class);
		Assert.assertTrue(
				result.getId().compareTo(fixture.getId()) == 0 &&
				result.getUsername().equals(fixture.getUsername()) &&
				result.getPassword().equals(fixture.getPassword()) &&
				result.getFirstName().equals(fixture.getFirstName()) &&
				result.getLastName().equals(fixture.getLastName()) &&
				result.getAuthorities().containsAll(fixture.getAuthorities()) &&
				result.getToken().equals(fixture.getToken())
				);
	}
}
