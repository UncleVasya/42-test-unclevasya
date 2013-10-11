package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserContact;
import com.uvs.coffeejob.UserManager;

@RunWith(RobolectricTestRunner.class)
public class UserManagerTest {
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
	
	UserManager mUserManager;
    
	@Before
	public void setUp() {
		mUserManager = UserManager.getInstance();
	}
	
	@Test
    public void test_UserManagerSetters() {
        mUserManager.setUser(null);
        assertNull(mUserManager.getUser());
        
        User user1 = new User();
        user1.setName(DEFAULT_NAME);
        user1.setSurname(DEFAULT_SURNAME);
        user1.setBio(DEFAULT_BIO);
        user1.setBirthDate(DEFAULT_BIRTH);
        mUserManager.setUser(user1);
        assertNotNull(mUserManager.getUser());
        
        User user2 = mUserManager.getUser();
        assertEquals(DEFAULT_NAME, user2.getName());
        assertEquals(DEFAULT_SURNAME, user2.getSurname());
        assertEquals(DEFAULT_BIO, user2.getBio());
        assertEquals(DEFAULT_BIRTH, user2.getBirthDate());
    }
	
	@Test
    public void test_getDefaultUser() {
		User user = UserManager.getDefaultUser();
		assertEquals(DEFAULT_NAME, user.getName());
        assertEquals(DEFAULT_SURNAME, user.getSurname());
        assertEquals(DEFAULT_BIO, user.getBio());
        assertEquals(DEFAULT_BIRTH, user.getBirthDate());
        for (int i=0; i<user.getContacts().size(); ++i) {
        	assertEquals(DEFAULT_CONTACTS.get(i), user.getContacts().get(i));
        }
    }
}