package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserContact;

@RunWith(RobolectricTestRunner.class)
public class UserTest {
	final String DEFAULT_NAME    = "Oleg";
	final String DEFAULT_SURNAME = "Ovcharenko";
	final String DEFAULT_BIO     = "Nothing interesting here. " + 
                                   "Just a man from Belogorsk.";
	final GregorianCalendar DEFAULT_BIRTH = new GregorianCalendar(1991, 00, 01);
    
	@Test
    public void test_UserSetters() {
        User user = new User();
        user.setName(DEFAULT_NAME);
        user.setSurname(DEFAULT_SURNAME);
        user.setBio(DEFAULT_BIO);
        user.setBirthDate(DEFAULT_BIRTH);
        assertEquals(DEFAULT_NAME, user.getName());
        assertEquals(DEFAULT_SURNAME, user.getSurname());
        assertEquals(DEFAULT_BIO, user.getBio());
        assertEquals(DEFAULT_BIRTH, user.getBirthDate());
    }
	
	@Test
    public void test_UserAddContact() {
		final String CONTACT_1_TYPE    = "ICQ";
		final String CONTACT_1_VALUE   = "1234567";
		
		final String CONTACT_2_TYPE    = "Jabber";
		final String CONTACT_2_VALUE   = "uvs.jabber.org";
		
        User user = new User();
        assertEquals(0, user.getContacts().size());
        
        user.addContact(CONTACT_1_TYPE, CONTACT_1_VALUE);
        assertEquals(1, user.getContacts().size());
        
        UserContact contact = new UserContact(CONTACT_2_TYPE, CONTACT_2_VALUE);
        user.addContact(contact);
        assertEquals(2, user.getContacts().size());
        
        
        assertEquals(new UserContact(CONTACT_1_TYPE, CONTACT_1_VALUE),  
                     user.getContacts().get(0));
        assertEquals(new UserContact(CONTACT_2_TYPE, CONTACT_2_VALUE),  
                user.getContacts().get(1));
    }
	
	@Test
    public void test_UserClearContacts() {
		final String CONTACT_1_TYPE    = "ICQ";
		final String CONTACT_1_VALUE   = "1234567";
		
		final String CONTACT_2_TYPE    = "Jabber";
		final String CONTACT_2_VALUE   = "uvs.jabber.org";
		
        User user = new User();
        
        user.addContact(CONTACT_1_TYPE, CONTACT_1_VALUE);
        user.addContact(CONTACT_2_TYPE, CONTACT_2_VALUE);
        assertEquals(2, user.getContacts().size());
        
        user.clearContacts();
        assertEquals(0, user.getContacts().size());
        
        user.addContact(CONTACT_1_TYPE, CONTACT_1_VALUE);
        assertEquals(1, user.getContacts().size());
        
        user.clearContacts();
        assertEquals(0, user.getContacts().size());
    }
}