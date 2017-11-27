package com.kerneldc.education.studentNotesService.security.service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.util.JwtTokenUtil;

@Service
public class JwtAuthenticationService implements AuthenticationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Value("${webapp.username}")
	private String username;
	@Value("${webapp.password}")
	private String password;

	public JwtAuthenticationService() {
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
	}

    public UserDetails authenticate(UserDetails user) {
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
    
}
