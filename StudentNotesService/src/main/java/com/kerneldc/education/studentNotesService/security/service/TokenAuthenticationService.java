package com.kerneldc.education.studentNotesService.security.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kerneldc.education.studentNotesService.security.bean.User;
import com.kerneldc.education.studentNotesService.security.bean.UserAuthentication;
import com.kerneldc.education.studentNotesService.security.util.JwtTokenUtil;

import io.jsonwebtoken.JwtException;

@Service
public class TokenAuthenticationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	public static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public TokenAuthenticationService() {
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
	}

    public String createJwtTokenforAuthentication(String username) {
        return jwtTokenUtil.generate(username);
    }

    public Authentication getExistingAuthentication(final HttpServletRequest request) {
		LOGGER.debug("begin ...");
        String jwtToken = request.getHeader(AUTH_HEADER_NAME);
        LOGGER.debug("jwtToken: {}", jwtToken);
        if (jwtToken != null) {
            try {
                User user = jwtTokenUtil.parseToken(jwtToken);
                LOGGER.debug("user == null: {}", user == null);
                return new UserAuthentication(user);
            } catch (UsernameNotFoundException | JwtException e) {
            	LOGGER.info("Invalid token");
                return null;
            }
        }
		LOGGER.debug("end ...");
        return null;
    }

}
