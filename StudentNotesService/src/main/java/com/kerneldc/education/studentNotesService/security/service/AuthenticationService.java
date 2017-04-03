package com.kerneldc.education.studentNotesService.security.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationService {

	UserDetails authenticate(UserDetails user) throws UsernameNotFoundException, BadCredentialsException;
}
