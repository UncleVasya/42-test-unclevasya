package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.widget.TextView;

import com.uvs.coffeejob.MainActivity;
import com.uvs.coffeejob.R;
import com.uvs.coffeejob.User;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
	final String DEFAULT_NAME    = "Oleg";
	final String DEFAULT_SURNAME = "Ovcharenko";
	final String DEFAULT_BIO     = "Nothing interesting here. " + 
                                   "Just a man from Belogorsk.";
 
	private MainActivity mMainActivity;
	private TextView mUserName;
	private TextView mUserBio;
	
	@Before
	public void setUp() {
		mMainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
		mMainActivity.onCreate(null);
		mUserName = (TextView)mMainActivity.findViewById(R.id.userNameText);
		mUserBio = (TextView)mMainActivity.findViewById(R.id.userBioText);
	}
	
	@Test 
	public void test_UserShown() {
		assertEquals(DEFAULT_NAME + " " + DEFAULT_SURNAME, mUserName.getText().toString());
		assertEquals(DEFAULT_BIO, mUserBio.getText().toString());
	}
}

