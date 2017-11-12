package com.kerneldc.education.studentNotesService.domain;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

import com.kerneldc.education.studentNotesService.bean.GradeEnum;
import com.kerneldc.education.studentNotesService.domain.converter.GradeConverter;

@Entity
@Table(name = "grade", uniqueConstraints=@UniqueConstraint(columnNames={"student_id", "school_year_id"}))
//@XmlAccessorType(XmlAccessType.FIELD)
public class Grade extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlTransient
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id", nullable=false)
	//@JsonView(View.GradeExtended.class)
	private Student student;

	@ManyToOne
	@JoinColumn(name = "school_year_id", nullable=false)
	//@JsonView(View.GradeExtended.class)
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
	
//	@Override
//    public boolean equals(final Object object) {
//
//        return EqualsBuilder.reflectionEquals(this, object, "id", "version"/*, "student", "schoolYear"*/);
//    }
//	@Override
//    public int hashCode() {
//
//        return HashCodeBuilder.reflectionHashCode(this, "id", "version"/*, "student", "schoolYear"*/);
//    }
}
