package com.kerneldc.education.studentNotesService.domain;

//@Entity
//@Table(name = "STUDENT", uniqueConstraints=@UniqueConstraint(columnNames={"FIRST_NAME", "LAST_NAME"}))
//@NamedEntityGraph(name = "Student.noteList", 
//					attributeNodes = @NamedAttributeNode(value = "noteList"))
//@XmlAccessorType(XmlAccessType.FIELD)
public class User extends AbstractPersistableEntity {

//	@Id
//	@Column(name = "ID")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@XmlTransient
	private Long id;
//	@Column(name = "FIRST_NAME")
	private String username;
	private String password;
	private String firstName;
//	@Column(name = "LAST_NAME")
	private String lastName;
	

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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
