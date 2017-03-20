package com.kerneldc.education.studentNotesService.security.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.bean.UserAuthentication;
import com.kerneldc.education.studentNotesService.security.constants.Constants;
import com.kerneldc.education.studentNotesService.security.util.JwtTokenUtil;

import io.jsonwebtoken.JwtException;

@Service
public class AuthenticationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Value("${webapp.username}")
	private String username;
	@Value("${webapp.password}")
	private String password;

	public AuthenticationService() {
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
	}

//    public String createJwtTokenforAuthentication(String username) {
//        return jwtTokenUtil.generate(username);
//    }

	/**
	 * Extracts the 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
    public Authentication getAuthenticationFromToken(final HttpServletRequest request) throws IOException {
    	String jwtToken = getJwtToken(request);
    	if (jwtToken != null) {
    		LOGGER.debug("|{}|", jwtToken);
            try {
                User user = jwtTokenUtil.parseToken(jwtToken);
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

    public User authenticate(User user) throws UsernameNotFoundException, BadCredentialsException {
    	LOGGER.debug("begin ...");
    	LOGGER.debug("username: {}, password: {}", username, password);
    	
    	if (!user.getUsername().equals(username)) {
    		LOGGER.warn("Username '{}' not found", user.getUsername());
    		throw new UsernameNotFoundException("");
    	}
    	if (!user.getPassword().equals(password)) {
    		LOGGER.warn("Password '{}' not valid", user.getPassword());
    		throw new BadCredentialsException("");
    	}
    	User newUser = new User();
    	newUser.setId(7l);
    	newUser.setUsername(user.getUsername());
    	newUser.setPassword(user.getPassword());
    	newUser.setFirstName(user.getUsername() + " first name");
    	newUser.setLastName(user.getUsername() + " last name");
    	newUser.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("authority1"), new SimpleGrantedAuthority("authority2"), new SimpleGrantedAuthority("authority3")));
    	newUser.setToken(jwtTokenUtil.generate(newUser.getUsername(), newUser.getAuthorities()));
    	LOGGER.debug("end ...");
    	return newUser;
    }
    
    private static String getJwtToken(HttpServletRequest request) {
		String authenticationHeader = request.getHeader(Constants.AUTH_HEADER_NAME);
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
}
