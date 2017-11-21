package com.kerneldc.education.studentNotesService.repository.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.StudentNotesApplication;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.exception.SnsException;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.repository.util.RepositoryUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class RepositoryUtilsTests {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private StudentRepository studentRepository;

	@Before
	public void beforeEachTest() {
		assertThat(studentRepository, notNullValue());
	}

	@Test
    public void testGetAndCheckEntityVersion_versionChanged_failure() {

		Student student = studentRepository.findOne(1l);
		assertThat(student, notNullValue());
		
		try {
			RepositoryUtils.getAndCheckEntityVersion(1l, 7l, studentRepository);
		} catch (Exception e) {
			assertThat(e, instanceOf(SnsException.class));
			assertThat(e.getMessage(), equalTo("Student version has changed."));
		}
	}

}
