package com.kerneldc.education.studentNotesService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.bean.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class StudentRepositoryTests implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	@Autowired
	private StudentRepository studentRepository;
	
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
		student.setGrade(Grade.FIVE);
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Assert.assertTrue(
			updatedStudent.getId().equals(1l) &&
			updatedStudent.getFirstName().equals(student.getFirstName()) &&
			updatedStudent.getLastName().equals(student.getLastName()) &&
			updatedStudent.getGrade().equals(Grade.FIVE));
    }

	@Test
	@DirtiesContext
    public void testSaveChangeANoteTimestampAndText() {
		
		Student student = studentRepository.getStudentById(1l);
		Note note = student.getNoteList().get(0);
		note.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note.setText(note.getText()+" v1");
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Optional<Note> optionalNote = updatedStudent.getNoteList().stream()
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
		Note note = student.getNoteList().get(0);
		Long id = note.getId();
		student.getNoteList().remove(note);
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Optional<Note> optionalNote = updatedStudent.getNoteList().stream()
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
		student.getNoteList().add(note);
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Optional<Note> optionalNote = updatedStudent.getNoteList().stream()
			.filter(n->n.getTimestamp().equals(note.getTimestamp()) && n.getText().equals(note.getText()))
			.findAny();
		Assert.assertTrue(optionalNote.isPresent());
	}

	@Test
	@DirtiesContext
    public void testDelete() {
		
		studentRepository.delete(1l);
		entityManager.flush();
		Student deletedStudent = studentRepository.getStudentById(1l);
		Assert.assertTrue(deletedStudent == null);
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
		student.setGrade(Grade.OTHER);
		Student newStudent = studentRepository.save(student);
		entityManager.flush();
		List<Student> allStudents = studentRepository.getAllStudents();
		Optional<Student> optionalStudent = allStudents.stream()
				.filter(s->s.getFirstName().equals(student.getFirstName()) && 
						s.getLastName().equals(student.getLastName()) &&
						s.getGrade().equals(student.getGrade()))
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
		student.setGrade(Grade.THREE);
		Note note = new Note();
		note.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note.setText("note - testSaveNewStudentWithOneNote");
		student.getNoteList().add(note);
		Student newStudent = studentRepository.save(student);
		entityManager.flush();
		List<Student> allStudents = studentRepository.getAllStudents();
		Optional<Student> optionalStudent = allStudents.stream()
				.filter(s->s.getFirstName().equals(student.getFirstName()) && 
						s.getLastName().equals(student.getLastName()) &&
						s.getGrade().equals(student.getGrade()))
				.findAny();
		assertTrue(newStudent.getId() != null && optionalStudent.isPresent());
		assertTrue(optionalStudent.get().getNoteList().size() == 1);
		assertTrue(
			optionalStudent.get().getNoteList().get(0).getId() != null &&
			optionalStudent.get().getNoteList().get(0).getTimestamp().equals(note.getTimestamp()) &&
			optionalStudent.get().getNoteList().get(0).getText().equals(note.getText()));
	}

	@Test
    public void testGetStudentById() {

		Student student = new Student();
		student.setFirstName("first name - testGetStudentById");
		student.setLastName("last name - testGetStudentById");
		student.setGrade(Grade.TWO);
		Note note1 = new Note();
		note1.setTimestamp(new Timestamp(System.currentTimeMillis()));
		note1.setText("note 1 - testGetStudentById");
		student.getNoteList().add(note1);
		Note note2 = new Note();
		note2.setTimestamp(new Timestamp(note1.getTimestamp().getTime() - 60000)); // the second note 1 minute earlier
		note2.setText("note 2 - testGetStudentById");
		student.getNoteList().add(note2);
		Long newId = studentRepository.save(student).getId();
		entityManager.flush();
		entityManager.clear();
		Student newStudent = studentRepository.getStudentById(newId);
		Note newNote2 = newStudent.getNoteList().get(0);
		Note newNote1 = newStudent.getNoteList().get(1);
		// Test that the note with the earlier timestamp is the first note retrieved ie the order by timestamp works
		assertTrue(
			newNote2.getId() != null &&
			newNote2.getTimestamp().equals(note2.getTimestamp()) &&
			newNote2.getText().equals(note2.getText()) &&
			newNote1.getId() != null &&
			newNote1.getTimestamp().equals(note1.getTimestamp()) &&
			newNote1.getText().equals(note1.getText()));
	}
	
	// TODO test should limit to 2 and take the first student pick his/her last note and compare it
	// to the second student last note. The time stamp of the first should be later than the second
	@Test
	public void testGetLatestActiveStudents() {
		Set<Student> students = studentRepository.getLatestActiveStudents(1);
		System.out.println(students.size());
		System.out.println(new ArrayList<Student>(students).get(0).getNoteList().size());
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
		s1.setGrade(Grade.SEVEN);
		Note s1n1 = new Note();
		s1n1.setText("s1n1 note 1 text");
		s1n1.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,1).atStartOfDay()));
		Note s1n2 = new Note();
		s1n2.setText("s1n2 note 2 text");
		s1n2.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,2).atStartOfDay()));
		LOGGER.debug("s1n2.getTimestamp(): {}", s1n2.getTimestamp());
		s1.getNoteList().addAll(Arrays.asList(s1n1, s1n2));
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
		s2.setGrade(Grade.EIGHT);
		Note s2n1 = new Note();
		s2n1.setText("s2n1 note 1 text");
		s2n1.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,1).atStartOfDay()));
		s2n1.setTimestamp(Timestamp.valueOf(LocalDateTime.of(2018, 2, 1, 10, 7)));
		s2.getNoteList().add(s2n1);
		studentRepository.save(s2);

		Student s3 = new Student();
		s3.setFirstName("testGetStudentsByTimestampRange s3 first name");
		s3.setLastName("testGetStudentsByTimestampRange s3 last name");
		s3.setGrade(Grade.OTHER);
		Note s3n1 = new Note();
		s3n1.setText("s3n1 note 1 text");
		s3n1.setTimestamp(Timestamp.valueOf(LocalDate.of(2018,1,1).atStartOfDay()));
		s3n1.setTimestamp(Timestamp.valueOf(LocalDateTime.of(2018, 2, 2, 10, 7)));
		s3.getNoteList().add(s3n1);
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
		assertTrue(student.getNoteList().stream().anyMatch(
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
        		assertEquals(SeedDBData.s1.getGrade(), s.getGrade());
        	} else if (s.getId().equals(SeedDBData.s2.getId())) {
	        		assertEquals(SeedDBData.s2.getFirstName(), s.getFirstName());
	        		assertEquals(SeedDBData.s2.getLastName(), s.getLastName());
	        		assertEquals(SeedDBData.s2.getGrade(), s.getGrade());
            	} else if (s.getId().equals(SeedDBData.s3.getId())) {
            		assertEquals(SeedDBData.s3.getFirstName(), s.getFirstName());
            		assertEquals(SeedDBData.s3.getLastName(), s.getLastName());
            		assertEquals(SeedDBData.s3.getGrade(), s.getGrade());
                	}
        }		
	}
	
	@Test
	public void testNoteEquality() {
		Note note1a = studentRepository.getStudentById(1l).getNoteList().get(0);
		Note note1b = studentRepository.getStudentById(1l).getNoteList().get(0);
		System.out.println(note1a);
		System.out.println(note1b);
		entityManager.detach(note1a);
		entityManager.detach(note1b);
		assertEquals(note1a, note1b);
	}
}
