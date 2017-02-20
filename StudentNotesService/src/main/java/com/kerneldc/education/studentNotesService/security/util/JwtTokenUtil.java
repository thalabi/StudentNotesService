package com.kerneldc.education.studentNotesService.security.util;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.security.bean.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	@Autowired
	private KeyProvider keyProvider; 
	private Key key;
	
	@PostConstruct
	public void init() {
		key = keyProvider.getKey();
	}
	
	public String generate(String username, String... permissions) {
		
		Map<String, Object> claims = new HashMap<>();
		claims.put(Claims.SUBJECT, username);
		claims.put("permissions", permissions);
		String compactJws = Jwts.builder()
				  .setClaims(claims)
				  .signWith(SignatureAlgorithm.HS512, key)
				  .compact();
		return compactJws;
	}

	public User parseToken(String token) {
		
		User user = new User();
		user.setUsername(getUsernameFromToken(token));
		user.setToken(token);
		return user;
	}
	public String getUsernameFromToken(String token) {
        String username;
        try {
        	Claims claims = Jwts.parser()
                	.setSigningKey(key)
                	.parseClaimsJws(token)
                	.getBody();
        	username = claims.getSubject();
        } catch (Exception e) {
        	e.printStackTrace();
            username = null;
        }
        return username;
    }

	public String[] getPermissionsFromToken(String token) {
        String[] permissions;
        try {
        	Claims claims = Jwts.parser()
                	.setSigningKey(key)
                	.parseClaimsJws(token)
                	.getBody();
        	@SuppressWarnings("unchecked")
			List<String> list = (List<String>)claims.get("permissions");
			permissions = list.toArray(new String[0]);
        } catch (Exception e) {
        	e.printStackTrace();
           	permissions = null;
        }
        return permissions;
    }

}
