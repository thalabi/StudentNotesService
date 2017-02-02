package com.kerneldc.education.studentNote;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import com.kerneldc.education.studentNotes.StudentNotesApplication;
import com.kerneldc.education.studentNotes.bean.Students;
import com.kerneldc.education.studentNotes.domain.Note;
import com.kerneldc.education.studentNotes.domain.Student;
import com.kerneldc.education.studentNotes.repository.StudentRepository;
import com.kerneldc.education.studentNotes.service.StudentNotesReportService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class StudentNotesReportServiceTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private StudentNotesReportService studentNotesReportService;

	@Test
    public void testBeanToXml() throws JAXBException {

		// Create student # 1
		String firstName = "first name testBeanToXml";
		String lastName = "last name testBeanToXml";
		String grade = "5";
		Student student = new Student();
		student.setFirstName(firstName);
		student.setLastName(lastName);
		student.setGrade(grade);
		
		String note1TimestampText = "2017-01-29 16:11";
		String note1Text = "note 1 text testBeanToXml";
		String note2TimestampText = "2017-01-29 16:12";
		String note2Text = "note 2 text testBeanToXml";
		Note note1 = new Note();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse(note1TimestampText, formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText(note1Text);
		Note note2 = new Note();
		LocalDateTime timestamp2 = LocalDateTime.parse(note2TimestampText, formatter);
		note2.setTimestamp(Timestamp.valueOf(timestamp2));
		note2.setText(note2Text);

		student.setNoteList(Arrays.asList(note1, note2));

		// Create student # 2
		String firstName2 = "first name 2 testBeanToXml";
		String lastName2 = "last name 2 testBeanToXml";
		String grade2 = "4";
		Student student2 = new Student();
		student2.setFirstName(firstName2);
		student2.setLastName(lastName2);
		student2.setGrade(grade2);
		/*
		String note1TimestampText = "2017-01-29 16:11";
		String note1Text = "note 1 text testBeanToXml";
		String note2TimestampText = "2017-01-29 16:12";
		String note2Text = "note 2 text testBeanToXml";
		Note note1 = new Note();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse(note1TimestampText, formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText(note1Text);
		Note note2 = new Note();
		LocalDateTime timestamp2 = LocalDateTime.parse(note2TimestampText, formatter);
		note2.setTimestamp(Timestamp.valueOf(timestamp2));
		note2.setText(note2Text);

		student.setNoteList(Arrays.asList(note1, note2));
		*/
		
		Student createdStudent = studentRepository.save(student);
		Student createdStudent2 = studentRepository.save(student2);
		
		Students students = new Students();
		students.setStudentList(Arrays.asList(createdStudent, createdStudent2));

		LOGGER.info("createdStudent: {}", createdStudent);
		LOGGER.info("createdStudent2: {}", createdStudent2);
		
		// Verify createdStudent
		assert(
			createdStudent.getId().compareTo(0l) > 0 &&
			createdStudent.getFirstName().equals(firstName) &&
			createdStudent.getLastName().equals(lastName) &&
			createdStudent.getGrade().equals(grade) &&
			createdStudent.getVersion().compareTo(0l) == 0 &&
			createdStudent.getNoteList().size() == 2
			);
		
		assert(
				createdStudent.getNoteList().get(0).getId().compareTo(0l) > 0 &&
				timestampFormat.format(createdStudent.getNoteList().get(0).getTimestamp()).equals(note1TimestampText) &&
				createdStudent.getNoteList().get(0).getText().equals(note1Text) &&
				createdStudent.getNoteList().get(0).getVersion().compareTo(0l) == 0 &&

				createdStudent.getNoteList().get(1).getId().compareTo(0l) > 0 &&
				timestampFormat.format(createdStudent.getNoteList().get(1).getTimestamp()).equals(note2TimestampText) &&
				createdStudent.getNoteList().get(1).getText().equals(note2Text) &&
				createdStudent.getNoteList().get(1).getVersion().compareTo(0l) == 0
		);

		// Verify createdStudent2
		assert(
			createdStudent2.getId().compareTo(0l) > 0 &&
			createdStudent2.getFirstName().equals(firstName2) &&
			createdStudent2.getLastName().equals(lastName2) &&
			createdStudent2.getGrade().equals(grade2) &&
			createdStudent2.getVersion().compareTo(0l) == 0 //&&
			//createdStudent2.getNoteList().size() == 2
			);

		byte[] xmlBytes = studentNotesReportService.beanToXml(students);
		String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);
		
		LOGGER.info("xmlString: {}", xmlString);
/*
		assert(
			StringUtils.countMatches(xmlString, "<student>") == 1 && StringUtils.countMatches(xmlString, "</student>") == 1
			);
		assert(
			xmlString.contains("<id>"+createdStudent.getId()+"</id>") &&
			xmlString.contains("<firstName>"+createdStudent.getFirstName()+"</firstName>") &&
			xmlString.contains("<lastName>"+createdStudent.getLastName()+"</lastName>") &&
			xmlString.contains("<grade>"+createdStudent.getGrade()+"</grade>") &&
			xmlString.contains("<version>0</version>")
			);

		assert(
			StringUtils.countMatches(xmlString, "<noteList>") == 1 && StringUtils.countMatches(xmlString, "</noteList>") == 1 &&
			StringUtils.countMatches(xmlString, "<note>") == 2 && StringUtils.countMatches(xmlString, "</note>") == 2
			);
		assert(
			xmlString.contains("<id>"+createdStudent.getNoteList().get(0).getId()+"</id>") &&
			xmlString.contains("<timestamp>"+timestampFormat.format(createdStudent.getNoteList().get(0).getTimestamp())+"</timestamp>") &&
			xmlString.contains("<text>"+createdStudent.getNoteList().get(0).getText()+"</text>") &&
			xmlString.contains("<version>0</version>")
			);
		assert(
			xmlString.contains("<id>"+createdStudent.getNoteList().get(1).getId()+"</id>") &&
			xmlString.contains("<timestamp>"+timestampFormat.format(createdStudent.getNoteList().get(1).getTimestamp())+"</timestamp>") &&
			xmlString.contains("<text>"+createdStudent.getNoteList().get(1).getText()+"</text>") &&
			xmlString.contains("<version>0</version>")
			);
	*/
	}

	@Test
	public void testXmlToPdf() throws IOException {
		
		byte[] studentsXmlByteArray = IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("students.xml"));
		byte[] pdfByteArray = studentNotesReportService.xmlToPdf(studentsXmlByteArray);
		LOGGER.debug("pdfByteArray.length: {}", pdfByteArray.length);
		assert (pdfByteArray.length > 0);
	}
	
	@Test
	public void testGenerateReport() throws JAXBException, ParserConfigurationException, SAXException, IOException, TransformerException {
		
		Students students = new Students();
		students.setStudentList(studentRepository.getAllStudents());
		byte[] pdfByteArray = studentNotesReportService.generateReport(students);
		LOGGER.debug("pdfByteArray.length: {}", pdfByteArray.length);
		assert (pdfByteArray.length > 0);
	}
}
