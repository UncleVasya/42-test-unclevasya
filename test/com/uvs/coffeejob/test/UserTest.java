package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        User user = new User();
        assertNull(user.getContacts());
        
        user.addContact(TestData.DEFAULT_CONTACT_1_TYPE,
                        TestData.DEFAULT_CONTACT_1_VALUE);
        assertEquals(1, user.getContacts().size());
        
        user.addContact(TestData.getDefaultContact_2());
        assertEquals(2, user.getContacts().size());
        
        assertEquals(TestData.getDefaultContact_1(), user.getContacts().get(0));
        assertEquals(TestData.getDefaultContact_2(), user.getContacts().get(1));
    }
	
	@Test
    public void test_UserClearContacts() {
        User user = new User();
        
        user.addContact(TestData.getDefaultContact_1());
        user.addContact(TestData.getDefaultContact_2());
        user.addContact(TestData.getDefaultContact_3());
        assertEquals(3, user.getContacts().size());
        
        user.clearContacts();
        assertEquals(0, user.getContacts().size());
    }
}