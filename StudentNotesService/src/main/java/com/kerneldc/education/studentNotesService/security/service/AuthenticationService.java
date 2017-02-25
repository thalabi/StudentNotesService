package com.kerneldc.education.studentNotesService.security.service;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
		LOGGER.debug("begin ...");
        String jwtToken = request.getHeader(Constants.AUTH_HEADER_NAME);
        LOGGER.debug("jwtToken: {}", jwtToken);
        if (jwtToken != null) {
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

    public User authenticate(User user) {
    	LOGGER.debug("begin ...");
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
}
