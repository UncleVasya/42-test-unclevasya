package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import android.app.Activity;
import com.uvs.coffeejob.DBManager;
import com.uvs.coffeejob.User;

@RunWith(RobolectricTestRunner.class)
public class DBManagerTest {
    private DBManager mDatabase;
    
    @Before
    public void setUp() {
        mDatabase = new DBManager(new Activity());
    }
    @Test 
    public void test_ClearDB() {
        mDatabase.addUser(TestData.getDefaultUser());
        mDatabase.clearDB();
        assertEquals(0, mDatabase.getUsers().size());
    }
    
	@Test
	public void test_AddUser() {
	    mDatabase.clearDB();
	    for (int i=1; i<10; ++i) {
	        mDatabase.addUser(TestData.getDefaultUser());
	        assertEquals(i, mDatabase.getUsers().size());
	    }
	}
	
	@Test
	public void test_GetUsers() {
		User user1 = TestData.getDefaultUser();
		User user2 = TestData.getDefaultUser();
		user2.setName("Vasya");
		
		mDatabase.clearDB();
		mDatabase.addUser(user1);
		mDatabase.addUser(user2);
		List<User> fromDB = mDatabase.getUsers();
		
		assertEquals(2, fromDB.size());
		assertEquals(user1, fromDB.get(0));
		assertEquals(user2, fromDB.get(1));
	}
}
