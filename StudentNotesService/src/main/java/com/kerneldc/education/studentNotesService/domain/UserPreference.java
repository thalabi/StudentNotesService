package com.kerneldc.education.studentNotesService.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "user_preference", uniqueConstraints=@UniqueConstraint(columnNames={"username"}))
@NamedEntityGraph(name = "UserPreference.schoolYear", 
	attributeNodes = @NamedAttributeNode(value = "schoolYear"))
public class UserPreference extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "username")
	private String username;
	
	// Change the default fetch mode to LAZY
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_year_id")
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
}
