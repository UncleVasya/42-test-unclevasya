package com.uvs.coffeejob;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.graphics.Bitmap;

public class User {
	private String mName;
	private String mSurname;
	private String mBio;
	private Bitmap mPhoto;
	private GregorianCalendar mBirthDate;
	private String mId;
    private List<UserContact> mContacts;
	
    public void addContact(UserContact contact) {
        if (mContacts == null) {
            mContacts = new ArrayList<UserContact>();
        }
        mContacts.add(contact);
    }
    
    public void addContact(String type, String value) {
        if (mContacts == null) {
            mContacts = new ArrayList<UserContact>();
        }
    	mContacts.add(new UserContact(type, value));
    }
    
    public void clearContacts() {
    	mContacts.clear();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null) {    
            User user = (User) obj;
            result = ObjectUtils.bothNullOrEqual(this.getName(),
                                                 user.getName())        && 
                     ObjectUtils.bothNullOrEqual(this.getSurname(),
                                                 user.getSurname())     &&
                     ObjectUtils.bothNullOrEqual(this.getBio(),
                                                 user.getBio())         &&
                     ObjectUtils.bothNullOrEqual(this.getBirthDate(),
                                                 user.getBirthDate())   &&
                     ObjectUtils.bothNullOrEqual(this.getContacts(),
                                                 user.getContacts())    &&
                     ObjectUtils.bothNullOrEqual(this.getId(),
                                                 user.getId());
        }
            return result;
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
	
	public void setPhoto(Bitmap photo) {
	    mPhoto = photo;
	}
	
	public void setBirthDate(GregorianCalendar date) {
	    if (date != null) {
	        if (mBirthDate == null) {
	            mBirthDate = new GregorianCalendar();
	        }
	        mBirthDate.setTime(date.getTime());
	    }
	    else {
	        mBirthDate = null;
	    }
	}
	
	public void setId(String id) {
	    mId = id;
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
	
	public Bitmap getPhoto() {
	    return mPhoto;
	}
	
	public GregorianCalendar getBirthDate() {
		return mBirthDate;
	}
	
	public List<UserContact> getContacts() {
		return mContacts;
	}
	
	public String getId() {
	    return mId;
	}
}