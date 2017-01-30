package com.kerneldc.education.studentNotes.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.kerneldc.education.studentNotes.util.XmlTimestampAdapter;

@Entity
@Table(name = "NOTE")
@XmlRootElement
public class Note extends AbstractPersistableEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@JsonProperty
	private Long id;
	@Column(name = "TIMESTAMP")
	private Timestamp timestamp;
	@Column(name = "TEXT")
	private String text;
	
//	@ManyToOne
//	@JoinTable(name = "STUDENT_NOTE",
//		joinColumns = @JoinColumn(name = "NOTE_ID"),
//		inverseJoinColumns = @JoinColumn( name="STUDENT_ID"))
//	private Student student;

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@XmlJavaTypeAdapter(XmlTimestampAdapter.class)
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
//	public Student getStudent() {
//		return student;
//	}
//	public void setStudent(Student student) {
//		this.student = student;
//	}
}
