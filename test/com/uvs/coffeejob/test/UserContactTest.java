package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import com.uvs.coffeejob.UserContact;

@RunWith(RobolectricTestRunner.class)
public class UserContactTest {
	@Test
    public void test_UserContactConstructor() {
        UserContact contact = new UserContact(
                TestData.DEFAULT_CONTACT_1_TYPE, 
                TestData.DEFAULT_CONTACT_1_VALUE
        );
       
        assertEquals(TestData.DEFAULT_CONTACT_1_TYPE,  contact.getType());
        assertEquals(TestData.DEFAULT_CONTACT_1_VALUE, contact.getValue());
    }
	
	@Test
    public void test_UserContactSetters() {
        UserContact contact = new UserContact();
        contact.setType(TestData.DEFAULT_CONTACT_1_TYPE);
        contact.setValue(TestData.DEFAULT_CONTACT_1_VALUE);
        
        assertEquals(TestData.DEFAULT_CONTACT_1_TYPE,  contact.getType());
        assertEquals(TestData.DEFAULT_CONTACT_1_VALUE, contact.getValue());
    }
}

