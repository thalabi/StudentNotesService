package com.kerneldc.education.studentNotesService;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;

import com.kerneldc.education.studentNotesService.domain.Note;
import com.kerneldc.education.studentNotesService.domain.Student;

public class SeedDBData {

	public static final Student s1 = new Student();
	public static final Student s2 = new Student();
	public static final Student s3 = new Student();
//	public static final Student s4 = new Student();
	static {
		
		s1.setId(1l);
		s1.setFirstName("kareem");
		s1.setLastName("halabi");
		//s1.setGrade(GradeEnum.SK);
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
		s1.setNoteSet(new HashSet<>(Arrays.asList(s1n1,s1n2,s1n3)));
		
		s2.setId(2l);
		s2.setFirstName("");
		s2.setLastName("halabi");
		//s2.setGrade(GradeEnum.FOUR);
		s2.setVersion(0l);
		
		s3.setId(3l);
		s3.setFirstName("Mr Parent");
		s3.setLastName("");
		//s3.setGrade(GradeEnum.EMPTY);
		s3.setVersion(0l);

		Note s3n1 = new Note();
		s3n1.setId(4l);
		s3n1.setTimestamp(new Timestamp(1481403630842l));
		s3n1.setText("note 4");
		s3n1.setVersion(0l);
		Note s3n2 = new Note();
		s3n2.setId(5l);
		s3n2.setTimestamp(new Timestamp(1481403630843l));
		s3n2.setText("note 5");
		s3n2.setVersion(0l);
		s3.setNoteSet(new HashSet<>(Arrays.asList(s3n1,s3n2)));
		
//		s4.setId(1l);
//		s4.setFirstName("kareem");
//		s4.setLastName("halabi");
//		s4.setGrade(GradeEnum.SK);
//		s4.setVersion(0l);
//		Note s4n1 = new Note();
//		s4n1.setId(1l);
//		s4n1.setTimestamp(new Timestamp(1481403630839l));
//		s4n1.setText("note 1");
//		s4n1.setVersion(0l);
//		Note s4n2 = new Note();
//		s4n2.setId(2l);
//		s4n2.setTimestamp(new Timestamp(1481403630841l));
//		s4n2.setText("note 2");
//		s4n2.setVersion(0l);
//		Note s4n3 = new Note();
//		s4n3.setId(3l);
//		s4n3.setTimestamp(new Timestamp(1481403630842l));
//		s4n3.setText("note 3");
//		s4n3.setVersion(0l);
//		s4.setNoteList(Arrays.asList(s4n1,s4n2,s4n3));
	
	}

}
