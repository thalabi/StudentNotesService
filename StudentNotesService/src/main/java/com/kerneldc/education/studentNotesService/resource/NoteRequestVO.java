package com.kerneldc.education.studentNotesService.resource;

import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;

public class NoteRequestVO extends AbstractBaseVO {

	private static final long serialVersionUID = 1L;

	public enum Operation {
		CREATE,UPDATE,DELETE
	}
	
	private Operation operation;
	private Long studentId;
	private Long studentVersion;
	private NoteUiDto noteUiDto;
	
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public Long getStudentId() {
		return studentId;
	}
	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	public Long getStudentVersion() {
		return studentVersion;
	}
	public void setStudentVersion(Long studentVersion) {
		this.studentVersion = studentVersion;
	}
	public NoteUiDto getNoteUiDto() {
		return noteUiDto;
	}
	public void setNoteUiDto(NoteUiDto noteUiDto) {
		this.noteUiDto = noteUiDto;
	}
}
