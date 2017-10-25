package com.kerneldc.education.studentNotesService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    public void testFindOne() {

		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		SchoolYear schoolYearExpected = new SchoolYear();
		schoolYearExpected.setId(1l);
		schoolYearExpected.setSchoolYear("2016-2017");
		schoolYearExpected.setStartDate(Date.valueOf(LocalDate.of(2016, 9, 1)));
		schoolYearExpected.setEndDate(Date.valueOf(LocalDate.of(2017, 6, 30)));
		schoolYearExpected.setVersion(0l);
		assertEquals(schoolYearExpected, schoolYear);
	}

	@Test
	@DirtiesContext
    public void testSaveToInsert() {

		SchoolYear newSchoolYear = new SchoolYear();
		newSchoolYear.setSchoolYear("2018-2019");
		newSchoolYear.setStartDate(Date.valueOf(LocalDate.of(2018, 9, 1)));
		newSchoolYear.setEndDate(Date.valueOf(LocalDate.of(2019, 6, 30)));
		SchoolYear savedSchoolYear = schoolYearRepository.save(newSchoolYear);
		assertTrue(savedSchoolYear.getId().compareTo(0l) > 0 &&
				savedSchoolYear.getSchoolYear().equals(newSchoolYear.getSchoolYear()) &&
				savedSchoolYear.getVersion().equals(0l));
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
    public void testFindAllByOrderBySchoolYearAsc() {

		SchoolYear newSchoolYear1 = new SchoolYear();
		newSchoolYear1.setSchoolYear("9998-9999");
		newSchoolYear1.setStartDate(Date.valueOf(LocalDate.of(9998, 9, 1)));
		newSchoolYear1.setEndDate(Date.valueOf(LocalDate.of(9999, 6, 30)));
		schoolYearRepository.save(newSchoolYear1);
		SchoolYear newSchoolYear2 = new SchoolYear();
		newSchoolYear2.setSchoolYear("2018-2019");
		newSchoolYear2.setStartDate(Date.valueOf(LocalDate.of(2018, 9, 1)));
		newSchoolYear2.setEndDate(Date.valueOf(LocalDate.of(2019, 6, 30)));
		schoolYearRepository.save(newSchoolYear2);
		List<SchoolYear> schoolYearList = schoolYearRepository.findAllByOrderBySchoolYearAsc();
		assertTrue(schoolYearList.size() == 4);
		String previousSchoolYear = "";
		for (SchoolYear schoolYear : schoolYearList) {
			assertTrue(schoolYear.getSchoolYear().compareTo(previousSchoolYear) >= 0);
			previousSchoolYear = schoolYear.getSchoolYear();
		}
	}

	@Test
    public void testGetStudentsBySchoolYearId() {
		SchoolYear schoolYear = schoolYearRepository.getStudentsBySchoolYearId(1l);
		LOGGER.debug("schoolYear: {}", schoolYear);
		assertEquals(2, schoolYear.getStudentSet().size());
		for (Student student: schoolYear.getStudentSet()) {
			if (student.getId().equals(1l)) {
				assertEquals(3, student.getNoteSet().size());
			}
			if (student.getId().equals(2l)) {
				assertEquals(0, student.getNoteSet().size());
			}
		}
	}

	@Test
    public void testGetLatestActiveStudentsBySchoolYearId() {
		Set<SchoolYear> schoolYears = schoolYearRepository.getLatestActiveStudentsBySchoolYearId(1l, 5);
		LOGGER.debug("schoolYears: {}", schoolYears);
		assertEquals(1, schoolYears.size());
		SchoolYear schoolYear = schoolYears.iterator().next(); 
		assertEquals(1, schoolYear.getStudentSet().size());
		for (Student student: schoolYear.getStudentSet()) {
			if (student.getId().equals(1l)) {
				assertEquals(3, student.getNoteSet().size());
			}
//			if (student.getId().equals(2l)) {
//				assertEquals(0, student.getNoteList().size());
//			}
		}
	}
}	
