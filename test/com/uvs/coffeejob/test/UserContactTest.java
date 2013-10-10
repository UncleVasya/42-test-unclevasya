package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.uvs.coffeejob.UserContact;

@RunWith(RobolectricTestRunner.class)
public class UserContactTest {
	final String CONTACT_TYPE    = "ICQ";
	final String CONTACT_VALUE   = "1234567";
    
	@Test
    public void test_UserContactConstructor() {
        UserContact contact = new UserContact(CONTACT_TYPE, CONTACT_VALUE);
        assertEquals(CONTACT_TYPE, contact.getType());
        assertEquals(CONTACT_VALUE, contact.getValue());
    }
	
	@Test
    public void test_UserContactSetters() {
        UserContact contact = new UserContact();
        contact.setType(CONTACT_TYPE);
        contact.setValue(CONTACT_VALUE);
        assertEquals(CONTACT_TYPE, contact.getType());
        assertEquals(CONTACT_VALUE, contact.getValue());
    }
}

