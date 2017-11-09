package com.kerneldc.education.studentNotesService.resource.vo;

import java.sql.Timestamp;
import java.util.List;

public class PrintRequestVo extends AbstractBaseVo {

	private static final long serialVersionUID = 1L;

	private Long schoolYearId;
	private List<Long> studentIds;
	private Timestamp fromTimestamp;
	private Timestamp toTimestamp;
	
	public Long getSchoolYearId() {
		return schoolYearId;
	}
	public void setSchoolYearId(Long schoolYearId) {
		this.schoolYearId = schoolYearId;
	}
	public List<Long> getStudentIds() {
		return studentIds;
	}
	public void setStudentIds(List<Long> studentIds) {
		this.studentIds = studentIds;
	}
	public Timestamp getFromTimestamp() {
		return fromTimestamp;
	}
	public void setFromTimestamp(Timestamp fromTimestamp) {
		this.fromTimestamp = fromTimestamp;
	}
	public Timestamp getToTimestamp() {
		return toTimestamp;
	}
	public void setToTimestamp(Timestamp toTimestamp) {
		this.toTimestamp = toTimestamp;
	}

}
