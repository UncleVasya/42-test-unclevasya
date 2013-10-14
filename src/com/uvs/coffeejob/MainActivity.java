package com.uvs.coffeejob;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity {
	private UserManager mUserManager;
	private TextView    mUserName;
	private TextView    mUserBio;
	private TextView    mUserBirth;
	private TextView    mUserContacts;
	private ImageView   mUserPhoto;
	private Button      mCloseBtn;
	private TabHost     mTabs;
	
	private AlertDialog      mAlertDialog;
	private ProgressDialog   mProgressDialog;
	
	private static RetrieveUserTask mRetrieveUserTask;
	
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = 
        new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
             onSessionStateChange(session, state, exception);
        }
    };
    
    private static final List<String> mPermissions = Arrays.asList(
            "user_birthday",
            "user_about_me",
            "email"
    );
    
    private static final String ACTIVITY_STATE = "ActivityState";
    private enum ActivityState {
        CREATED,
        CONNECTION_ESTABLISHED, 
        FACEBOOK_SIGNED_IN,
        USER_RETRIEVED
    }
    private ActivityState mActivityState;
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    final String TAG = "MainActivity.OnCreate()";
	    logKeyHash();
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		uiHelper      = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        mTabs         = (TabHost)findViewById(R.id.Tabhost);
		mUserName     = (TextView) findViewById(R.id.userNameText);
		mUserBio      = (TextView) findViewById(R.id.userBioText);
		mUserBirth    = (TextView) findViewById(R.id.userBirthText);
		mUserContacts = (TextView) findViewById(R.id.userContactsText);
		mUserPhoto    = (ImageView)findViewById(R.id.userPhotoView);
		mUserManager  = UserManager.getInstance();
		mCloseBtn     = (Button)   findViewById(R.id.closeButton);
		mCloseBtn.setOnClickListener(onClickListener);
		setupTabs();
		
		Log.i(TAG, Session.getActiveSession().toString()); 
		if(savedInstanceState == null) {
		    // application just launched, start from beginning
		    mUserManager.init(this);
		    onActivityStateChange(ActivityState.CREATED);
		}
		else {
		    // activity recreated (maybe after rotation);
		    // restore previous state
		    String state_str = savedInstanceState.getString(ACTIVITY_STATE);
		    onActivityStateChange(ActivityState.valueOf(state_str));
		}
	}
	
	private void setupTabs() {
	    mTabs.setup();
        TabHost.TabSpec spec = mTabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("User info");
        mTabs.addTab(spec);
        spec = mTabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Empty tab");
        mTabs.addTab(spec);
        mTabs.setCurrentTab(0);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.closeButton:
                    Session session = Session.getActiveSession();
                    if (session != null) {
                        session.closeAndClearTokenInformation();
                    }
                    MainActivity.this.finish();
                    break;
            }
        }
    };
    
    // THIS IS MAIN APPLICATION WORKFLOW
    private void onActivityStateChange(ActivityState state) {
        final String TAG = "MainActivity.onActivityStateChange()";
        
        Log.i(TAG, "New state: " + String.valueOf(state) + ";  " + 
                   "Old state: " + String.valueOf(mActivityState));
        mActivityState = state;
        switch (mActivityState) {
        case CREATED:
            checkNetwork();
            break;
        case CONNECTION_ESTABLISHED:
            loginWithFacebook();
            break;
        case FACEBOOK_SIGNED_IN:
            clearUserInfo();
            retrieveUser();
            break;
        case USER_RETRIEVED:
            showUserInfo(mUserManager.getUser());
            break;
        }
    }
    
    private void checkNetwork() {
        final String TAG = "MainActivity.checkNetwork()";
        
        // check for network access
        if (NetworkManager.isOnline(this)) {
            Log.i(TAG, "Connection established");
            onActivityStateChange(ActivityState.CONNECTION_ESTABLISHED);
        }
        else {
            Log.i(TAG, "No connection");
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // try again
                        onActivityStateChange(ActivityState.CREATED);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // close application
                        finish();
                        break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                   .setMessage(R.string.pleaseFindNetworkConnection)
                   .setPositiveButton(R.string.tryAgain, dialogClickListener)
                   .setNegativeButton(R.string.close, dialogClickListener)
                   .setCancelable(false);
            mAlertDialog = builder.show();
        }
    }
    
    private void loginWithFacebook() {
        final String TAG = "MainActivity.loginWithFacebook()";
        
        // auth with Facebook
        Session session = Session.getActiveSession();
        if (session.isOpened() ||
            session.getState().equals(SessionState.CREATED_TOKEN_LOADED))
        {
            Log.i(TAG, "We have correct session. Use it.");
            onSessionStateChange(Session.getActiveSession(), 
                                 Session.getActiveSession().getState(), null);
        }
        else if (session.getState().equals(SessionState.CLOSED_LOGIN_FAILED)) {
            Log.i(TAG, "We have session with failed login.");
            onSessionStateChange(Session.getActiveSession(), 
                    Session.getActiveSession().getState(), null);
        }
        else {
            Log.i(TAG, "No correct session found. Create a new one.");
            session = new Session(this);
            OpenRequest openRequest = new OpenRequest(this);
            openRequest.setPermissions(mPermissions);
            openRequest.setCallback(callback);
            Session.setActiveSession(session);
            Session.getActiveSession().openForRead(openRequest);
        }
    }
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.i("onSessionStateChanged", session.toString());
        if (state.isOpened() && NetworkManager.isOnline(this)) {
            onActivityStateChange(ActivityState.FACEBOOK_SIGNED_IN);
        }
        else if (state.equals(SessionState.CLOSED_LOGIN_FAILED)){   
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // try login again
                        Session.openActiveSession(MainActivity.this, true, callback);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // close application
                        finish();
                        break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                   .setMessage(R.string.pleaseLoginWithFacebook)
                   .setPositiveButton(R.string.login, dialogClickListener)
                   .setNegativeButton(R.string.close, dialogClickListener);
            mAlertDialog = builder.show();
       }
    }
	
	private void retrieveUser() {
	    if (mUserManager.isUserCached()) {
            onActivityStateChange(ActivityState.USER_RETRIEVED);
        }
	    else {
	        if (mRetrieveUserTask != null) {
	            // we already have a task working 
	            // which means activity was recreated
	            // (after rotation or something)
	            mRetrieveUserTask.setNewActivity(this);
	            
	        }
	        else {
	            mRetrieveUserTask = new RetrieveUserTask();
	            mRetrieveUserTask.execute();
	        }
	    }
	}
	
	private class RetrieveUserTask extends AsyncTask<Void, Void, User> {	    
	    private InterruptListener taskExecutor;
	    private MainActivity activity = MainActivity.this;
	    private ProgressDialog progressDialog; 
	    private boolean finished = false;
        
	    protected void onPreExecute() {
	       final String TAG = "RetrieveUserTask.onPreExecute()";
           
	       Log.i(TAG, "Entering function");  
           setupProgressDialog();
        }
        
        protected User doInBackground(Void...params) {
            final String TAG = "RetrieveUserTask.doInBackground()";
            
            Log.i(TAG, "Entering function");
            taskExecutor = mUserManager;
            return mUserManager.getUser();
        }
        
        protected void onPostExecute(User user) {
            final String TAG = "RetrieveUserTask.onPostExecute()";
            
            Log.i(TAG, "Entering function");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            MainActivity.mRetrieveUserTask = null; // task completed its job
            activity.checkUserRetrieved();
        }
        
        protected void onCancelled() {
            final String TAG = "RetrieveUserTask.onCancelled()";
            
            Log.i(TAG, "Interrupting task");
            //taskExecutor.interrupt();
            if (activity != null) {
                activity.finish();
            }
        }
        
        private void setupProgressDialog() {
            final String TAG = "RetrieveUserTask.setupProgressDialog()";
            
            Log.i(TAG, "Entering function");
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(getString(R.string.gettingUser));
            progressDialog.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    RetrieveUserTask.this.cancel(false);
                }
            });
            progressDialog.show();
        }
        
        public void setNewActivity(MainActivity new_activity) {
            final String TAG = "RetrieveUserTask.setNewActivity()";
            
            Log.i(TAG, "Entering function");
            activity = new_activity;
            if (finished) {
                onPostExecute(null);
            }
            else {
                setupProgressDialog();
            }
        }
        
        public void detachActivity(){
            final String TAG = "RetrieveUserTask.detachActivity()";
            
            Log.i(TAG, "Entering function");
            activity = null;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }
        
        public boolean isFinished() {
            return finished;
        }
	}
	
	public void checkUserRetrieved() {
	    if (mUserManager.isUserCached()) {
            // we have successfully got a user info
	        onActivityStateChange(ActivityState.USER_RETRIEVED);
        }
        else {
            // we failed to get user info from DB or Facebook;
            // this possible only theoretically in cases:
            //      - connection gone during the request;
            //      - connection considered as opened but doesn't work really;
            //      - Facebook API changed or something alike;
            
            if (NetworkManager.isOnline(MainActivity.this)) {
                // this is NOT a connection problem;
                // ask user if he wants to try again or close the application
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            // try again
                            onActivityStateChange(ActivityState.FACEBOOK_SIGNED_IN);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            // close application
                            finish();
                            break;
                        }
                    }
                };  
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                       .setMessage(R.string.cannotGetUserInfo)
                       .setPositiveButton(R.string.tryAgain, dialogClickListener)
                       .setNegativeButton(R.string.close, dialogClickListener)
                       .setCancelable(false);
                mAlertDialog = builder.show();
            }
            else {
                // this IS a connection problem;
                // go back to connection checking state
                onActivityStateChange(ActivityState.CREATED);
            }
        }
	}
	
	private void showUserInfo(User user) {
	    // user name
	    String str = getString(R.string.noData);
	    if (user.getName() != null && user.getSurname() != null) {
	        str = user.getName() + " " + user.getSurname();
	    }
	    mUserName.setText(str);
	    
	    // user bio
	    str = getString(R.string.noData);
	    if (user.getBio() != null) {
	        str = user.getBio();
	    }
        mUserBio.setText(str);
        
        // user birth date
        str = userBirthToStr(user);
        if (str == null) {
            str = getString(R.string.noData);
        }
        mUserBirth.setText(str);
        
        // user photo
        mUserPhoto.setImageBitmap(user.getPhoto());
        
        // user contacts
        str = userContactsToStr(user);
        if (str == null) {
            str = getString(R.string.noData);
        }
        mUserContacts.setText(str);
	}
	
	private String userBirthToStr(User user) {
	    String result = null;
	    if (user.getBirthDate() != null) {
    		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    		result = format.format(user.getBirthDate().getTime());
	    }
	    return result;
	}
	
	private String userContactsToStr(User user) {
	    String result = null;
        if (user.getContacts() != null) {
            StringBuilder str_contacts = new StringBuilder();
        	List<UserContact> contacts = user.getContacts();
        	UserContact contact = contacts.get(0);
        	str_contacts.append(contact.getType() + ": " + contact.getValue());
            for (int i=1; i<user.getContacts().size(); ++i) {
            	contact = contacts.get(i);
            	str_contacts.append("\n" + contact.getType() + ": " + contact.getValue());
            }
            result = str_contacts.toString();
        }
        return result;
	}
	
	private void clearUserInfo() {
		mUserName.setText(null);
		mUserBio.setText(null);
		mUserBirth.setText(null);
		mUserPhoto.setImageBitmap(null);
		mUserContacts.setText(null);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        
        // free shown dialog, if any
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }
        mAlertDialog = null;
        // detach activity from asyncTask
        if (mRetrieveUserTask != null) {
            mRetrieveUserTask.detachActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
        outState.putString(ACTIVITY_STATE, mActivityState.toString());
    }
    
    private void logKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                "com.uvs.coffeejob", PackageManager.GET_SIGNATURES
            );
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {
            Log.i("KeyHash:", "NameNotFound");

        } catch (NoSuchAlgorithmException e) {
            Log.i("KeyHash:", "NoAlgo");
        }
    }
}