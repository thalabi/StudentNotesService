package com.kerneldc.education.studentNotesService;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.UserPreferenceRepository;

import ch.qos.logback.classic.net.SyslogAppender;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class UserPreferenceRepositoryTests {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private UserPreferenceRepository userPreferenceRepository;
	
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	
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

	@Test
	@DirtiesContext
    public void testSave() {

		assertTrue(userPreferenceRepository != null);
		List<UserPreference> userPreferenceList = userPreferenceRepository.findByUsername("TestUser");
		assertEquals(1, userPreferenceList.size());
		UserPreference userPreference = userPreferenceList.get(0);

		SchoolYear schoolYear = schoolYearRepository.findOne(2l);
		assertThat(schoolYear.getSchoolYear(), equalTo("2017-2018"));
		userPreference.setSchoolYear(schoolYear);
		userPreferenceRepository.save(userPreference);
		System.out.println(userPreference);
//		assertThat(updatedUserPreference.getVersion(), equalTo(Long.valueOf(1l)));
	}

}
