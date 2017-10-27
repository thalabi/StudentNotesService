package com.kerneldc.education.studentNotesService.domain;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;

@Entity
@Table(name = "student", uniqueConstraints=@UniqueConstraint(columnNames={"first_name", "last_name"}))
@NamedEntityGraphs({
	@NamedEntityGraph(name = "Student.noteSet", attributeNodes = @NamedAttributeNode(value = "noteSet")),
	@NamedEntityGraph(name = "Student.gradeSet", attributeNodes = @NamedAttributeNode(value = "gradeSet")),
	@NamedEntityGraph(name = "Student.noteSetAndGradeSet", attributeNodes = {@NamedAttributeNode(value = "noteSet"), @NamedAttributeNode(value = "gradeSet")})
})
@XmlAccessorType(XmlAccessType.FIELD)
//@JsonIdentityInfo(
//		  generator = ObjectIdGenerators.PropertyGenerator.class, 
//		  property = "id")
public class Student extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlTransient
	@JsonView(View.Default.class)
	private Long id;
	@Column(name = "first_name")
	@JsonView(View.Default.class)
	private String firstName = "";
	@Column(name = "last_name")
	@JsonView(View.Default.class)
	private String lastName = "";
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	//@OrderBy("timestamp")
	@JoinTable(
		name = "student_grade",
		joinColumns = @JoinColumn(name = "student_id"),
		inverseJoinColumns = @JoinColumn(name="grade_id"))
	//@XmlElementWrapper(name="notes")
	//@XmlElement(name="note")
	@JsonView(View.Default.class)
	private Set<Grade> gradeSet = new LinkedHashSet<>();

	@OneToMany(cascade = CascadeType.ALL/*, orphanRemoval = true*/)
	@OrderBy("timestamp")
	@JoinTable(
		name = "student_note",
		joinColumns = @JoinColumn(name = "student_id"),
		inverseJoinColumns = @JoinColumn(name="note_id"))
	@XmlElementWrapper(name="notes")
	@XmlElement(name="note")
	@JsonView(View.Default.class)
	private Set<Note> noteSet = new LinkedHashSet<>();

	@XmlTransient
	@ManyToMany(/*cascade=CascadeType.ALL, */mappedBy="studentSet")
	@JsonView(View.StudentExtended.class)
	private Set<SchoolYear> schoolYearSet = new HashSet<>();
    
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
//	public GradeEnum getGrade() {
//		return grade;
//	}
//	public void setGrade(GradeEnum grade) {
//		this.grade = grade;
//	}
	public Set<Grade> getGradeSet() {
		return gradeSet;
	}
	public void setGradeSet(Set<Grade> gradeSet) {
		this.gradeSet = gradeSet;
	}
	public Set<Note> getNoteSet() {
		return noteSet;
	}
	public void setNoteSet(Set<Note> noteSet) {
		this.noteSet = noteSet;
	}
	public Set<SchoolYear> getSchoolYearSet() {
		return schoolYearSet;
	}
	public void setSchoolYearSet(Set<SchoolYear> schoolYearSet) {
		this.schoolYearSet = schoolYearSet;
	}
	
//	@Override
//    public boolean equals(final Object object) {
//
//        return EqualsBuilder.reflectionEquals(this, object, "id", "version", "noteSet", "schoolYearSet", "gradeSet");
//    }
//	@Override
//    public int hashCode() {
//
//        return HashCodeBuilder.reflectionHashCode(this, "id", "version", "noteSet", "schoolYearSet", "gradeSet");
//    }

	// utility methods
	
	public void addSchoolYear(SchoolYear schoolYear) {
		schoolYearSet.add(schoolYear);
		schoolYear.getStudentSet().add(this);
	}

	public void removeSchoolYear(SchoolYear schoolYear) {
		schoolYearSet.remove(schoolYear);
		schoolYear.getStudentSet().remove(this);
	}
}
