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
    
	@Test
    public void test_UserSetters() {
        User user = new User();
        user.setName(TestData.DEFAULT_NAME);
        user.setSurname(TestData.DEFAULT_SURNAME);
        user.setBio(TestData.DEFAULT_BIO);
        user.setBirthDate(TestData.DEFAULT_BIRTH);
        user.setPhoto(TestData.DEFAULT_PHOTO);
        assertEquals(TestData.DEFAULT_NAME, user.getName());
        assertEquals(TestData.DEFAULT_SURNAME, user.getSurname());
        assertEquals(TestData.DEFAULT_BIO, user.getBio());
        assertEquals(TestData.DEFAULT_BIRTH, user.getBirthDate());
        assertEquals(TestData.DEFAULT_PHOTO, user.getPhoto());
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