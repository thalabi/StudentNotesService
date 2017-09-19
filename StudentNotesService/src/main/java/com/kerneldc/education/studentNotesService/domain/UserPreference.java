package com.kerneldc.education.studentNotesService.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.kerneldc.education.studentNotesService.domain.jsonView.View;

@Entity
@Table(name = "user_preference", uniqueConstraints=@UniqueConstraint(columnNames={"username"}))
public class UserPreference extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(View.Default.class)
	private Long id;
	@Column(name = "username")
	@JsonView(View.Default.class)
	private String username;
	
	@ManyToOne//(fetch=FetchType.EAGER)
	@JoinColumn(name = "school_year_id")
	//@PrimaryKeyJoinColumn
	//@Fetch(FetchMode.JOIN)
	@JsonView(View.Default.class)
	private SchoolYear schoolYear;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public SchoolYear getSchoolYear() {
		return schoolYear;
	}
	public void setSchoolYear(SchoolYear schoolYear) {
		this.schoolYear = schoolYear;
	}
	@Override
	public boolean equals(final Object object) {

        return EqualsBuilder.reflectionEquals(this, object, "id", "version");
    }
	@Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this, "id", "version");
    }
}
