package com.kerneldc.education.studentNotesService.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kerneldc.education.studentNotesService.dto.StudentDto;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Students {

	@XmlElement(name="student")
	private List<StudentDto> studentList = new ArrayList<>();

	public List<StudentDto> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<StudentDto> studentList) {
		this.studentList = studentList;
	}

	// utility
	public void setStudentList(Set<StudentDto> studentList) {
		this.studentList = new ArrayList<>(studentList);
	}
	
}
