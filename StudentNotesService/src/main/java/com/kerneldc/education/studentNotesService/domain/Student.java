package com.kerneldc.education.studentNotesService.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.bean.Grade;
import com.kerneldc.education.studentNotesService.domain.converter.GradeConverter;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;

@Entity
@Table(name = "student", uniqueConstraints=@UniqueConstraint(columnNames={"first_name", "last_name"}))
@NamedEntityGraph(name = "Student.noteList", 
					attributeNodes = @NamedAttributeNode(value = "noteList"))
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
	@Column(name = "GRADE")
	@Convert(converter=GradeConverter.class)
	@JsonView(View.Default.class)
	private Grade grade;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("timestamp")
	@JoinTable(
		name = "student_note",
		joinColumns = @JoinColumn(name = "student_id"),
		inverseJoinColumns = @JoinColumn(name="note_id"))
	@XmlElementWrapper(name="notes")
	@XmlElement(name="note")
	@JsonView(View.Default.class)
	private List<Note> noteList = new ArrayList<>();

	@XmlTransient
	@ManyToMany(cascade=CascadeType.ALL, mappedBy="studentSet")
	//@JsonBackReference
	//@JsonIgnore
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
	public Grade getGrade() {
		return grade;
	}
	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public List<Note> getNoteList() {
		return noteList;
	}
	public void setNoteList(List<Note> noteList) {
		this.noteList = noteList;
	}
	
	public Set<SchoolYear> getSchoolYearSet() {
		return schoolYearSet;
	}
	public void setSchoolYearSet(Set<SchoolYear> schoolYearSet) {
		this.schoolYearSet = schoolYearSet;
	}
	
	@Override
    public boolean equals(final Object object) {

        return EqualsBuilder.reflectionEquals(this, object, "id", "version", "noteList", "schoolYearSet");
    }
	@Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this, "id", "version", "noteList", "schoolYearSet");
    }

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
