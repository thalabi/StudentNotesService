package com.kerneldc.education.studentNotesService;

import com.kerneldc.education.studentNotesService.bean.Grade;
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
