package com.kerneldc.education.studentNotesService.bean;

import javax.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;

public enum GradeEnum {
	
	@XmlEnumValue("JK")
	JK("JK"),
	
	@XmlEnumValue("SK")
	SK("SK"),
	
	@XmlEnumValue("1")
	ONE("1"),
	
	@XmlEnumValue("2")
	TWO("2"),
	
	@XmlEnumValue("3")
	THREE("3"),
	
	@XmlEnumValue("4")
	FOUR("4"),
	
	@XmlEnumValue("5")
	FIVE("5"),
	
	@XmlEnumValue("6")
	SIX("6"),
	
	@XmlEnumValue("7")
	SEVEN("7"),
	
	@XmlEnumValue("8")
	EIGHT("8"),
	
	@XmlEnumValue("Other")
	OTHER("Other"),
	
	@XmlEnumValue("")
	EMPTY("");
	
	private final String grade;
	
	GradeEnum (String grade) {
		this.grade = grade;
	}
	
	@JsonValue
	public String getValue() {
		return grade;
	}
	
	public static GradeEnum fromValue(String value) {
		if (value == null) return null;
		switch (value) {
		case "JK":
			return GradeEnum.JK;
		case "SK":
			return GradeEnum.SK;
		case "1":
			return GradeEnum.ONE;
		case "2":
			return GradeEnum.TWO;
		case "3":
			return GradeEnum.THREE;
		case "4":
			return GradeEnum.FOUR;
		case "5":
			return GradeEnum.FIVE;
		case "6":
			return GradeEnum.SIX;
		case "7":
			return GradeEnum.SEVEN;
		case "8":
			return GradeEnum.EIGHT;
		case "Other":
			return GradeEnum.OTHER;
		case "":
			return GradeEnum.EMPTY;
		default:
			throw new SnsRuntimeException(String.format("Invalid value %s passed to fromValue() method", value));
		}
	}
}
