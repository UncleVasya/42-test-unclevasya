package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.uvs.coffeejob.User;

@RunWith(RobolectricTestRunner.class)
public class UserTest {
	final String DEFAULT_NAME    = "Oleg";
	final String DEFAULT_SURNAME = "Ovcharenko";
	final String DEFAULT_BIO     = "Nothing interesting here. " + 
                                   "Just a man from Belogorsk.";
    
	@Test
    public void test_UserSetters() {
        User user = new User();
        user.setName(DEFAULT_NAME);
        user.setSurname(DEFAULT_SURNAME);
        user.setBio(DEFAULT_BIO);
        assertEquals(DEFAULT_NAME, user.getName());
        assertEquals(DEFAULT_SURNAME, user.getSurname());
        assertEquals(DEFAULT_BIO, user.getBio());
    }
}

