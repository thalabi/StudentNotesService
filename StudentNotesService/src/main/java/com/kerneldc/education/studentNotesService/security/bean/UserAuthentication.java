package com.kerneldc.education.studentNotesService.security.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	private User user;
	
	public UserAuthentication(User user) {
		super(null);
		LOGGER.debug("begin ...");
		this.user = user;
		setAuthenticated(true);
		LOGGER.debug("end ...");
	}

	@Override
	public Object getCredentials() {
		return user.getPassword();
	}

	@Override
	public Object getPrincipal() {
		return user.getUsername();
	}

    @Override
    public User getDetails() {
        return user;
    }
}
