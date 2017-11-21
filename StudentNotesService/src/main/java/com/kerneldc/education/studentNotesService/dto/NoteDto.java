package com.kerneldc.education.studentNotesService.dto;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.kerneldc.education.studentNotesService.util.XmlTimestampAdapter;

public class NoteDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	@XmlJavaTypeAdapter(XmlTimestampAdapter.class)
	private Timestamp timestamp;
	private String text;
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
