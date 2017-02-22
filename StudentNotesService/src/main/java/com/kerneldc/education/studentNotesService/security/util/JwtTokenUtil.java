package com.kerneldc.education.studentNotesService.security.util;

import java.security.Key;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.kerneldc.education.studentNotesService.security.bean.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Autowired
	private KeyProvider keyProvider; 
	private Key key;
	
	@PostConstruct
	public void init() {
		key = keyProvider.getKey();
		LOGGER.debug("key: {}", key.toString());
	}
	
	public String generate(String username, String... permissions) {
		
		Map<String, Object> claims = new HashMap<>();
		claims.put(Claims.SUBJECT, username);
		claims.put("permissions", permissions);
		String compactJws = Jwts.builder()
				  .setClaims(claims)
				  .signWith(SignatureAlgorithm.HS512, key)
				  .compact();
		LOGGER.debug("Generated token {} for username {} and permissions {}", compactJws, username, permissions);
		return compactJws;
	}

	public String generate(String username, Collection<? extends GrantedAuthority>authorities) {
		
		Map<String, Object> claims = new HashMap<>();
		claims.put(Claims.SUBJECT, username);
		claims.put("authorities", authorities);
		String compactJws = Jwts.builder()
				  .setClaims(claims)
				  .signWith(SignatureAlgorithm.HS512, key)
				  .compact();
		LOGGER.debug("Generated token {} for username {} and authorities {}", compactJws, username, authorities);
		return compactJws;
	}

	@SuppressWarnings("unchecked")
	public User parseToken(String token) {
		
		User user = new User();
		LOGGER.debug("Attempting to parse token {}", token);
    	Claims claims = Jwts.parser()
            	.setSigningKey(key)
            	.parseClaimsJws(token)
            	.getBody();
    	user.setUsername(claims.getSubject());
    	user.setAuthorities((Collection<GrantedAuthority>)claims.get("authorities"));
    	user.setToken(token);
		LOGGER.debug("Token parsed. User is {}", user);
		return user;
	}
	
	public String getUsernameFromToken(String token) {
        String username;
    	Claims claims = Jwts.parser()
            	.setSigningKey(key)
            	.parseClaimsJws(token)
            	.getBody();
    	username = claims.getSubject();
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
