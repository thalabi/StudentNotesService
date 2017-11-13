package com.kerneldc.education.studentNotesService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

//import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class SchoolYearRepositoryTests implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	
	@Autowired
	private JpaContext jpaContext;
	
	private EntityManager entityManager;

	@Override
	public void afterPropertiesSet() {
		entityManager = jpaContext.getEntityManagerByManagedType(SchoolYear.class);
	}

	@Test
    public void testFindOne() throws ParseException {

		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		SchoolYear schoolYearExpected = new SchoolYear();
		schoolYearExpected.setId(1l);
		schoolYearExpected.setSchoolYear("2016-2017");
		schoolYearExpected.setStartDate(dateFormat.parse("2016-09-01"));
		schoolYearExpected.setEndDate(dateFormat.parse("2017-06-30"));
		schoolYearExpected.setVersion(0l);
		//assertEquals(schoolYearExpected, schoolYear);
		assertThat(schoolYear, allOf(hasProperty("id", equalTo(schoolYearExpected.getId())),
				hasProperty("schoolYear", equalTo(schoolYearExpected.getSchoolYear()))));
	}

	@Test
	@DirtiesContext
    public void testSaveToInsert() throws ParseException {

		SchoolYear newSchoolYear = new SchoolYear();
		newSchoolYear.setSchoolYear("2018-2019");
		newSchoolYear.setStartDate(dateFormat.parse("2018-09-01"));
		newSchoolYear.setEndDate(dateFormat.parse("2019-06-30"));
		SchoolYear savedSchoolYear = schoolYearRepository.save(newSchoolYear);
		assertThat(savedSchoolYear, allOf(hasProperty("id", greaterThan(0l)), hasProperty("schoolYear", equalTo("2018-2019"))));
		assertThat(savedSchoolYear.getStartDate(), equalTo(newSchoolYear.getStartDate()));
		assertThat(savedSchoolYear.getEndDate(), equalTo(newSchoolYear.getEndDate()));
	}

	@Test(expected=DataIntegrityViolationException.class)
	@DirtiesContext
    public void testUniqueIndex1() {

		SchoolYear newSchoolYear1 = new SchoolYear();
		newSchoolYear1.setSchoolYear("2017-2018");
		schoolYearRepository.save(newSchoolYear1);
		SchoolYear newSchoolYear2 = new SchoolYear();
		newSchoolYear2.setSchoolYear("2017-2018");
		schoolYearRepository.save(newSchoolYear2);
	}

	@Test(expected=DataIntegrityViolationException.class)
	@DirtiesContext
    public void testNullSchoolYear() {

		SchoolYear newSchoolYear = new SchoolYear();
		newSchoolYear.setSchoolYear(null);
		schoolYearRepository.save(newSchoolYear);
	}

	@Test
	@DirtiesContext
    public void testSaveToUpdate() {

		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		schoolYear.setSchoolYear(schoolYear.getSchoolYear()+" v1");
		Long oldVersion = new Long(schoolYear.getVersion());
		SchoolYear savedSchoolYear = schoolYearRepository.save(schoolYear);
		entityManager.flush(); // will cause version to be bumped up
		LOGGER.debug("schoolYear: {}", schoolYear);
		LOGGER.debug("savedSchoolYear: {}", savedSchoolYear);
		assertEquals(schoolYear.getId(), savedSchoolYear.getId());
		assertEquals(schoolYear.getSchoolYear(), savedSchoolYear.getSchoolYear());
		assertEquals(new Long(oldVersion+1l), savedSchoolYear.getVersion());
	}

	@Test
	@DirtiesContext
    public void testDelete() {

		schoolYearRepository.delete(1l);
		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		assertNull(schoolYear);
	}

	@Test
    public void testFindAllByOrderBySchoolYearAsc() throws ParseException {

		SchoolYear newSchoolYear1 = new SchoolYear();
		newSchoolYear1.setSchoolYear("9998-9999");
		newSchoolYear1.setStartDate(dateFormat.parse("9998-09-01"));
		newSchoolYear1.setEndDate(dateFormat.parse("9999-06-30"));
		schoolYearRepository.save(newSchoolYear1);
		SchoolYear newSchoolYear2 = new SchoolYear();
		newSchoolYear2.setSchoolYear("2018-2019");
		newSchoolYear2.setStartDate(dateFormat.parse("2018-09-01"));
		newSchoolYear2.setEndDate(dateFormat.parse("2019-06-30"));
		schoolYearRepository.save(newSchoolYear2);
		List<SchoolYear> schoolYearList = schoolYearRepository.findAllByOrderBySchoolYearAsc();
		assertTrue(schoolYearList.size() == 4);
		String previousSchoolYear = "";
		for (SchoolYear schoolYear : schoolYearList) {
			assertTrue(schoolYear.getSchoolYear().compareTo(previousSchoolYear) >= 0);
			previousSchoolYear = schoolYear.getSchoolYear();
		}
	}

//	@Test
//    public void testGetLatestActiveStudentsBySchoolYearId() {
//		Set<SchoolYear> schoolYears = schoolYearRepository.getLatestActiveStudentsBySchoolYearId(1l, 5);
//		LOGGER.debug("schoolYears: {}", schoolYears);
//		assertEquals(1, schoolYears.size());
//		SchoolYear schoolYear = schoolYears.iterator().next(); 
//		assertEquals(2, schoolYear.getStudentSet().size());
//		for (Student student: schoolYear.getStudentSet()) {
//			if (student.getId().equals(1l)) {
//				assertEquals(3, student.getNoteSet().size());
//			}
//			if (student.getId().equals(2l)) {
//				assertEquals(0, student.getNoteSet().size());
//			}
//		}
//	}
//	
	@Test
	@DirtiesContext
	public void testCascade () throws ParseException {
		Student student = new Student();
		student.setFirstName("first name cascade");
		student.setLastName("last name cascade");
		//StudentRepository.save(student);
		SchoolYear schoolYear = new SchoolYear();
		schoolYear.setSchoolYear("sy 1");
		schoolYear.setStartDate(dateFormat.parse("2017-09-01"));
		schoolYear.setEndDate(dateFormat.parse("2018-06-30"));
		schoolYear.getStudentSet().add(student);
		schoolYearRepository.save(schoolYear);
		entityManager.flush();
		//schoolYear.getStudentSet().remove(student);
		schoolYearRepository.delete(schoolYear);
	}
}	
