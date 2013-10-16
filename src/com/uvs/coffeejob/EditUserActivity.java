package com.uvs.coffeejob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EditUserActivity extends SherlockFragmentActivity {
    private EditText mNameEdit;
    private EditText mSurnameEdit;
    private EditText mBioEdit;
    private EditText mContactsEdit;
    private EditText mBirthDateEdit;
    private ListView mContactsList;
    private Button   mSelectDateButton;
    private Button   mSaveButton;
    private Button   mCancelButton;
    
    private UserManager mUserManager = UserManager.getInstance();
    
    private static final SimpleDateFormat DATE_FORMAT = 
            new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        
        mNameEdit         = (EditText)findViewById(R.id.name_edit);
        mSurnameEdit      = (EditText)findViewById(R.id.surname_edit);
        mBioEdit          = (EditText)findViewById(R.id.bio_edit);
        mContactsEdit     = (EditText)findViewById(R.id.contacts_edit);
        mBirthDateEdit    = (EditText)findViewById(R.id.birthDateEdit);
        mSelectDateButton = (Button)findViewById(R.id.selectDateButton);
        mSaveButton       = (Button)findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(mOnClickListener);
        mCancelButton     = (Button)findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(mOnClickListener);
        
        mUserManager.init(this);
        
        showUser();
    }
    
    private OnClickListener mOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_button:
                    updateUserInfo();
                    break;
            }
       }
    };
    
    private void showUser() {
        User user = mUserManager.getUser();
        
        mNameEdit.setText(user.getName());
        mSurnameEdit.setText(user.getSurname());
        mBioEdit.setText(user.getBio());
        mBirthDateEdit.setText(userBirthToStr(user));
        mContactsEdit.setText(userContactsToStr(user));
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
        List<UserContact> contacts = user.getContacts();
        if (contacts != null) {
            StringBuilder str_contacts = new StringBuilder();
            for (int i=0; i < contacts.size(); ++i) {
                UserContact contact = contacts.get(i);
                str_contacts.append(contact.getType() + ": " + 
                                    contact.getValue() + "\n");
            }
            str_contacts.deleteCharAt(str_contacts.length()-1);
            result = str_contacts.toString();
        }
        return result;
    }
    
    public void updateUserInfo() {
        final String TAG = "EditUserActivity.updateUserInfo()";
        
        // data validation
        if (mNameEdit.getText().length() <= 0) {
            Toast.makeText(this, "Please tell me your name", 
                           Toast.LENGTH_LONG)
                 .show();
        }
        else if (mSurnameEdit.getText().length() <= 0) {
            Toast.makeText(this, "I'm curious of your surname", 
                           Toast.LENGTH_LONG)
                 .show();
        }
        else if (mBioEdit.getText().length() <= 0) {
            Toast.makeText(this, "What's about you biography?", 
                           Toast.LENGTH_LONG)
                 .show();
        }
        else if (isValidBirth() && isValidContactsData()) {
            // all checks passed - data is valid
            User user = new User();
            user.setName(mNameEdit.getText().toString());
            user.setSurname(mSurnameEdit.getText().toString());
            user.setBio(mBioEdit.getText().toString());
            
            // set birth
            Log.i(TAG, "Parsing birth date\n\n");
            user.setBirthDate(parseBirthEditToCalendar());   
            
            // set contacts
            Log.i(TAG, "Parsing contacts view");
            Log.i(TAG, "Full text: " + mContactsEdit.getText().toString() + "\n\n");
            String[] lines = mContactsEdit.getText().toString().split("[\\r\\n]+");
            for (String line: lines) {
                Log.i(TAG, "Parsing line: " + line);
                String[] data = line.split(": ");
                Log.i(TAG, "type:" + data[0] + "\nvalue:" + data[1] + "\n\n");
                user.addContact(data[0], data[1]);
            }
            
            mUserManager.setUser(user);
        }
    }
    
    private boolean isValidBirth() {
        final int MIN_VALID_YEAR = 1920;
        
        boolean result = true;
        GregorianCalendar birth = parseBirthEditToCalendar();
        if (birth == null) {
            result = false; // should never happen
        }
        else if (birth.get(Calendar.YEAR) < MIN_VALID_YEAR) {
            Toast.makeText(this, "Don't try to trick me. You can't be so old!", 
                    Toast.LENGTH_LONG)
                 .show();
            result = false;
        }
        else if (birth.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            Toast.makeText(this, "Wow, you've got a Time machine? I bet you was born earlier!", 
                    Toast.LENGTH_LONG)
                 .show();
            result = false;
        }
        return result;
    }
    
    
    private GregorianCalendar parseBirthEditToCalendar() {
        final String TAG = "EditUserActivity.parseBirthEditToCalendar()";
        
        GregorianCalendar birth = null;
        try {
            Date parsed = DATE_FORMAT.parse(mBirthDateEdit.getText().toString());
            birth = new GregorianCalendar();
            birth.setTime(parsed);
        } catch (ParseException e) {
            Log.i(TAG, "Cannot parse date string");
        }
        return birth;
    }
    
    private boolean isValidContactsData() {
        final String TAG = "isValidContactsData()";
        boolean result = true;
        Log.i(TAG, "Parsing contacts view");
        Log.i(TAG, "Full text: " + mContactsEdit.getText().toString() + "\n\n");
        String[] lines = mContactsEdit.getText().toString().split("[\\r\\n]+");
        for (String line: lines) {
            Log.i(TAG, "Parsing line: " + line);
            String[] data = line.split(": ");
            Log.i(TAG, "Valid tokens: " + data.length);
            if (data.length != 2) {
                result = false;
                break;
            }
            else {
                Log.i(TAG, "type:" + data[0] + "\nvalue:" + data[1] + "\n\n");
                if (data[0].length() <= 0 || data[1].length() <= 0) {
                    result = false;
                    break;
                }
            }
        }
        if (result != true) {
            Toast.makeText(this, "Ooopse, something's wron with your contacts.", 
                    Toast.LENGTH_LONG)
                 .show(); 
        }
        return result;
    }
}