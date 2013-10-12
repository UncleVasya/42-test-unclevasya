package com.uvs.coffeejob;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;

public class UserManager {
	private User mUser;
	private Context mContext;
	private DBManager mDatabase;
	
	private static final UserManager instance = new UserManager();    
	private UserManager() {}
	
	public void init(Context context) {
		mContext = context;
		mDatabase = new DBManager(mContext);
		//LoadData();
	}
	
	public static UserManager getInstance() {
		return instance;
	}
	
	// sets a new user and inserts it into DB
	public void setUser(User user) {
		mUser = user;
		updateDB();
	}
	
	public User getUser() {
	    if (mUser == null) {
	        mUser = FacebookManager.getUser();
	    }
		return mUser;
	}
	
	public boolean isUserCached() {
	    return (mUser != null);
	}
	
	private void LoadData() {
		List<User> users = mDatabase.getUsers();
		if (users.isEmpty()) {
		    // DB doesn't exist or empty or broken;
		    // create a new DB with default user
		    setUser(getDefaultUser());
		}
		else {
		    mUser = users.get(0);
		}
	}
	
	public static User getDefaultUser() {
		final String DEFAULT_NAME    = "Oleg";
		final String DEFAULT_SURNAME = "Ovcharenko";
		final String DEFAULT_BIO     = "Nothing interesting here. " + 
				                       "Just a man from Belogorsk.";
		final GregorianCalendar DEFAULT_BIRTH = new GregorianCalendar(1991, 00, 01);
		final List<UserContact> DEFAULT_CONTACTS = new ArrayList<UserContact>(){{
			add(new UserContact("ICQ", "1234567"));
			add(new UserContact("Jabber", "uvs.jabber.org"));
			add(new UserContact("Phone", "9379992"));
		}};

		User user = new User();
		user.setName(DEFAULT_NAME);
		user.setSurname(DEFAULT_SURNAME);
		user.setBio(DEFAULT_BIO);
		user.setBirthDate(DEFAULT_BIRTH);
		for (UserContact contact: DEFAULT_CONTACTS) {
			user.addContact(contact);
		}
		return user;
	}
	
	public void updateDB(){
		mDatabase.clearDB();
		if (mUser != null) {
		    mDatabase.addUser(mUser);
		}
	}
}