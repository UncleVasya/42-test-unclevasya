package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import android.app.Activity;
import android.os.AsyncTask;

import com.uvs.coffeejob.DBManager;
import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserManager;

@RunWith(RobolectricTestRunner.class)
public class UserManagerTest {	
	UserManager mUserManager;
	DBManager mDB;
	User mUser;
    
	@Before
	public void setUp() {
		mUserManager = UserManager.getInstance();
		mDB = new DBManager(new Activity());
		
		mUserManager.init(new Activity());
		mDB.clearDB();
		mUser = null;
	}
	
	@Test
    public void test_setUser() {       
        User user1 = TestData.getDefaultUser();
        mUserManager.setUser(user1);
        assertEquals(TestData.getDefaultUser(), user1); // no side effect
        
        User user2 = mUserManager.getUser();
        assertEquals(TestData.getDefaultUser(), user1); // no side effect
        
        assertEquals(user1, user2);
    }
	
	@Test
	public void test_interrupt() {
	    mUserManager.interrupt();
	    assertEquals(true, mUserManager.isInterrupted());
	}
	
	@Test
	public void test_getUser_normal() {
	    mDB.addUser(TestData.getDefaultUser());
	    assertEquals(TestData.getDefaultUser(), mUserManager.getUser());
	}
	
	@Test
	public void test_getUser_background() {
	    mDB.addUser(TestData.getDefaultUser());
	    
	    Robolectric.getBackgroundScheduler().pause();
        Robolectric.getUiThreadScheduler().pause();
        
	    GetUserTask getUserTask = new GetUserTask(); 
        getUserTask.execute();
  
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        
        assertEquals(TestData.getDefaultUser(), mUser);
	}
	
	private class GetUserTask extends AsyncTask<Void, Void, User> {

        protected User doInBackground(Void... params) {
            return mUserManager.getUser();
        }
        
        protected void onPostExecute(User user) {
            mUser = user;
        }
	}
}