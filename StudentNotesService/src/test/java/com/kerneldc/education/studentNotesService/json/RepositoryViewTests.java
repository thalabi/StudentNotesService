package com.kerneldc.education.studentNotesService.json;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.StudentNotesApplication;
import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;
import com.kerneldc.education.studentNotesService.junit.MyTestExecutionListener;
import com.kerneldc.education.studentNotesService.repository.UserPreferenceRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudentNotesApplication.class)
@TestExecutionListeners(listeners = MyTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@Transactional
public class RepositoryViewTests {

	@Autowired
	private UserPreferenceRepository userPreferenceRepository;

	@Ignore
	@Test
	public void testSchoolYear_DefaulView() throws JsonProcessingException {
		
		SchoolYear sy = createSchoolYear();
		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	    
	    String result = mapper
	    	      .writerWithView(View.Default.class)
	    	      .writeValueAsString(sy);
	    System.out.println(result);
	}
	
	@Ignore
	@Test
	public void testSchoolYear_SchoolYearExtendedView() throws JsonProcessingException {
		
		SchoolYear sy = createSchoolYear();
		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	    
	    String result = mapper
	    	      .writerWithView(View.SchoolYearExtended.class)
	    	      .writeValueAsString(sy);
	    System.out.println(result);
	}
	
	@Test
	public void testUserPreference_DefaultView() throws JsonProcessingException {
		
		UserPreference up = createUserPreference();
		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	    
	    String result = mapper
	    	      .writerWithView(View.Default.class)
	    	      .writeValueAsString(up);
	    System.out.println(result);
	}
	
	@Test
	public void testUserPreference_SchoolYearExtendedView() throws JsonProcessingException {
		
		UserPreference up = createUserPreference();
		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	    
	    String result = mapper
	    	      .writerWithView(View.SchoolYearExtended.class)
	    	      .writeValueAsString(up);
	    System.out.println(result);
	}
	
	private SchoolYear createSchoolYear() {
		Student s = new Student();
		s.setId(1l);
		s.setFirstName("fn");
		s.setLastName("ln");
		Note n1 = new Note();
		n1.setId(2l);
		n1.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		s.setNoteSet(new HashSet<>(Arrays.asList(n1)));
		
		SchoolYear sy = new SchoolYear();
		sy.setId(3l);
		sy.setStartDate(new Date());
		sy.setEndDate(new Date());
		sy.setSchoolYear("2016-2017");
		sy.setStudentSet(new HashSet<>(Arrays.asList(s)));
		return sy;
	}
	
	private UserPreference createUserPreference() {
		UserPreference userPreference = userPreferenceRepository.findOne(1l);	
		return userPreference;
	}
}
