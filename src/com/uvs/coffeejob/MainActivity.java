package com.uvs.coffeejob;

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
	
	private ProgressDialog mProgress; 
	
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
    
    private enum ActivityState {
        CREATED,
        CONNECTION_ESTABLISHED, 
        FACEBOOK_SIGNED_IN, 
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		uiHelper      = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
		mUserName     = (TextView) findViewById(R.id.userNameText);
		mUserBio      = (TextView) findViewById(R.id.userBioText);
		mUserBirth    = (TextView) findViewById(R.id.userBirthText);
		mUserContacts = (TextView) findViewById(R.id.userContactsText);
		mUserPhoto    = (ImageView)findViewById(R.id.userPhotoView);
		mUserManager  = UserManager.getInstance();
		mCloseBtn     = (Button)   findViewById(R.id.closeButton);
		mCloseBtn.setOnClickListener(onClickListener);
		
		final String TAG = "MainActivity.OnCreate()";
		
		// setup tabs
		TabHost tabs = (TabHost)findViewById(R.id.Tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("User info");
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("Empty tab");
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
		
		Log.i(TAG, Session.getActiveSession().toString());
		if(savedInstanceState == null) {
		    // application just launched, start from beginning
		    mUserManager.init(this);
		    onActivityStateChange(ActivityState.CREATED);
		}
		else {
		    // activity recreated (maybe after rotation);
		    // see if we can skip few steps
		    if (mUserManager.isUserCached() || 
		        Session.getActiveSession().isOpened())
		    {
		        onActivityStateChange(ActivityState.FACEBOOK_SIGNED_IN);
		    }
		}
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
    
    private void onActivityStateChange(ActivityState state) {
        final String TAG = "MainActivity";
        switch (state) {
        case CREATED:
            // check for internet access
            if (NetworkManager.isOnline(this)) {
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

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.pleaseFindNetworkConnection)
                       .setPositiveButton(R.string.tryAgain, dialogClickListener)
                       .setNegativeButton(R.string.close, dialogClickListener)
                       .show();
            }
            break;
        case CONNECTION_ESTABLISHED:
            Log.i(TAG, "Connection established");
            // auth with Facebook
            Session session = Session.getActiveSession();
            if (session.isOpened() ||
                session.getState().equals(SessionState.CREATED_TOKEN_LOADED))
            {
                Log.i(TAG, "We have correct session. User it.");
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
            break;
        case FACEBOOK_SIGNED_IN:
            Log.i(TAG, "Signed in with Facebook");
            new ShowUserInfo().execute();
            break;
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

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.pleaseLoginWithFacebook)
                   .setPositiveButton(R.string.login, dialogClickListener)
                   .setNegativeButton(R.string.close, dialogClickListener)
                   .show();
       }
    }
	
	private class ShowUserInfo extends AsyncTask<Void, Void, User> {
        protected void onPreExecute() {
           if (mUserManager.isUserCached() != true) {
               // show progress dialog only if we're going to do
               // some time consuming job: operate with DB or Facebook;
               mProgress = new ProgressDialog(MainActivity.this);
               mProgress.setMessage(getString(R.string.gettingUser));
               mProgress.setOnCancelListener(new OnCancelListener() {
                   public void onCancel(DialogInterface dialog) {
                       cancel(false);
                   }
               });
               mProgress.show();
           }
        }
        
        protected User doInBackground(Void...params) {
            return mUserManager.getUser();
        }
        
        protected void onPostExecute(User user) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.cancel();
            }
            clearUserInfo();
            if (user != null) {
                // we have successfully got a user info
                showUserInfo(user);
            }
            else {
                // we couldn't get user info from DB or Facebook;
                // this possible only theoretically in cases:
                //      - connection gone during the request;
                //      - connection considered as opened but doesn't work really;
                //      - Facebook API changed;
                
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.cannotGetUserInfo)
                           .setPositiveButton(R.string.tryAgain, dialogClickListener)
                           .setNegativeButton(R.string.close, dialogClickListener)
                           .show();
                }
                else {
                    // this IS a connection problem;
                    // go back to previous state (before CONNECTION_ESTABLISHED)
                    onActivityStateChange(ActivityState.CREATED);
                }
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
        
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.cancel();
        }
        mProgress = null;
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
    }
}