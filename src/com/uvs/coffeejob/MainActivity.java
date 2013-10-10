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
		User user = new User();
		return user;
	}

	private void showUserInfo(User user) {
		
	}
	
	private void clearUserInfo() {

	}
}
