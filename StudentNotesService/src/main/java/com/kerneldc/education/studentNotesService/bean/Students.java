package com.kerneldc.education.studentNotesService.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kerneldc.education.studentNotesService.domain.Student;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Students {

	@XmlElement(name="student")
	private List<Student> studentList = new ArrayList<>();

	public List<Student> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<Student> studentList) {
		this.studentList = studentList;
	}

	// utility
	public void setStudentList(Set<Student> studentList) {
		this.studentList = new ArrayList<>(studentList);
	}
	
}
