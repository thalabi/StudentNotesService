package com.kerneldc.education.studentNotesService.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kerneldc.education.studentNotesService.security.bean.User;

@Service
public class UserLoginService implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

//    @Autowired
//    private UserRepository userRepository;

	public UserLoginService() {
		LOGGER.debug("begin ...");
		LOGGER.debug("end ...");
	}

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		LOGGER.debug("begin ...");
//		User foundUser = userRepository.findByUsername(username);
		User foundUser = new User();
		switch (username) {
		case "thalabi":
			foundUser.setUsername("thalabi");
			foundUser.setFirstName("thalabi first name");
			foundUser.setLastName("thalabi last name");
			break;
		case "x":
			foundUser.setUsername("x");
			foundUser.setFirstName("x first name");
			foundUser.setLastName("x last name");
			break;
		default:
          throw new UsernameNotFoundException("User with username " + username + " does not exist.");
		}
//        if (foundUser == null) {
//            throw new UsernameNotFoundException("User with username " + username + " does not exist.");
//        }
		LOGGER.debug("end ...");
		return foundUser;
	}

}
