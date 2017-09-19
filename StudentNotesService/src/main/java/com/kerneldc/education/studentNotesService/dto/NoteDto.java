package com.kerneldc.education.studentNotesService.dto;

import java.sql.Timestamp;

public class NoteDto extends AbstractBaseDto {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Timestamp timestamp;
	private String text;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
