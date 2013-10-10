package com.uvs.coffeejob;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView mUserName;
	private TextView mUserBio;
	private TextView mUserBirth;
	private TextView mUserContacts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mUserName = (TextView)findViewById(R.id.userNameText);
		mUserBio = (TextView)findViewById(R.id.userBioText);
		mUserBirth = (TextView)findViewById(R.id.userBirthText);
		mUserContacts = (TextView)findViewById(R.id.userContactsText);
		
		User me = setupDefaultUser();
		showUserInfo(me);
	}
	
	private User setupDefaultUser() {
		final String DEFAULT_NAME    = "Oleg";
		final String DEFAULT_SURNAME = "Ovcharenko";
		final String DEFAULT_BIO     = "Nothing interesting here. " + 
                                       "Just a man from Belogorsk.";
		final GregorianCalendar DEFAULT_BIRTH = new GregorianCalendar(1991, 00, 01);
		final String CONTACT_1_TYPE    = "ICQ";
		final String CONTACT_1_VALUE   = "1234567";
		final String CONTACT_2_TYPE    = "Jabber";
		final String CONTACT_2_VALUE   = "uvs.jabber.org";
		final String CONTACT_3_TYPE    = "Phone";
		final String CONTACT_3_VALUE   = "9379992";
		
		User user = new User();
		user.setName(DEFAULT_NAME);
		user.setSurname(DEFAULT_SURNAME);
		user.setBio(DEFAULT_BIO);
		user.setBirthDate(DEFAULT_BIRTH);
		user.addContact(CONTACT_1_TYPE, CONTACT_1_VALUE);
		user.addContact(CONTACT_2_TYPE, CONTACT_2_VALUE);
		user.addContact(CONTACT_3_TYPE, CONTACT_3_VALUE);
		return user;
	}

	private void showUserInfo(User user) {
		clearUserInfo();
		if (user != null) {
			mUserName.setText(user.getName() + " " + user.getSurname());
			mUserBio.setText(user.getBio());
			
			SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
			mUserBirth.setText(format.format(user.getBirthDate().getTime()));
			
            StringBuilder str_contacts = new StringBuilder();
            if (user.getContacts() != null) {
            	List<UserContact> contacts = user.getContacts();
            	UserContact contact = contacts.get(0);
            	str_contacts.append(contact.getType() + ": " + contact.getValue());
                for (int i=1; i<user.getContacts().size(); ++i) {
                	contact = contacts.get(i);
                	str_contacts.append("\n" + contact.getType() + ": " + contact.getValue());
                }
            } 
			mUserContacts.setText(str_contacts);
		}
	}
	
	private void clearUserInfo() {
		mUserName.setText(null);
		mUserBio.setText(null);
		mUserBirth.setText(null);
		mUserContacts.setText(null);
	}
}
