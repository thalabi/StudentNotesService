package com.kerneldc.education.studentNotesService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.domain.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.StudentDto;
import com.kerneldc.education.studentNotesService.dto.transformer.StudentTransformer;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;
import com.kerneldc.education.studentNotesService.repository.SchoolYearRepository;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;
import com.kerneldc.education.studentNotesService.util.KdcCollectionUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class StudentRepositoryTests implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	
	@Autowired
	private JpaContext jpaContext;
	
	private EntityManager entityManager;

	@Override
	public void afterPropertiesSet() {
		entityManager = jpaContext.getEntityManagerByManagedType(Student.class);
	}

	@Test
	@DirtiesContext
    public void testSaveChangeFirstLastNameAndGrade() {

		Student student = studentRepository.findOne(1l);
		student.setFirstName(student.getFirstName()+" v1");
		student.setLastName(student.getLastName()+" v1");
		//student.setGrade(GradeEnum.FIVE);
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Assert.assertTrue(
			updatedStudent.getId().equals(1l) &&
			updatedStudent.getFirstName().equals(student.getFirstName()) &&
			updatedStudent.getLastName().equals(student.getLastName())/* &&
			updatedStudent.getGrade().equals(GradeEnum.FIVE)*/);
    }

	@Test
	@DirtiesContext
    public void testSaveChangeANoteTimestampAndText() {
		
		Student student = studentRepository.getStudentById(1l);
		Note note = student.getNoteSet().iterator().next();
		note.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note.setText(note.getText()+" v1");
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Optional<Note> optionalNote = updatedStudent.getNoteSet().stream()
			.filter(n->n.getId().equals(note.getId()))
			.findAny();
		if (optionalNote.isPresent()) {
			Note updatedNote = optionalNote.get();
			Assert.assertTrue(
				updatedNote.getTimestamp().equals(note.getTimestamp()) &&
				updatedNote.getText().equals(note.getText()));
		} else {
			Assert.fail("Saved student does not contain note updated");
		}
	}
	
	@Test
	@DirtiesContext
    public void testSaveDeleteANote() {
		
		Student student = studentRepository.getStudentById(1l);
		Note note = student.getNoteSet().iterator().next();
		Long id = note.getId();
		student.getNoteSet().remove(note);
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Optional<Note> optionalNote = updatedStudent.getNoteSet().stream()
			.filter(n->n.getId().equals(id))
			.findAny();
		Assert.assertTrue(!optionalNote.isPresent());
	}
	
	@Test
	@DirtiesContext
    public void testSaveAddANote() {
		
		Student student = studentRepository.getStudentById(1l);
		Note note = new Note();
		note.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note.setText("note - testSaveAddANote");
		student.getNoteSet().add(note);
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Optional<Note> optionalNote = updatedStudent.getNoteSet().stream()
			.filter(n->n.getTimestamp().equals(note.getTimestamp()) && n.getText().equals(note.getText()))
			.findAny();
		Assert.assertTrue(optionalNote.isPresent());
	}

	@Test
	@DirtiesContext
    public void testDelete() {
		
		Student student = studentRepository.getStudentById(3l);
		//SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		List<Long> schoolYearIdList = KdcCollectionUtils.convertToList(student.getSchoolYearSet(), "id");
		Set<SchoolYear> schoolYearSet = student.getSchoolYearSet();
		for (SchoolYear schoolYear : schoolYearSet) {
			student.removeSchoolYear(schoolYear);
		}
		studentRepository.delete(3l);
		entityManager.flush();
		Student deletedStudent = studentRepository.getStudentById(3l);
		assertThat(deletedStudent, nullValue());
		for (Long id : schoolYearIdList) {
			SchoolYear schoolYear = schoolYearRepository.findOne(id);
			assertThat(schoolYear, notNullValue());
		}
	}

	@Test(expected=EmptyResultDataAccessException.class)
	@DirtiesContext
    public void testDeleteNotFound() {
		
		studentRepository.delete(777777l);
	}

	@Test
	@DirtiesContext
    public void testSaveNewStudentWithNoNotes() {
		
		Student student = new Student();
		student.setFirstName("first name - testSaveNewStudentWithNoNotes");
		student.setLastName("last name - testSaveNewStudentWithNoNotes");
		//student.setGrade(GradeEnum.OTHER);
		LOGGER.debug("student: {}", student);
		Student newStudent = studentRepository.save(student);
		entityManager.flush();
		List<Student> allStudents = studentRepository.getAllStudents();
		Optional<Student> optionalStudent = allStudents.stream()
				.filter(s->s.getFirstName().equals(student.getFirstName()) && 
						s.getLastName().equals(student.getLastName())/* &&
						s.getGrade().equals(student.getGrade())*/)
				.findAny();
		Assert.assertTrue(newStudent.getId() != null && optionalStudent.isPresent());
	}

	@Test
	//@DirtiesContext
    public void testSaveNewStudentWithDuplicateFirstLastName() {

		// test same first and last name
		Student student1 = new Student();
		student1.setFirstName("first name - testSaveNewStudentWithDuplicateFirstLastName");
		student1.setLastName("last name - testSaveNewStudentWithDuplicateFirstLastName");
		studentRepository.save(student1);

		Student student2 = new Student();
		student2.setFirstName("first name - testSaveNewStudentWithDuplicateFirstLastName");
		student2.setLastName("last name - testSaveNewStudentWithDuplicateFirstLastName");
		try {
			studentRepository.save(student2);
			fail("Failed test when first and last name are the same for both students");
		} catch (DataIntegrityViolationException e) {
			assertTrue(true);
		}

		// test same first name and no last name
		Student student3 = new Student();
		student3.setFirstName("first name - testSaveNewStudentWithDuplicateFirstLastName 2");
		studentRepository.save(student3);

		Student student4 = new Student();
		student4.setFirstName("first name - testSaveNewStudentWithDuplicateFirstLastName 2");
		try {
			studentRepository.save(student4);
			fail("Failed test when first name is the same and last name is null for both students");
		} catch (DataIntegrityViolationException e) {
			assertTrue(true);
		}
	
		// test same last name and no first name
		Student student5 = new Student();
		student5.setLastName("last name - testSaveNewStudentWithDuplicateFirstLastName 3");
		studentRepository.save(student5);

		Student student6 = new Student();
		student6.setLastName("last name - testSaveNewStudentWithDuplicateFirstLastName 3");
		try {
			studentRepository.save(student6);
			fail("Failed test when last name is the same and first name is null for both students");
		} catch (DataIntegrityViolationException e) {
			assertTrue(true);
		}
	}

	@Test
	@DirtiesContext
    public void testSaveNewStudentWithOneNote() {
		
		Student student = new Student();
		student.setFirstName("first name - testSaveNewStudentWithOneNote");
		student.setLastName("last name - testSaveNewStudentWithOneNote");
		Grade grade = new Grade();
		grade.setGradeEnum(GradeEnum.THREE);
		grade.setStudent(student);
		grade.setSchoolYear(schoolYearRepository.findOne(1l));
		student.getGradeSet().add(grade);
		Note note = new Note();
		note.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note.setText("note - testSaveNewStudentWithOneNote");
		student.getNoteSet().add(note);
		Student newStudent = studentRepository.save(student);
		entityManager.flush();
		List<Student> allStudents = studentRepository.getAllStudents();
		Optional<Student> optionalStudent = allStudents.stream()
				.filter(s->s.getFirstName().equals(student.getFirstName()) && 
						s.getLastName().equals(student.getLastName())/* &&
						s.getGrade().equals(student.getGrade())*/)
				.findAny();
		assertTrue(newStudent.getId() != null && optionalStudent.isPresent());
		assertTrue(optionalStudent.get().getNoteSet().size() == 1);
		assertTrue(
			optionalStudent.get().getNoteSet().iterator().next().getId() != null &&
			optionalStudent.get().getNoteSet().iterator().next().getTimestamp().equals(note.getTimestamp()) &&
			optionalStudent.get().getNoteSet().iterator().next().getText().equals(note.getText()));
	}

	@Test
	@DirtiesContext
	@Commit
    public void testGetStudentById_TwoNotesInTheRightOrder_Success() {

		Student student = new Student();
		student.setFirstName("first name - testGetStudentById");
		student.setLastName("last name - testGetStudentById");
		//student.setGrade(GradeEnum.TWO);
		Note note1 = new Note();
		note1.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note1.setText("note 1 - testGetStudentById");
		student.getNoteSet().add(note1);
		Note note2 = new Note();
		note2.setTimestamp(new Timestamp(note1.getTimestamp().getTime() - 60000)); // the second note 1 minute earlier
		note2.setText("note 2 - testGetStudentById");
		student.getNoteSet().add(note2);
		Long newId = studentRepository.save(student).getId();
		entityManager.flush();
		entityManager.clear();
		Student newStudent = studentRepository.getStudentById(newId);
		assertThat(newStudent.getNoteSet(), hasSize(2));
		Iterator<Note> iterator = newStudent.getNoteSet().iterator();
		Note newNote2 = iterator.next();
		Note newNote1 = iterator.next();
		// Test that the note with the earlier timestamp is the first note retrieved ie the order by timestamp works
		assertThat(newNote2.getId(), notNullValue());
		assertThat(newNote2.getTimestamp(), equalTo(note2.getTimestamp()));
		assertThat(newNote2.getText(), equalTo(note2.getText()));
		assertThat(newNote1.getId(), notNullValue());
		assertThat(newNote1.getTimestamp(), equalTo(note1.getTimestamp()));
		assertThat(newNote1.getText(), equalTo(note1.getText()));
	}
	
	@Test
    public void testGetStudentById_AddGrade_Success() {

		Student student = new Student();
		student.setFirstName("first name - testGetStudentById_AddGrade_Success");
		student.setLastName("last name - testGetStudentById_AddGrade_Success");
		Grade grade = new Grade();
		student.setGradeSet(new HashSet<>(Arrays.asList(grade)));
		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		grade.setSchoolYear(schoolYear);
		grade.setStudent(student);
		//grade.setGrade2(GradeEnum.SEVEN);
		Long newId = studentRepository.save(student).getId();
		entityManager.flush();
		entityManager.clear();
		//Student newStudent = studentRepository.getStudentById(newId);
		Student newStudent = studentRepository.findOne(newId);
		assertThat(newStudent.getGradeSet(), hasSize(1));
		Grade newGrade = newStudent.getGradeSet().iterator().next();
		//assertThat(newGrade.getGrade2(), equalTo(GradeEnum.SEVEN));
		assertThat(newGrade.getSchoolYear(), equalTo(schoolYear));
	}

	@Test
    public void testGetStudentById_AddGradeAndNote_Success() {

		Student student = new Student();
		student.setFirstName("first name - testGetStudentById_AddGradeAndNote_Success");
		student.setLastName("last name - testGetStudentById_AddGradeAndNote_Success");
		Grade grade = new Grade();
		student.setGradeSet(new HashSet<>(Arrays.asList(grade)));
		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
		grade.setSchoolYear(schoolYear);
		grade.setStudent(student);
		//grade.setGrade2(GradeEnum.SEVEN);
		Note note1 = new Note();
		note1.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note1.setText("note 1 - testGetStudentById_AddGradeAndNote_Success");
		student.getNoteSet().add(note1);
		Long newId = studentRepository.save(student).getId();
		entityManager.flush();
		entityManager.clear();
		//Student newStudent = studentRepository.getStudentById(newId);
		Student newStudent = studentRepository.findOne(newId);
		assertThat(newStudent.getGradeSet(), hasSize(1));
		Grade newGrade = newStudent.getGradeSet().iterator().next();
		//assertThat(newGrade.getGrade2(), equalTo(GradeEnum.SEVEN));
		assertThat(newGrade.getSchoolYear(), equalTo(schoolYear));
		assertThat(newStudent.getNoteSet(), hasSize(1));
		assertThat(newStudent.getNoteSet().iterator().next(), equalTo(note1));
	}

	// TODO test should limit to 2 and take the first student pick his/her last note and compare it
	// to the second student last note. The time stamp of the first should be later than the second
	@Test
	public void testGetLatestActiveStudents() {
		Set<Student> students = studentRepository.getLatestActiveStudents(1);
		System.out.println(students.size());
		System.out.println(new ArrayList<Student>(students).get(0).getNoteSet().size());
		assertTrue(students.size() == 1);
	}

	// TODO (test should check that each student returned has a note with a timestamp in the range, DONE)
	// also should check the remaining students ie not selected, none have a timestamp in the range
	// Add to the above, need to add more notes with timestamps that are apart so as to run the test
	// right now the database has all timestamps almost the same 
	@Test
	public void testGetStudentsByTimestampRange() {
		
		Student s1 = new Student();
		s1.setFirstName("testGetStudentsByTimestampRange s1 first name");
		s1.setLastName("testGetStudentsByTimestampRange s1 last name");
		//s1.setGrade(GradeEnum.SEVEN);
		Note s1n1 = new Note();
		s1n1.setText("s1n1 note 1 text");
		s1n1.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,1).atStartOfDay()));
		Note s1n2 = new Note();
		s1n2.setText("s1n2 note 2 text");
		s1n2.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,2).atStartOfDay()));
		LOGGER.debug("s1n2.getTimestamp(): {}", s1n2.getTimestamp());
		s1.getNoteSet().addAll(Arrays.asList(s1n1, s1n2));
		studentRepository.save(s1);
		entityManager.flush();

		Timestamp fromTimestamp = Timestamp.valueOf(LocalDate.of(2018, 1, 1).atStartOfDay());
		Timestamp toTimestamp = Timestamp.valueOf(LocalDate.of(2018, 1, 1).atStartOfDay());
		Set<Student> students = studentRepository.getStudentsByTimestampRange(fromTimestamp, toTimestamp);
		assertEquals(1, students.size());
		students.stream().forEach(student->checkStudentHasAtLeastOneTimestampBetween(student, fromTimestamp, toTimestamp));
		
		Timestamp fromTimestamp2 = Timestamp.valueOf(LocalDate.of(2018, 1, 2).atStartOfDay());
		Timestamp toTimestamp2 = Timestamp.valueOf(LocalDate.of(2018, 1, 2).atStartOfDay());
		Set<Student> students2 = studentRepository.getStudentsByTimestampRange(fromTimestamp2, toTimestamp2);
		assertEquals(1, students2.size());
		students2.stream().forEach(student->checkStudentHasAtLeastOneTimestampBetween(student, fromTimestamp2, toTimestamp2));

		Timestamp fromTimestamp3 = Timestamp.valueOf(LocalDate.of(2018, 1, 2).atStartOfDay());
		Timestamp toTimestamp3 = Timestamp.valueOf(LocalDate.of(2018, 1, 3).atStartOfDay());
		Set<Student> students3 = studentRepository.getStudentsByTimestampRange(fromTimestamp3, toTimestamp3);
		assertEquals(1, students3.size());
		students3.stream().forEach(student->checkStudentHasAtLeastOneTimestampBetween(student, fromTimestamp3, toTimestamp3));

		Timestamp fromTimestamp4 = Timestamp.valueOf(LocalDate.of(2018, 1, 3).atStartOfDay());
		Timestamp toTimestamp4 = Timestamp.valueOf(LocalDate.of(2018, 1, 3).atStartOfDay());
		Set<Student> students4 = studentRepository.getStudentsByTimestampRange(fromTimestamp4, toTimestamp4);
		assertEquals(0, students4.size());
		students4.stream().forEach(student->checkStudentHasAtLeastOneTimestampBetween(student, fromTimestamp4, toTimestamp4));

		Student s2 = new Student();
		s2.setFirstName("testGetStudentsByTimestampRange s2 first name");
		s2.setLastName("testGetStudentsByTimestampRange s2 last name");
		//s2.setGrade(GradeEnum.EIGHT);
		Note s2n1 = new Note();
		s2n1.setText("s2n1 note 1 text");
		s2n1.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,1).atStartOfDay()));
		s2n1.setTimestamp(Timestamp.valueOf(LocalDateTime.of(2018, 2, 1, 10, 7)));
		s2.getNoteSet().add(s2n1);
		studentRepository.save(s2);

		Student s3 = new Student();
		s3.setFirstName("testGetStudentsByTimestampRange s3 first name");
		s3.setLastName("testGetStudentsByTimestampRange s3 last name");
		//s3.setGrade(GradeEnum.OTHER);
		Note s3n1 = new Note();
		s3n1.setText("s3n1 note 1 text");
		s3n1.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,1).atStartOfDay()));
		s3n1.setTimestamp(Timestamp.valueOf(LocalDateTime.of(2018, 2, 2, 10, 7)));
		s3.getNoteSet().add(s3n1);
		studentRepository.save(s3);
		
		entityManager.flush();

		Timestamp fromTimestamp5 = Timestamp.valueOf(LocalDate.of(2018, 2, 1).atStartOfDay());
		Timestamp toTimestamp5 = Timestamp.valueOf(LocalDate.of(2018, 2, 10).atStartOfDay());
		Set<Student> students5 = studentRepository.getStudentsByTimestampRange(fromTimestamp5, toTimestamp5);
		assertEquals(2, students5.size());
		students5.stream().forEach(student->checkStudentHasAtLeastOneTimestampBetween(student, fromTimestamp5, toTimestamp5));

		Timestamp fromTimestamp6 = Timestamp.valueOf(LocalDate.of(2018, 2, 1).atStartOfDay());
		Timestamp toTimestamp6 = Timestamp.valueOf(LocalDate.of(2018, 2, 2).atTime(LocalTime.MAX));
		Set<Student> students6 = studentRepository.getStudentsByTimestampRange(fromTimestamp6, toTimestamp6);
		assertEquals(2, students6.size());
		students6.stream().forEach(student->checkStudentHasAtLeastOneTimestampBetween(student, fromTimestamp6, toTimestamp6));

		Timestamp fromTimestamp7 = Timestamp.valueOf(LocalDate.of(2018, 2, 1).atStartOfDay());
		Timestamp toTimestamp7 = Timestamp.valueOf(LocalDate.of(2018, 2, 1).atTime(LocalTime.MAX));
		Set<Student> students7 = studentRepository.getStudentsByTimestampRange(fromTimestamp7, toTimestamp7);
		assertEquals(1, students7.size());
		students7.stream().forEach(student->checkStudentHasAtLeastOneTimestampBetween(student, fromTimestamp7, toTimestamp7));
	}
	
	private void checkStudentHasAtLeastOneTimestampBetween (Student student, Timestamp fromTimestamp, Timestamp toTimestamp) {
		assertTrue(student.getNoteSet().stream().anyMatch(
				note -> checkNotesTimestampIsBetween(note, fromTimestamp, toTimestamp)
		));
	}
	private boolean checkNotesTimestampIsBetween (Note note, Timestamp fromTimestamp, Timestamp toTimestamp) {
		return (note.getTimestamp().equals(fromTimestamp) || note.getTimestamp().after(fromTimestamp)) &&
				(note.getTimestamp().equals(toTimestamp) || note.getTimestamp().before(toTimestamp));
	}

	@Test
	public void testGetStudentsByListOfIds() {
		assertEquals(2,studentRepository.getStudentsByListOfIds(Arrays.asList(1l,3l)).size());
	}
	
	@Test
	public void testGetAllStudents() {
		
		List<Student> students = studentRepository.getAllStudents();
        for (Student s: students) {
        	if (s.getId().equals(SeedDBData.s1.getId())) {
        		assertEquals(SeedDBData.s1.getFirstName(), s.getFirstName());
        		assertEquals(SeedDBData.s1.getLastName(), s.getLastName());
        		//assertEquals(SeedDBData.s1.getGrade(), s.getGrade());
        	} else if (s.getId().equals(SeedDBData.s2.getId())) {
	        		assertEquals(SeedDBData.s2.getFirstName(), s.getFirstName());
	        		assertEquals(SeedDBData.s2.getLastName(), s.getLastName());
	        		//assertEquals(SeedDBData.s2.getGrade(), s.getGrade());
            	} else if (s.getId().equals(SeedDBData.s3.getId())) {
            		assertEquals(SeedDBData.s3.getFirstName(), s.getFirstName());
            		assertEquals(SeedDBData.s3.getLastName(), s.getLastName());
            		//assertEquals(SeedDBData.s3.getGrade(), s.getGrade());
                	}
        }		
	}
	
	@Test
	public void testNoteEquality() {
		Note note1a = studentRepository.getStudentById(1l).getNoteSet().iterator().next();
		Note note1b = studentRepository.getStudentById(1l).getNoteSet().iterator().next();
		System.out.println(note1a);
		System.out.println(note1b);
		entityManager.detach(note1a);
		entityManager.detach(note1b);
		assertEquals(note1a, note1b);
	}
	
	@Test
	public void testFindOne() {
		Student student = studentRepository.findOne(1l);
		//student.getSchoolYearSet().size();
	}
	
	@Test
	public void testGetStudentById() {
		Student student = studentRepository.getStudentById(1l);
	}
	
	@Test
	public void testGetStudentByIdWithGradeList() {
		Student student = studentRepository.getStudentByIdWithGradeList(1l);
	}
	
	@Test
	public void testGetStudentByIdWithNodeListAndGradeList() {
		Student student = studentRepository.getStudentByIdWithNoteListAndGradeList(1l);
	}

	@Test
	@DirtiesContext
	//@Commit
	public void testAddSchoolYearToStudent() {
		Student student = studentRepository.findOne(3l);
		SchoolYear schoolYear = schoolYearRepository.findOne(2l);
		LOGGER.debug("schoolYear.getStudentSet().size(): {}", schoolYear.getStudentSet().size());
		Long schoolYearVersion = new Long(schoolYear.getVersion());
		student.addSchoolYear(schoolYear);
		studentRepository.save(student);
		entityManager.flush();
		assertThat(student.getSchoolYearSet(), hasSize(2));
		assertThat(schoolYear.getStudentSet(), hasSize(1));
		assertThat(schoolYear.getVersion(), equalTo(schoolYearVersion+1l));
	}

	@Test
	@DirtiesContext
	public void testRemoveSchoolYearFromStudent() {
		Student student = studentRepository.findOne(1l);
		assertThat(student.getSchoolYearSet(), hasSize(1));
		assertThat(student.getSchoolYearSet().iterator().next().getSchoolYear(), equalTo("2016-2017"));
		student.removeSchoolYear(student.getSchoolYearSet().iterator().next());
		studentRepository.save(student);
		entityManager.flush();
		assertThat(student.getSchoolYearSet(), hasSize(0));
	}

	@Test
	@DirtiesContext
	//@Commit
	public void testAddTwoSchoolYearsToStudent() {
		Student student = studentRepository.findOne(3l);
		SchoolYear schoolYear1 = schoolYearRepository.findOne(1l);
		assertThat(schoolYear1.getStudentSet(), hasSize(3));
		SchoolYear schoolYear2 = new SchoolYear();
		schoolYear2.setSchoolYear("2018-2019");
		schoolYear2.setStartDate(Date.valueOf(LocalDate.of(2018, 9, 1)));
		schoolYear2.setEndDate(Date.valueOf(LocalDate.of(2019, 6, 30)));
		schoolYearRepository.save(schoolYear2);
		entityManager.flush();
		Long schoolYear1Version = new Long(schoolYear1.getVersion());
		Long schoolYear2Version = new Long(schoolYear2.getVersion());
		student.addSchoolYear(schoolYear1);
		student.addSchoolYear(schoolYear2);
		LOGGER.debug("student: {}", student);
		studentRepository.save(student);
		entityManager.flush();
		assertThat(student.getSchoolYearSet(), hasSize(2));
		assertThat(schoolYear1.getStudentSet(), hasSize(3));
		assertThat(schoolYear2.getStudentSet(), hasSize(1));
		assertThat(schoolYear1.getVersion(), equalTo(schoolYear1Version));
		assertThat(schoolYear2.getVersion(), equalTo(schoolYear2Version+1l));
	}


	@Test
	@DirtiesContext
	@Commit
	public void testRemoveOneSchoolYearFromStudentWithTwoSchoolYears() {
		Student student = studentRepository.findOne(3l);
		SchoolYear secondSchoolYear = schoolYearRepository.findOne(2l);
		
		Long schoolYear1Version = new Long(secondSchoolYear.getVersion());
		student.addSchoolYear(secondSchoolYear);
		studentRepository.save(student);
		entityManager.flush();
		
		student.removeSchoolYear(secondSchoolYear);
		studentRepository.save(student);
		entityManager.flush();
		assertThat(student.getSchoolYearSet(), hasSize(1));
		assertThat(secondSchoolYear.getStudentSet(), hasSize(0));
		assertEquals(new Long(schoolYear1Version+2l), secondSchoolYear.getVersion());
//		assertEquals(new Long(schoolYear2Version+1l), schoolYear2.getVersion());
	}

	@Test
	@DirtiesContext
	public void testGetStudentsForSchoolYear() {
//		Student student1 = studentRepository.findOne(1l);
//		Student student2 = studentRepository.findOne(2l);
//		SchoolYear schoolYear = schoolYearRepository.findOne(1l);
//		Long schoolYearVersion = new Long(schoolYear.getVersion());
//		student1.addSchoolYear(schoolYear);
//		studentRepository.save(student1);
//		entityManager.flush();
//		student2.addSchoolYear(schoolYear);
//		studentRepository.save(student2);
//		entityManager.flush();
//
//		assertEquals(2, schoolYear.getStudentSet().size());
//		assertEquals(new Long(schoolYearVersion+2l), schoolYear.getVersion());
//		entityManager.detach(student1);
//		entityManager.detach(student2);
//		schoolYearEntityManager.detach(schoolYear);
//		entityManager.clear();
//		schoolYearEntityManager.clear();
		LOGGER.debug("================================================");
		//SchoolYear schoolYearWithGraph = schoolYearRepository.getSchoolYearById(1l);
		SchoolYear schoolYearWithGraph = new SchoolYear();
		LOGGER.debug("schoolYearWithGraph: {}",schoolYearWithGraph);
		schoolYearWithGraph = schoolYearRepository.findOne(1l);
		//schoolYearWithGraph.setStudentSet(new HashSet<Student>());
		LOGGER.debug("schoolYearWithGraph: {}",schoolYearWithGraph);
//		entityManager.flush();
//		Iterator<Student> iterator = schoolYearWithGraph.getStudentSet().iterator();
//		Student firstStudent = iterator.next();
//		LOGGER.debug("firstStudent.getFirstName(): {}", firstStudent.getFirstName());
//		Student secondStudent = iterator.next();
//		LOGGER.debug("secondStudent.getFirstName(): {}", secondStudent.getFirstName());
//		assertEquals(3, secondStudent.getNoteList().size());
		//student1.getNoteList().size();
	}
	
	@Test
	public void testGetStudentDtosInSchoolYear() {
		List<StudentDto> students = studentRepository.getStudentDtosInSchoolYear(1l);
		assertThat(students, hasSize(3));
	}
	@Test
	public void testGetStudentDtosNotInSchoolYear() {
		List<StudentDto> students = studentRepository.getStudentDtosNotInSchoolYear(2l);
		assertThat(students, hasSize(3));
	}

	@Test
	@Commit
	public void testChangeGrade_Success() {
		//Student student = studentRepository.findOne(1l);
		Student student = studentRepository.getStudentByIdWithGradeList(1l);
		Grade grade = student.getGradeSet().iterator().next();
		assertThat(grade.getGradeEnum(), equalTo(GradeEnum.JK));
		assertThat(student.getGradeSet(), hasSize(1));
		grade.setGradeEnum(GradeEnum.EIGHT);
		assertThat(grade.getVersion(), equalTo(0l));
		entityManager.flush();
		assertThat(grade.getVersion(), equalTo(1l));
	}

	@Test
	@Commit
	public void testChangeGrade_UsingStudentFromUsernameTable_Success() {
		//Student student = studentRepository.findOne(1l);
		//Student student = studentRepository.getStudentByIdWithGradeList(1l);
		List<StudentUiDto> students = studentRepository.getStudentsByUsername("TestUser");
		assertThat(students, hasSize(3));
		Map<Long, StudentUiDto> idToStudentUiDtoMap = KdcCollectionUtils.convertToMap(students, "id", Long.class);
		StudentUiDto studentUiDto = idToStudentUiDtoMap.get(1l);
		Student student = StudentTransformer.uiDtoToEntity(studentUiDto);
		Grade grade = student.getGradeSet().iterator().next();
		assertThat(grade.getGradeEnum(), equalTo(GradeEnum.JK));
		assertThat(student.getGradeSet(), hasSize(1));
		grade.setGradeEnum(GradeEnum.EIGHT);
		assertThat(grade.getVersion(), equalTo(0l));
		Student savedStudent = studentRepository.save(student);
		entityManager.flush();
		assertThat(savedStudent.getGradeSet().iterator().next().getVersion(), equalTo(1l));
	}

	@Test
	@Commit
	public void testAddGrade_Success() {
		//Student student = studentRepository.findOne(1l);
		Student student = studentRepository.getStudentByIdWithGradeList(1l);
		Grade grade = student.getGradeSet().iterator().next();
		assertThat(student.getGradeSet(), hasSize(1));
		assertThat(grade.getGradeEnum(), equalTo(GradeEnum.JK));
		Grade newGrade = new Grade();
		SchoolYear newSchoolYear = schoolYearRepository.findOne(2l);
		newGrade.setStudent(student);
		newGrade.setSchoolYear(newSchoolYear);
		newGrade.setGradeEnum(GradeEnum.SK);
		student.getGradeSet().add(newGrade);
		entityManager.flush();
		//assertThat(grade.getVersion(), equalTo(1l));
	}
	
	@Test
	//@Commit
	public void testAddGradeUsingASuppliedEnityObject_Success() {
		Student student = studentRepository.getStudentByIdWithGradeList(1l);
		Grade grade = student.getGradeSet().iterator().next();
		assertThat(student.getGradeSet(), hasSize(1));
		assertThat(grade.getGradeEnum(), equalTo(GradeEnum.JK));
		Grade newGrade = new Grade();
		SchoolYear existingSchoolYear = new SchoolYear(); //SuppliedEnityObject
		existingSchoolYear.setId(2l);
		existingSchoolYear.setVersion(0l); // version has to be non null otherwise Hibernate thinks object is detached
		newGrade.setStudent(student);
		newGrade.setSchoolYear(existingSchoolYear);
		newGrade.setGradeEnum(GradeEnum.SK);
		student.getGradeSet().add(newGrade);
		entityManager.flush();
		entityManager.clear();
		student = studentRepository.getStudentByIdWithGradeList(1l);
		assertThat(student.getGradeSet(), hasSize(2));
	}

	@Test
	public void testGetStudentsByUsername() {
		List<StudentUiDto> students = studentRepository.getStudentsByUsername("TestUser");
		assertThat(students, hasSize(3));
	}

	@Test
	public void testgetStudentsByUsernameAndListOfIds() {
		List<StudentUiDto> students = studentRepository.getStudentsByUsernameAndListOfIds(Long.valueOf(1l), Arrays.asList(Long.valueOf(1l)));
		assertThat(students, hasSize(1));
	}

	@Test
	public void testGetStudentGraphBySchoolYear() {
		List<StudentUiDto> students = studentRepository.getStudentGraphBySchoolYear(Long.valueOf(1l));
		assertThat(students, hasSize(3));
	}
}