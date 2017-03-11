package com.kerneldc.education.studentNotesService.bean;

import java.sql.Timestamp;

public class TimestampRange {

	private Timestamp fromTimestamp;
	private Timestamp toTimestamp;
	
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
