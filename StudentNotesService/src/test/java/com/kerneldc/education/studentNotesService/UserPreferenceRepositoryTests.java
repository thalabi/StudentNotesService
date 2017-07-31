package com.kerneldc.education.studentNotesService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.repository.UserPreferenceRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class UserPreferenceRepositoryTests {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private UserPreferenceRepository userPreferenceRepository;
	
	@Test
    public void testFindOne() {

		assertTrue(userPreferenceRepository != null);
		UserPreference userPreference = userPreferenceRepository.findOne(1l);		
		UserPreference userPreferenceExpected = new UserPreference();
		userPreferenceExpected.setId(1l);
		userPreferenceExpected.setUsername("TestUser");
		assertEquals(userPreferenceExpected.getId(), userPreference.getId());
		assertEquals(userPreferenceExpected.getUsername(), userPreference.getUsername());
		assertEquals("2016-2017", userPreference.getSchoolYear().getSchoolYear());
		userPreference.getSchoolYear().getStudentSet().size();
	}

	@Test
    public void testFindByUsername() {

		assertTrue(userPreferenceRepository != null);
		List<UserPreference> userPreferenceList = userPreferenceRepository.findByUsername("TestUser");
		assertEquals(1, userPreferenceList.size());
		UserPreference userPreference = userPreferenceList.get(0);
		UserPreference userPreferenceExpected = new UserPreference();
		userPreferenceExpected.setId(1l);
		userPreferenceExpected.setUsername("TestUser");
		assertEquals(userPreferenceExpected.getId(), userPreference.getId());
		assertEquals(userPreferenceExpected.getUsername(), userPreference.getUsername());
		assertEquals("2016-2017", userPreference.getSchoolYear().getSchoolYear());
	}

}
