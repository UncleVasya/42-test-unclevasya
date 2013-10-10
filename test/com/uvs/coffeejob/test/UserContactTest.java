package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.uvs.coffeejob.UserContact;

// TODO: one contact is enough here;
//       move rest of them to UserTest to test addContact() function
@RunWith(RobolectricTestRunner.class)
public class UserContactTest {
	final String CONTACT_1_TYPE    = "ICQ";
	final String CONTACT_1_VALUE   = "1234567";
	
	final String CONTACT_2_TYPE    = "Jabber";
	final String CONTACT_2_VALUE   = "uvs.jabber.org";
	
	final String CONTACT_3_TYPE    = "Phone";
	final String CONTACT_3_VALUE   = "9379992";
    
	@Test
    public void test_UserContactConstructor() {
        UserContact contact = new UserContact(CONTACT_2_TYPE, CONTACT_2_VALUE);
        assertEquals(CONTACT_2_TYPE, contact.getType());
        assertEquals(CONTACT_2_VALUE, contact.getValue());
    }
	
	@Test
    public void test_UserContactSetters() {
        UserContact contact = new UserContact();
        contact.setType(CONTACT_1_TYPE);
        contact.setValue(CONTACT_1_VALUE);
        assertEquals(CONTACT_1_TYPE, contact.getType());
        assertEquals(CONTACT_1_VALUE, contact.getValue());
    }
}

