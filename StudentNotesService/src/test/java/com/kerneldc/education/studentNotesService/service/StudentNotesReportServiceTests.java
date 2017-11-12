package com.kerneldc.education.studentNotesService.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.bean.Students;
import com.kerneldc.education.studentNotesService.dto.ui.GradeUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;
import com.kerneldc.education.studentNotesService.exception.SnsException;

public class StudentNotesReportServiceTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static PdfStudentNotesReportService fixture;
	
	@BeforeClass
	public static void setup() {
		fixture = new PdfStudentNotesReportService();
	}
	
	@Test
    public void testBeanToXml() throws JAXBException {		
		
		// Create student # 1
		String firstName = "first name testBeanToXml";
		String lastName = "last name testBeanToXml";
		StudentUiDto student = new StudentUiDto();
		student.setFirstName(firstName);
		student.setLastName(lastName);
		GradeUiDto gradeUiDto = new GradeUiDto();
		gradeUiDto.setGradeEnum(GradeEnum.JK);
		student.setGradeUiDto(gradeUiDto);
		
		String note1TimestampText = "2017-01-29 16:11";
		String note1Text = "note 1 text testBeanToXml";
		String note2TimestampText = "2017-01-29 16:12";
		String note2Text = "note 2 text testBeanToXml";
		NoteUiDto note1 = new NoteUiDto();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime timestamp1 = LocalDateTime.parse(note1TimestampText, formatter);
		note1.setTimestamp(Timestamp.valueOf(timestamp1));
		note1.setText(note1Text);
		NoteUiDto note2 = new NoteUiDto();
		LocalDateTime timestamp2 = LocalDateTime.parse(note2TimestampText, formatter);
		note2.setTimestamp(Timestamp.valueOf(timestamp2));
		note2.setText(note2Text);

		student.setNoteUiDtoSet(new HashSet<>(Arrays.asList(note1, note2)));

		// Create student # 2
		String firstName2 = "first name 2 testBeanToXml";
		String lastName2 = "last name 2 testBeanToXml";
		GradeEnum grade2 = GradeEnum.FOUR;
		StudentUiDto student2 = new StudentUiDto();
		student2.setFirstName(firstName2);
		student2.setLastName(lastName2);
		//student2.setGradeEnum(grade2);
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
		
//		Student createdStudent = studentRepository.save(student);
//		Student createdStudent2 = studentRepository.save(student2);
		
		Students students = new Students();
		students.setStudentList(Arrays.asList(student, student2));

//		LOGGER.info("createdStudent: {}", createdStudent);
//		LOGGER.info("createdStudent2: {}", createdStudent2);
		
		// Verify createdStudent
//		assert(
//			createdStudent.getId().compareTo(0l) > 0 &&
//			createdStudent.getFirstName().equals(firstName) &&
//			createdStudent.getLastName().equals(lastName) &&
//			createdStudent.getGradeEnum().equals(grade) &&
//			createdStudent.getVersion().compareTo(0l) == 0 &&
//			createdStudent.getNoteList().size() == 2
//			);
//		
//		assert(
//				createdStudent.getNoteList().get(0).getId().compareTo(0l) > 0 &&
//				timestampFormat.format(createdStudent.getNoteList().get(0).getTimestamp()).equals(note1TimestampText) &&
//				createdStudent.getNoteList().get(0).getText().equals(note1Text) &&
//				createdStudent.getNoteList().get(0).getVersion().compareTo(0l) == 0 &&
//
//				createdStudent.getNoteList().get(1).getId().compareTo(0l) > 0 &&
//				timestampFormat.format(createdStudent.getNoteList().get(1).getTimestamp()).equals(note2TimestampText) &&
//				createdStudent.getNoteList().get(1).getText().equals(note2Text) &&
//				createdStudent.getNoteList().get(1).getVersion().compareTo(0l) == 0
//		);

		// Verify createdStudent2
//		assert(
//			createdStudent2.getId().compareTo(0l) > 0 &&
//			createdStudent2.getFirstName().equals(firstName2) &&
//			createdStudent2.getLastName().equals(lastName2) &&
//			createdStudent2.getGradeEnum().equals(grade2) &&
//			createdStudent2.getVersion().compareTo(0l) == 0 //&&
//			//createdStudent2.getNoteList().size() == 2
//			);

		byte[] xmlBytes = fixture.beanToXml(students);
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

/*	
	@Test
	public void testXmlToPdf() throws IOException {
		
		byte[] studentsXmlByteArray = IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("students.xml"));
		byte[] pdfByteArray = pdfStudentNotesReportService.xmlToPdf(studentsXmlByteArray);
		LOGGER.debug("pdfByteArray.length: {}", pdfByteArray.length);
		assert (pdfByteArray.length > 0);
	}
	
	@Test
	public void testGenerateReport() throws SnsException {
		
		Students students = new Students();
		students.setStudentList(studentRepository.getAllStudents());
		byte[] pdfByteArray = pdfStudentNotesReportService.generateReport(students);
		LOGGER.debug("pdfByteArray.length: {}", pdfByteArray.length);
		assert (pdfByteArray.length > 0);
	}
*/	
}