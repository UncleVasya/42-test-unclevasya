package com.uvs.coffeejob;

import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class User {
	private String mName;
	private String mSurname;
	private String mBio;
	private GregorianCalendar mBirthDate;
    private List<UserContact> mContacts = new ArrayList<UserContact>();
	
    public void addContact(UserContact contact) {
    	
    }
    
    public void addContact(String type, String value) {
    	
    }
    
    public void clearContacts() {
    	
    }
	
	// setters
	public void setName(String name) {
		mName = name;
	}
	
	public void setSurname(String surname) {
		mSurname = surname;
	}
	
	public void setBio(String bio) {
		mBio = bio;
	}
	
	public void setBirthDate(GregorianCalendar date) {
		
	}
	
	
	// getters
	public String getName() {
		return mName;
	}
	
	public String getSurname() {
		return mSurname;
	}
	
	public String getBio() {
		return mBio;
	}
	
	public GregorianCalendar getBirthDate() {
		return mBirthDate;
	}
	
	public List<UserContact> getContacts() {
		return mContacts;
	}
}
