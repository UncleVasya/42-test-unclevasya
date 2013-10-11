package com.uvs.coffeejob;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class MainActivity extends Activity {
	private UserManager mUserManager;
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
		mUserManager = UserManager.getInstance();
		if(savedInstanceState == null) {
			mUserManager.init(this);
		}
		
		User me = UserManager.getDefaultUser();
		showUserInfo(me);
	}

	private void showUserInfo(User user) {
		clearUserInfo();
		if (user != null) {
			mUserName.setText(user.getName() + " " + user.getSurname());
			mUserBio.setText(user.getBio());
			mUserBirth.setText(userBirthToStr(user));
			mUserContacts.setText(userContactsToStr(user));
		}
	}
	
	private String userBirthToStr(User user) {
		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
		return format.format(user.getBirthDate().getTime());
	}
	
	private String userContactsToStr(User user) {
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
        return str_contacts.toString();
	}
	
	private void clearUserInfo() {
		mUserName.setText(null);
		mUserBio.setText(null);
		mUserBirth.setText(null);
		mUserContacts.setText(null);
	}
}