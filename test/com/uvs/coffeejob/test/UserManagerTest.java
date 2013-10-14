package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.app.Activity;

import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserManager;

@RunWith(RobolectricTestRunner.class)
public class UserManagerTest {	
	UserManager mUserManager;
    
	@Before
	public void setUp() {
		mUserManager = UserManager.getInstance();
		mUserManager.init(new Activity());
	}
	
	@Test
    public void test_setUser() {
        mUserManager.setUser(null);
        assertNull(mUserManager.getUser());
        
        User user1 = TestData.getDefaultUser();
        mUserManager.setUser(user1);
        assertEquals(TestData.getDefaultUser(), user1); // no side effect
        
        User user2 = mUserManager.getUser();
        assertEquals(TestData.getDefaultUser(), user1); // no side effect
        
        assertEquals(user1, user2);
    }
}