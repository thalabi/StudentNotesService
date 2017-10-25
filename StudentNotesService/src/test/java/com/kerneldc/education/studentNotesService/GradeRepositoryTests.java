package com.kerneldc.education.studentNotesService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.repository.Grade2Repository;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class GradeRepositoryTests implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private Grade2Repository gradeRepository;
	
	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private SchoolYearRepository schoolYearRepository;
	
	@Autowired
	private JpaContext jpaContext;

	private EntityManager entityManager;

	@Override
	public void afterPropertiesSet() {
		entityManager = jpaContext.getEntityManagerByManagedType(Grade.class);
	}

	@Before
	public void beforeEachTest() {
		assertThat(gradeRepository, notNullValue());
		assertThat(studentRepository, notNullValue());
		assertThat(schoolYearRepository, notNullValue());
	}
	
	@Test
    public void testFindOne() {

		Grade grade = gradeRepository.findOne(1l);
		assertThat(grade, notNullValue());
		assertThat(grade.getGradeEnum(), equalTo(GradeEnum.JK));
		assertThat(grade.getStudent().getId(), equalTo(1l));
		assertThat(grade.getSchoolYear().getId(), equalTo(1l));
	}

	@Test
    public void testSave_UpdateGrade_Success() {

		Grade grade = gradeRepository.findOne(1l);
		assertThat(grade, notNullValue());
		grade.setGradeEnum(GradeEnum.TWO);
		Grade savedGrade = gradeRepository.save(grade);
		entityManager.flush();
		assertThat(savedGrade.getGradeEnum(), equalTo(GradeEnum.TWO));
		assertThat(savedGrade.getStudent().getId(), equalTo(1l));
		assertThat(savedGrade.getSchoolYear().getId(), equalTo(1l));
	}

	@Test
	public void testSave_Success() {

		Student student = studentRepository.findOne(1l);
		assertThat(student, notNullValue());
		SchoolYear schoolYear = schoolYearRepository.findOne(2l);
		assertThat(schoolYear, notNullValue());
		Grade grade = new Grade();
		grade.setStudent(student);
		grade.setSchoolYear(schoolYear);
		grade.setGradeEnum(GradeEnum.TWO);
		Grade savedGrade = gradeRepository.save(grade);
		assertThat(savedGrade.getId(), notNullValue());
	}

	@Test
	public void testSave_NullStudent_Failure() {
		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		assertThat(schoolYear, notNullValue());
		Grade newGrade2 = new Grade();
		newGrade2.setSchoolYear(schoolYear);
		newGrade2.setGradeEnum(GradeEnum.ONE);
		try {
			gradeRepository.save(newGrade2);
			fail("Should have throw DataIntegrityViolationException");
		} catch (Exception e) {
			assertThat(e, instanceOf(DataIntegrityViolationException.class));
		}
	}

	@Test
	public void testSave_NullSchoolYear_Failure() {
		Student student = studentRepository.findOne(1l);
		assertThat(student, notNullValue());
		Grade newGrade2 = new Grade();
		newGrade2.setStudent(student);
		newGrade2.setGradeEnum(GradeEnum.ONE);
		try {
			gradeRepository.save(newGrade2);
			fail("Should have throw DataIntegrityViolationException");
		} catch (Exception e) {
			assertThat(e, instanceOf(DataIntegrityViolationException.class));
		}
	}

	@Test
	public void testSave_FectchTree_Success() {

//		Student student = studentRepository.findOne(1l);
//		assertThat(student, notNullValue());
//		SchoolYear schoolYear = schoolYearRepository.findOne(2l);
//		assertThat(schoolYear, notNullValue());
//		Grade newGrade2 = new Grade();
//		student.getGradeSet().add(newGrade2);
//		newGrade2.setStudent(student);
//		newGrade2.setSchoolYear(schoolYear);
//		newGrade2.setGrade(GradeEnum.ONE);
//		Grade createdGrade2 = gradeRepository.save(newGrade2);
//		entityManager.flush();
		
		Grade grade = gradeRepository.findOne(1l);
		LOGGER.debug("grade.getGrade2(): {}", grade.getGradeEnum());
		LOGGER.debug("grade.getStudent(): {}", grade.getStudent());
//		LOGGER.debug("createdGrade2.getGrade2().getFirstName(): {}", createdGrade2.getStudent().getFirstName());
//		LOGGER.debug("createdGrade2.getStudent().getGradeSet().iterator().next().getGrade2(): {}", createdGrade2.getStudent().getGradeSet().size());
//		LOGGER.debug("{}", createdGrade2.getStudent().getGradeSet().iterator().next().getSchoolYear().getStudentSet().iterator().next().getSchoolYearSet().size());

	}

}
