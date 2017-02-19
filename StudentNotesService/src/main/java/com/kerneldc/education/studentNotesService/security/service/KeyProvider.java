package com.kerneldc.education.studentNotesService.security.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.springframework.stereotype.Component;
@Component
public class KeyProvider {

	private Key key;
	
	public KeyProvider() throws NoSuchAlgorithmException {
		key = KeyGenerator.getInstance("AES").generateKey();
	}
	
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
}
