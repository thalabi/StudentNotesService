package com.kerneldc.education.studentNotesService.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TimestampRange {

	private int fromYear;
	private int fromMonth;
	private int fromDay;
	private int toYear;
	private int toMonth;
	private int toDay;
	
	public int getFromYear() {
		return fromYear;
	}
	public void setFromYear(int fromYear) {
		this.fromYear = fromYear;
	}
	public int getFromMonth() {
		return fromMonth;
	}
	public void setFromMonth(int fromMonth) {
		this.fromMonth = fromMonth;
	}
	public int getFromDay() {
		return fromDay;
	}
	public void setFromDay(int fromDay) {
		this.fromDay = fromDay;
	}
	public int getToYear() {
		return toYear;
	}
	public void setToYear(int toYear) {
		this.toYear = toYear;
	}
	public int getToMonth() {
		return toMonth;
	}
	public void setToMonth(int toMonth) {
		this.toMonth = toMonth;
	}
	public int getToDay() {
		return toDay;
	}
	public void setToDay(int toDay) {
		this.toDay = toDay;
	}

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
