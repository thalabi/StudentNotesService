package com.kerneldc.education.studentNotesService.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.StudentNotesApplication;
import com.kerneldc.education.studentNotesService.security.util.JwtTokenUtil;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
public class JwtTokenTests {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Test
    public void testGenerate() {
        Assert.assertTrue(jwtTokenUtil.generate("username", null).length() != 0);
    }

	@Test
    public void testGetUsernameFromToken() {
		String token = jwtTokenUtil.generate("username", null);
		String username = jwtTokenUtil.getUsernameFromToken(token);
        Assert.assertTrue("username".equals(username));
    }

	@Test
    public void testGetAuthoritiesFromToken() {
		List<GrantedAuthority> fixture = new ArrayList<>();
		fixture.addAll(Arrays.asList(new SimpleGrantedAuthority("authority1"), new SimpleGrantedAuthority("authority2"), new SimpleGrantedAuthority("authority3")));

		String token = jwtTokenUtil.generate("username", fixture);
		Collection<GrantedAuthority> result = jwtTokenUtil.getAuthoritiesFromToken(token);
		
        Assert.assertTrue(result.containsAll(fixture));
    }
	
}
