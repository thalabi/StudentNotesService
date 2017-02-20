package com.kerneldc.education.studentNotesService.security;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.StudentNotesApplication;
import com.kerneldc.education.studentNotesService.security.util.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
public class JwtTokenTests {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Test
    public void testGenerate() {
        Assert.assertTrue(jwtTokenUtil.generate("username").length() != 0);
    }

	@Test
    public void testGetUsernameFromToken() {
		String token = jwtTokenUtil.generate("username");
		String username = jwtTokenUtil.getUsernameFromToken(token);
        Assert.assertTrue("username".equals(username));
    }

	@Test
    public void testGetPermissionsFromToken() {
		String token = jwtTokenUtil.generate("username", "permission1", "permission2", "permission3");
		String[] permissions = jwtTokenUtil.getPermissionsFromToken(token);
        Assert.assertTrue(Arrays.equals(permissions, new String[]{"permission1", "permission2", "permission3"}));
    }

	@Test
    public void testMap() {
		Map<String, Object> m = new HashMap<>();
		String[] permissions = new String[]{"permission1", "permission2", "permission3"};
		m.put("permissions", permissions);
		String[] p2 = (String[])m.get("permissions");
		Assert.assertTrue(p2.length == 3);
	}
	
	@Test
	public void testClaims() throws NoSuchAlgorithmException {

		List<String> permissions = new ArrayList<>(Arrays.asList("permission1", "permission2", "permission3"));
		String p = "pValue";
		Map<String, Object> claims = new HashMap<>();
		claims.put(Claims.SUBJECT, "username");
		claims.put("permissions", permissions);
		claims.put("p", p);
		Key key = KeyGenerator.getInstance("AES").generateKey();
		String compactJws = Jwts.builder()
				  .setClaims(claims)
				  .signWith(SignatureAlgorithm.HS512, key)
				  .compact();
    	Claims retrievedClaims = Jwts.parser()
            	.setSigningKey(key)
            	.parseClaimsJws(compactJws)
            	.getBody();
    	String retrievedP = (String)retrievedClaims.get("p");
    	System.out.println("retrievedP: "+retrievedP);
    	List<String> retrievedPermissions = (ArrayList<String>)retrievedClaims.get("permissions");
    	System.out.println("retrievedPermissions: "+retrievedPermissions);
	}

}
