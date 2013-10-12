package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.widget.TextView;

import com.uvs.coffeejob.MainActivity;
import com.uvs.coffeejob.R;
import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserContact;
import com.uvs.coffeejob.UserManager;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
	final String DEFAULT_NAME     = "Oleg";
	final String DEFAULT_SURNAME  = "Ovcharenko";
	final String DEFAULT_BIO      = "Nothing interesting here. " + 
                                    "Just a man from Belogorsk.";
	final String DEFAULT_BIRTH    = "January 01, 1991";
	final String DEFAULT_CONTACTS = "ICQ: 1234567" + "\n" +
                                    "Jabber: uvs.jabber.org" + "\n" +
                                    "Phone: 9379992";
	
	private UserManager mUserManager;
 
	private MainActivity mMainActivity;
	private TextView mUserName;
	private TextView mUserBio;
	private TextView mUserBirth;
	private TextView mUserContacts;
	
	@Before
	public void setUp() {
		mMainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
		mMainActivity.onCreate(null);
		mUserName = (TextView)mMainActivity.findViewById(R.id.userNameText);
		mUserBio = (TextView)mMainActivity.findViewById(R.id.userBioText);
		mUserBirth = (TextView)mMainActivity.findViewById(R.id.userBirthText);
		mUserContacts = (TextView)mMainActivity.findViewById(R.id.userContactsText);
		mUserManager = UserManager.getInstance();
	}
	
	@Test 
	public void test_UserShown() {
	    User user = mUserManager.getUser();
		assertEquals(user.getName() + " " + user.getSurname(), mUserName.getText().toString());
		assertEquals(user.getBio(), mUserBio.getText().toString());
		
		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
		assertEquals(format.format(user.getBirthDate().getTime()), mUserBirth.getText().toString());
		
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
		assertEquals(str_contacts.toString(), mUserContacts.getText().toString());
	}
}