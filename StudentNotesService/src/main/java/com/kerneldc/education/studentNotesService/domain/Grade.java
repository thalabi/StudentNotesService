package com.kerneldc.education.studentNotesService.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;

@Entity
@Table(name = "grade", uniqueConstraints=@UniqueConstraint(columnNames={"student_id", "school_year_id"}))
public class Grade extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlTransient
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable=false)
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_year_id", nullable=false)
	private SchoolYear schoolYear;
	
	@Column(name = "grade")
	private GradeEnum gradeEnum;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public SchoolYear getSchoolYear() {
		return schoolYear;
	}
	public void setSchoolYear(SchoolYear schoolYear) {
		this.schoolYear = schoolYear;
	}
	public GradeEnum getGradeEnum() {
		return gradeEnum;
	}
	public void setGradeEnum(GradeEnum gradeEnum) {
		this.gradeEnum = gradeEnum;
	}
}
