package com.kerneldc.education.studentNotesService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.repository.StudentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@Transactional
public class StudentRepositoryTests implements InitializingBean {

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
    public void testSaveChangeFirstLastNameAndGrade() {

		Student student = studentRepository.findOne(1l);
		student.setFirstName(student.getFirstName()+" v1");
		student.setLastName(student.getLastName()+" v1");
		student.setGrade("5");
		Student updatedStudent = studentRepository.save(student);
		entityManager.flush();
		Assert.assertTrue(
			updatedStudent.getId().equals(1l) &&
			updatedStudent.getFirstName().equals(student.getFirstName()) &&
			updatedStudent.getLastName().equals(student.getLastName()) &&
			updatedStudent.getGrade().equals("5"));
    }

	@Test
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
    public void testDelete() {
		
		Student student = studentRepository.getStudentById(1l);
		studentRepository.delete(student);
		entityManager.flush();
		Student deletedStudent = studentRepository.getStudentById(1l);
		Assert.assertTrue(deletedStudent == null);
	}

	@Test
    public void testSaveNewStudentWithNoNotes() {
		
		Student student = new Student();
		student.setFirstName("first name - testSaveNewStudentWithNoNotes");
		student.setLastName("last name - testSaveNewStudentWithNoNotes");
		student.setGrade("Other");
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
    public void testSaveNewStudentWithOneNote() {
		
		Student student = new Student();
		student.setFirstName("first name - testSaveNewStudentWithOneNote");
		student.setLastName("last name - testSaveNewStudentWithOneNote");
		student.setGrade("3");
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
		Assert.assertTrue(newStudent.getId() != null && optionalStudent.isPresent());
		Assert.assertTrue(optionalStudent.get().getNoteList().size() == 1);
		Assert.assertTrue(
			optionalStudent.get().getNoteList().get(0).getId() != null &&
			optionalStudent.get().getNoteList().get(0).getTimestamp().equals(note.getTimestamp()) &&
			optionalStudent.get().getNoteList().get(0).getText().equals(note.getText()));
	}

	@Test
    public void testGetStudentById() {

		Student student = new Student();
		student.setFirstName("first name - testGetStudentById");
		student.setLastName("last name - testGetStudentById");
		student.setGrade("2");
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
		Assert.assertTrue(
			newNote2.getId() != null &&
			newNote2.getTimestamp().equals(note2.getTimestamp()) &&
			newNote2.getText().equals(note2.getText()) &&
			newNote1.getId() != null &&
			newNote1.getTimestamp().equals(note1.getTimestamp()) &&
			newNote1.getText().equals(note1.getText()));
	}
	
	@Test
	public void testGetLatestActiveStudents() {
		List<Student> students = studentRepository.getLatestActiveStudents(1);
		System.out.println(students.size());
		System.out.println(students.get(0).getNoteList().size());
		Assert.assertTrue(students.size() == 1);
	}
}
