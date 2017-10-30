package com.kerneldc.education.studentNotesService.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kerneldc.education.studentNotesService.domain.Student;
import com.kerneldc.education.studentNotesService.dto.ui.StudentUiDto;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Students {

	@XmlElement(name="student")
	private List<StudentUiDto> studentList = new ArrayList<>();

	public List<StudentUiDto> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<StudentUiDto> studentList) {
		this.studentList = studentList;
	}

	// utility
	public void setStudentList(Set<StudentUiDto> studentList) {
		this.studentList = new ArrayList<>(studentList);
	}
	
}
