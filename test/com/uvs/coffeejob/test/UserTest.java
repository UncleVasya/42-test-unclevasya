package com.uvs.coffeejob.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
        assertThat(user.getName(), equalTo(DEFAULT_NAME));
        assertThat(user.getSurname(), equalTo(DEFAULT_SURNAME));
        assertThat(user.getBio(), equalTo(DEFAULT_BIO));
    }
}

