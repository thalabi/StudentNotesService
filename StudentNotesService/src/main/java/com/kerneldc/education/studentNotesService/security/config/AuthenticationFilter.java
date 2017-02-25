package com.kerneldc.education.studentNotesService.security.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.kerneldc.education.studentNotesService.security.service.AuthenticationService;

/**
 * Filter that uses the token in the request header to generate an authenticated user object
 * and storing in in the security context holder
 * @author Tarif Halabi 2
 *
 */
@Component
public class AuthenticationFilter extends GenericFilterBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    private AuthenticationService authenticationService;

    public AuthenticationFilter() {
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		LOGGER.debug("begin ...");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        Authentication authentication =	authenticationService.getAuthenticationFromToken(httpRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);

        SecurityContextHolder.getContext().setAuthentication(null);
		LOGGER.debug("end ...");
	}

}
