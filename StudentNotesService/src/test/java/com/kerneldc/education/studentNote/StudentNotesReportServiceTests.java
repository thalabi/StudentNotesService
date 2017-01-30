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
import org.apache.commons.lang3.StringUtils;
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
import com.kerneldc.education.studentNotes.domain.Note;
import com.kerneldc.education.studentNotes.domain.Student;
import com.kerneldc.education.studentNotes.repository.StudentRepository;
import com.kerneldc.education.studentNotes.service.StudentNotesReportService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class StudentNotesReportServiceTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private final static String XSL_FILE = "studentNotes.xsl";
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private StudentNotesReportService studentNotesReportService;

	@Test
    public void testBeanToXml() throws JAXBException {

		// Create one student
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

		Student createdStudent = studentRepository.save(student);

		LOGGER.info("createdStudent: {}", createdStudent);
		
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

		byte[] xmlBytes = studentNotesReportService.beanToXml(createdStudent);
		String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);
		
		LOGGER.info("xmlString: {}", xmlString);

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
	}

//	@Test
//    public void testXmlToXslFo() throws IOException, ParserConfigurationException, SAXException, TransformerException {
//		
//		byte[] xmlBytes = IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("studentBean.xml"));
//		LOGGER.info("xmlBytes.length: {}", xmlBytes.length);
//		byte[] foByteArray = studentNotesReportService.xmlToXslFo(xmlBytes, XSL_FILE);
//		LOGGER.info("foByteArray.length: {}", foByteArray.length);
//		String foString = new String(foByteArray, StandardCharsets.UTF_8);
//		LOGGER.info("foString: {}", foString);
//	}
	@Test
	public void testXmlToPdf() {
		
		studentNotesReportService.xmlToPdf();
	}
}
