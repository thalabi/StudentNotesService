package com.kerneldc.education.studentNotesService.dto.ui;

import java.sql.Timestamp;

public class NoteUiDto extends AbstractBaseUiDto {

	private static final long serialVersionUID = 1L;

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
