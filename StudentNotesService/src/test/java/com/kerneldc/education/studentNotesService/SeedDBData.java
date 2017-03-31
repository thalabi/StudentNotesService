package com.kerneldc.education.studentNotesService;

import java.sql.Timestamp;
import java.util.Arrays;

import com.kerneldc.education.studentNotesService.bean.Grade;
import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;

public class SeedDBData {

	public static final Student s1 = new Student();
	public static final Student s2 = new Student();
	public static final Student s3 = new Student();
	static {
		
		s1.setId(1l);
		s1.setFirstName("kareem");
		s1.setLastName("halabi");
		s1.setGrade(Grade.SK);
		s1.setVersion(0l);
		//String expected = "{\"version\":0,\"id\":1,\"firstName\":\"kareem\",\"lastName\":\"halabi\",\"grade\":\"SK\",\"noteList\":
		//[{\"version\":0,\"id\":1,\"timestamp\":1481403630839,\"text\":\"note 1\"},
			//{\"version\":0,\"id\":2,\"timestamp\":1481403630841,\"text\":\"note 2\"},
			//{\"version\":0,\"id\":3,\"timestamp\":1481403630842,\"text\":\"note 3\"}]}";
		Note s1n1 = new Note();
		s1n1.setId(1l);
		s1n1.setTimestamp(new Timestamp(1481403630839l));
		s1n1.setText("note 1");
		s1n1.setVersion(0l);
		Note s1n2 = new Note();
		s1n2.setId(2l);
		s1n2.setTimestamp(new Timestamp(1481403630841l));
		s1n2.setText("note 2");
		s1n2.setVersion(0l);
		Note s1n3 = new Note();
		s1n3.setId(3l);
		s1n3.setTimestamp(new Timestamp(1481403630842l));
		s1n3.setText("note 3");
		s1n3.setVersion(0l);
		s1.setNoteList(Arrays.asList(s1n1,s1n2,s1n3));
		
		s2.setId(2l);
		s2.setFirstName("");
		s2.setLastName("halabi");
		s2.setGrade(Grade.FOUR);
		
		s3.setId(3l);
		s3.setFirstName("Mr Parent");
		s3.setLastName("");
		s3.setGrade(Grade.EMPTY);
	}

}
