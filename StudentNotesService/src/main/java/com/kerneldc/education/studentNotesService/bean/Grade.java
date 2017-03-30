package com.kerneldc.education.studentNotesService.bean;

import javax.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kerneldc.education.studentNotesService.exception.SnsRuntimeException;

public enum Grade {
	
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
	
	Grade (String grade) {
		this.grade = grade;
	}
	
	@JsonValue
	public String getValue() {
		return grade;
	}
	
	public static Grade fromValue(String value) {
		if (value == null) return null;
		switch (value) {
		case "JK":
			return Grade.JK;
		case "SK":
			return Grade.SK;
		case "1":
			return Grade.ONE;
		case "2":
			return Grade.TWO;
		case "3":
			return Grade.THREE;
		case "4":
			return Grade.FOUR;
		case "5":
			return Grade.FIVE;
		case "6":
			return Grade.SIX;
		case "7":
			return Grade.SEVEN;
		case "8":
			return Grade.EIGHT;
		case "Other":
			return Grade.OTHER;
		case "":
			return Grade.EMPTY;
		default:
			throw new SnsRuntimeException(String.format("Invalid value %s passed to fromValue() method", value));
		}
	}
}
