package com.uvs.coffeejob;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class UserManager {
	private User mUser;
	private Context mContext;
	private DBManager mDatabase;
	
	private static final UserManager instance = new UserManager();    
	private UserManager() {}
	
	public void init(Context context) {
		mContext = context;
		mDatabase = new DBManager(mContext);
	}
	
	public static UserManager getInstance() {
		return instance;
	}
	
	// sets a new user and inserts it into DB
	public void setUser(User user) {
		mUser = user;
		updateDB();
	}
	
	public User getUser() {
	    final String TAG = "UserManager.GetUser()"; 
	    if (mUser == null) {
	        // if we don't have user info in memory
	        // go look in DB
	        Log.i(TAG, "no user in memory; look in DB");
	        mUser = getUserFromDB();
	        if (mUser == null) {
	            // still don't have it?
	            // go look at Facebook
	            Log.i(TAG, "no user in DB; go to Facebook");
	            mUser = FacebookManager.getUser();
	            updateDB();
	        }
	    }
		return mUser;
	}
	
	public boolean isUserCached() {
	    return (mUser != null);
	}
	
	private User getUserFromDB() {
	    User user = null;
		List<User> usersFromDB = mDatabase.getUsers();
		if (usersFromDB.isEmpty() != true) {
		    user = usersFromDB.get(0);
		}
		return user;
	}
	
	public void updateDB(){
		mDatabase.clearDB();
		if (mUser != null) {
		    mDatabase.addUser(mUser);
		}
	}
}