package com.kerneldc.education.studentNotesService.bean;

import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;

public enum Grades {
	JK("JK"), SK("SK"), ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), OTHER("OTHER"), EMPTY("");
	
	private final String grade;
	
	Grades (String grade) {
		this.grade = grade;
	}
	
	public String getValue() {
		return grade;
	}
	
	public static Grades fromValue(String value) {
		if (value == null) return null;
		switch (value) {
		case "JK":
			return Grades.JK;
		case "SK":
			return Grades.SK;
		case "1":
			return Grades.ONE;
		case "2":
			return Grades.TWO;
		case "3":
			return Grades.THREE;
		case "4":
			return Grades.FOUR;
		case "5":
			return Grades.FIVE;
		case "6":
			return Grades.SIX;
		case "7":
			return Grades.SEVEN;
		case "8":
			return Grades.EIGHT;
		case "Other":
			return Grades.OTHER;
		case "":
			return Grades.EMPTY;
		default:
			throw new SnsRuntimeException(String.format("Invalid value %s passed to fromValue() method", value));
		}
	}
}
