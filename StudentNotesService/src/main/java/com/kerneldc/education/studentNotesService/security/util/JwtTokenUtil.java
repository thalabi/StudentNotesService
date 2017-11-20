package com.kerneldc.education.studentNotesService.security.util;

import java.io.IOException;
import java.security.Key;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.bean.UserAuthentication;
import com.kerneldc.education.studentNotesService.security.constants.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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

	public User parseToken(String token) throws IOException {
		
		User user = new User();
		LOGGER.debug("Attempting to parse token {}", token);
    	Claims claims = Jwts.parser()
            	.setSigningKey(key)
            	.parseClaimsJws(token)
            	.getBody();
    	user.setUsername(claims.getSubject());

    	ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
		// claims object is json, it needs to be deserialized
    	JsonNode jsonNodeList = objectMapper.convertValue(claims.get("authorities"), JsonNode.class);
    	Collection<GrantedAuthority> authorities = objectMapper.readValue(objectMapper.writeValueAsString(jsonNodeList), new TypeReference<Collection<SimpleGrantedAuthority>>(){});
    	//LOGGER.debug("authorities: {}", authorities);
    	
    	user.setAuthorities(authorities);
    	user.setToken(token);
		//LOGGER.debug("Token parsed. User is {}", user);
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

	public Collection<GrantedAuthority> getAuthoritiesFromToken(String token) {
		Collection<GrantedAuthority> authorities;
        try {
        	Claims claims = Jwts.parser()
                	.setSigningKey(key)
                	.parseClaimsJws(token)
                	.getBody();
        	ObjectMapper objectMapper = new ObjectMapper();
    		objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
    		// claims object is json, it needs to be deserialized
        	JsonNode jsonNodeList = objectMapper.convertValue(claims.get("authorities"), JsonNode.class);
        	authorities = objectMapper.readValue(objectMapper.writeValueAsString(jsonNodeList), new TypeReference<Collection<SimpleGrantedAuthority>>(){});
        } catch (Exception e) {
        	e.printStackTrace();
        	authorities = null;
        }
        return authorities;
    }

    public static String jwtTokenFromRequest(HttpServletRequest request) {
		String authenticationHeader = request.getHeader(SecurityConstants.AUTH_HEADER_NAME);
        LOGGER.debug("authenticationHeader: {}", authenticationHeader);
        if (authenticationHeader != null) {
        	Pattern p = Pattern.compile("^\\s*Bearer\\s+(.*)$", Pattern.CASE_INSENSITIVE);
        	Matcher m = p.matcher(authenticationHeader);
        	LOGGER.debug("matches: {}, group count: {}", m.matches(), m.groupCount());
        	if (m.matches() && m.groupCount() == 1) {
        		return m.group(1);
        	}
        }
        return null;
    }

	/**
	 * Extracts the 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
    public Authentication getAuthenticationFromToken(final HttpServletRequest request) throws IOException {
    	String jwtToken = JwtTokenUtil.jwtTokenFromRequest(request);
    	if (jwtToken != null) {
    		LOGGER.debug("|{}|", jwtToken);
            try {
                User user = parseToken(jwtToken);
                LOGGER.debug("user == null: {}", user == null);
                return new UserAuthentication(user);
            } catch (UsernameNotFoundException | JwtException | JsonProcessingException e) {
            	LOGGER.info("Invalid token");
                return null;
            }
        }
		LOGGER.debug("end ...");
        return null;
    }


}
