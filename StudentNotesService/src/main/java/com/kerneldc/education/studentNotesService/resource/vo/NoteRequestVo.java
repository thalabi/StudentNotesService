package com.kerneldc.education.studentNotesService.resource.vo;

import com.kerneldc.education.studentNotesService.dto.NoteDto;

public class NoteRequestVo extends AbstractBaseVo {

	private static final long serialVersionUID = 1L;

	private Long studentId;
	private Long studentVersion;
	private NoteDto noteDto;
	
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
	public NoteDto getNoteDto() {
		return noteDto;
	}
	public void setNoteDto(NoteDto noteDto) {
		this.noteDto = noteDto;
	}
}
