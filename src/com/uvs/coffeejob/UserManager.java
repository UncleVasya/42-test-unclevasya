package com.uvs.coffeejob;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class UserManager implements InterruptListener {
	private User mUser;
	private List<User> mUserFriends;
	private Context mContext;
	private DBManager mDatabase;
	private FacebookManager mFacebookManager;
	
	private volatile boolean mInterrupted = false;
	private List<InterruptListener> mTaskExecutors = new ArrayList<InterruptListener>();
	
	private static final UserManager instance = new UserManager();    
	private UserManager() {}
	
	public void init(Context context) {
		mContext = context;
		mDatabase = new DBManager(mContext);
		mFacebookManager = new FacebookManager();
		mInterrupted = false;
		mUser = null;
		mUserFriends = null;
	}
	
	public static UserManager getInstance() {
		return instance;
	}
	
	public void interrupt() {
	    mInterrupted = true;
	    for (InterruptListener taskExecutor: mTaskExecutors) {
	        taskExecutor.interrupt();
	        mTaskExecutors.remove(taskExecutor);
	    }
	}
	
	public void setInterrupted(boolean interrupted) {
        if (interrupted) {
            interrupt();
        }
        else {
            mInterrupted = false;
            mFacebookManager = new FacebookManager();
        }
    }
	
	public boolean isInterrupted() {
        return mInterrupted;
    }
	
	// sets a new user and inserts it into DB
	public void setUser(User user) {
		mUser = user;
		updateDB();
	}
	
	public void setFriends(List<User> friends) {
	    mUserFriends = friends;
	}
	
	public User getUser() {
	    final String TAG = "UserManager.GetUser()"; 
	    if (mUser == null) {
	        // if we don't have user info in memory
	        // go look in DB
	        Log.i(TAG, "no user in memory; look in DB");
	        
	        if (isInterrupted()) {
                Log.i(TAG, "Task is interrupted");
                return null;
            }
	        mUser = getUserFromDB();
	        
	        if (mUser == null) {
	            // still don't have it?
	            // go look at Facebook
	            Log.i(TAG, "no user in DB; go to Facebook");
	            
	            mTaskExecutors.add(mFacebookManager);
	            mUser = mFacebookManager.getUser();
	            if (mTaskExecutors.isEmpty() != true) {
	                mTaskExecutors.remove(mTaskExecutors.size()-1);
	            }
	            
	            updateDB();
	        }
	        if (isInterrupted()) {
	            Log.i(TAG, "Task is interrupted");
	            return null;
	        }
	    }
		return mUser;
	}
	
    public List<User> getUserFriends() {
        final String TAG = "UserManager.GetUserFriends()"; 
        if (mUserFriends == null) {
            Log.i(TAG, "No friends in memory; download from Facebook");
            mTaskExecutors.add(mFacebookManager);
            mUserFriends = mFacebookManager.getDebugFriends();
            if (mTaskExecutors.isEmpty() != true) {
                mTaskExecutors.remove(mTaskExecutors.size()-1);
            }
            if (isInterrupted()) {
                Log.i(TAG, "Task is interrupted");
                return null;
            }
        }
        return mUserFriends;
    }
	
	public boolean isUserCached() {
	    return (mUser != null);
	}
	
	public boolean isFriendsDataCashed() {
	    return (mUserFriends != null);
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