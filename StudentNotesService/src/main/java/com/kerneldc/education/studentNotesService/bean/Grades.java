package com.kerneldc.education.studentNotesService.bean;

public enum Grades {
	JK("JK"), SK("SK"), ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), OTHER("OTHER");
	
	private final String grade;
	
	Grades (String grade) {
		this.grade = grade;
	}
	
	public String getGrade() {
		return grade;
	}
	
	public String getValue() {
		return grade;
	}
}
