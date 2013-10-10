package com.uvs.coffeejob;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView mUserName;
	private TextView mUserBio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mUserName = (TextView)findViewById(R.id.userNameText);
		mUserBio = (TextView)findViewById(R.id.userBioText);
		
		User me = setupDefaultUser();
		showUserInfo(me);
	}
	
	private User setupDefaultUser() {
		final String DEFAULT_NAME    = "Oleg";
		final String DEFAULT_SURNAME = "Ovcharenko";
		final String DEFAULT_BIO     = "Nothing interesting here. " +
							           "Just a human from Belogorsk";
		User user = new User();
		user.setName(DEFAULT_NAME);
		user.setSurname(DEFAULT_SURNAME);
		user.setBio(DEFAULT_BIO);
		return user;
	}

	private void showUserInfo(User user) {
		clearUserInfo();
		if (user != null) {
			mUserName.setText(user.getName() + " " + user.getSurname());
			mUserBio.setText(user.getBio());
		}
	}
	
	private void clearUserInfo() {
		mUserName.setText(null);
		mUserBio.setText(null);
	}
}
