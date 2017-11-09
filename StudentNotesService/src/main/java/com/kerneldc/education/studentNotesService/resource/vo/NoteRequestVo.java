package com.kerneldc.education.studentNotesService.resource.vo;

import com.kerneldc.education.studentNotesService.dto.ui.NoteUiDto;

public class NoteRequestVo extends AbstractBaseVo {

	private static final long serialVersionUID = 1L;

	private Long studentId;
	private Long studentVersion;
	private NoteUiDto noteUiDto;
	
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
