package com.kerneldc.education.studentNotesService;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.kerneldc.education.studentNotesService.security.JacksonGrantedAuthorityTests;
import com.kerneldc.education.studentNotesService.security.JwtTokenTests;
import com.kerneldc.education.studentNotesService.security.SecurityResourceTests;

/*
@RunWith(Suite.class)

@Suite.SuiteClasses({
	JacksonGrantedAuthorityTests.class,
	JwtTokenTests.class,
	SecurityResourceTests.class,
	StudentNotesResourceTests.class,
	StudentRepositoryTests.class,
	StudentNotesReportServiceTests.class
})
*/
public class TestSuite {
	public TestSuite() {
		System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
	}
}
