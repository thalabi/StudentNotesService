package com.kerneldc.education.studentNote;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.kerneldc.education.studentNotes.StudentNotesApplication;
import com.kerneldc.education.studentNotes.domain.Student;
import com.kerneldc.education.studentNotes.repository.StudentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//@Transactional
public class StudentRepositoryTests {

	@Autowired
	private StudentRepository studentRepository;

	@Test
    public void testSaveStudentChangeFirstLastNameAndGrade() {
		
		Student student = studentRepository.findOne(1l);
		student.setFirstName(student.getFirstName()+" v1");
		student.setLastName(student.getLastName()+" v1");
		student.setGrade("5");
		Student updatedStudent = studentRepository.save(student);
		Assert.assertTrue(
			updatedStudent.getId().equals(1l) &&
			updatedStudent.getFirstName().equals(student.getFirstName()) &&
			updatedStudent.getLastName().equals(student.getLastName()) &&
			updatedStudent.getGrade().equals("5") &&
			updatedStudent.getVersion().equals(student.getVersion()+1));
    }

}
