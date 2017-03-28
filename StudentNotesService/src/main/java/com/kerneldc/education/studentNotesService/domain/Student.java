package com.kerneldc.education.studentNotesService.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "STUDENT", uniqueConstraints=@UniqueConstraint(columnNames={"FIRST_NAME", "LAST_NAME"}))
@NamedEntityGraph(name = "Student.noteList", 
					attributeNodes = @NamedAttributeNode(value = "noteList"))
@XmlAccessorType(XmlAccessType.FIELD)
public class Student extends AbstractPersistableEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlTransient
	private Long id;
	@Column(name = "FIRST_NAME")
	private String firstName = "";
	@Column(name = "LAST_NAME")
	private String lastName = "";
	@Column(name = "GRADE")
	private String grade;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("timestamp")
	@JoinTable(
		name = "STUDENT_NOTE",
		joinColumns = @JoinColumn(name = "STUDENT_ID"),
		inverseJoinColumns = @JoinColumn(name="NOTE_ID"))
	@XmlElementWrapper(name="notes")
	@XmlElement(name="note")
	private List<Note> noteList = new ArrayList<>();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName == null ? "" : firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName == null ? "" : lastName;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}

	public List<Note> getNoteList() {
		return noteList;
	}
	public void setNoteList(List<Note> noteList) {
		this.noteList = noteList;
	}
}
