package com.kerneldc.education.studentNotesService.json;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.SchoolYear;
import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.domain.UserPreference;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;

public class ViewTests {

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
		
		UserPreference up = createUserPreference(createSchoolYear());
		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	    
	    String result = mapper
	    	      .writerWithView(View.Default.class)
	    	      .writeValueAsString(up);
	    System.out.println(result);
	}
	
	@Test
	public void testUserPreference_SchoolYearExtendedView() throws JsonProcessingException {
		
		UserPreference up = createUserPreference(createSchoolYear());
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
	
	private UserPreference createUserPreference(SchoolYear sy) {
		UserPreference up = new UserPreference();
		up.setId(4l);
		up.setSchoolYear(sy);
		up.setUsername("JohnDoe");;
		return up;
	}
}
