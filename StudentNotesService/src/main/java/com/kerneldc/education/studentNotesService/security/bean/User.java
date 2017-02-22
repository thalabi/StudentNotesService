package com.kerneldc.education.studentNotesService.security.bean;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String[] permisions = new String[]{};
	//@JsonIgnore
	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	private Collection<GrantedAuthority> authorities = new ArrayList<>();
	private String token;
	
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
	public String[] getPermisions() {
		return permisions;
	}
	public void setPermisions(String[] permisions) {
		this.permisions = permisions;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
