package com.kerneldc.education.studentNotesService.domain;

import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;

@Entity
@Table(name = "school_year", uniqueConstraints=@UniqueConstraint(columnNames={"school_year"}))
@NamedEntityGraph(name = "SchoolYear.studentSet", 
					attributeNodes = @NamedAttributeNode(value = "studentSet"))
//@JsonIdentityInfo(
//		  generator = ObjectIdGenerators.PropertyGenerator.class, 
//		  property = "id")
public class SchoolYear extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(View.Default.class)
	private Long id;
	@Column(name = "school_year")
	@JsonView(View.Default.class)
	private String schoolYear;
	@Column(name = "start_date")
	@JsonView(View.Default.class)
	private Date startDate;
	@Column(name = "end_date")
	@JsonView(View.Default.class)
	private Date endDate;

	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})  
    @JoinTable(
    	name="student_school_year",
    	joinColumns=@JoinColumn(name="school_year_id"),
    	inverseJoinColumns=@JoinColumn(name="student_id"))
	@OrderBy(value="firstName") //TODO add last name
	@JsonView(View.SchoolYearExtended.class)
	private Set<Student> studentSet = new HashSet<>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSchoolYear() {
		return schoolYear;
	}
	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Set<Student> getStudentSet() {
		return studentSet;
	}
	public void setStudentSet(Set<Student> studentSet) {
		this.studentSet = studentSet;
	}

	@Override
    public boolean equals(final Object object) {

        return EqualsBuilder.reflectionEquals(this, object, "id", "version", "studentSet");
    }
	@Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this, "id", "version", "studentSet");
    }

}
