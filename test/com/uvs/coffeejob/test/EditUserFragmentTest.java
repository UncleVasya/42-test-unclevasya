package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.uvs.coffeejob.EditUserFragment;
import com.uvs.coffeejob.R;
import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserContact;
import com.uvs.coffeejob.UserManager;

@RunWith(RobolectricTestRunner.class)
public class EditUserFragmentTest {
    private SherlockFragmentActivity mActivity;
    private SherlockFragment mFragment;
    
    private EditText mNameEdit;
    private EditText mSurnameEdit;
    private EditText mBioEdit;
    private EditText mBirthEdit;
    private EditText mContactsEdit;
    private Button mSaveButton;
    private Button mCancelButton;
    
    private UserManager mUserManager;
    private SimpleDateFormat mDateFormat = 
            new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    
    @Before
    public void SetUp() {
        mActivity = Robolectric.buildActivity(SherlockFragmentActivity.class)
                    .create().start().resume().get();
        
        mUserManager = UserManager.getInstance();
        mUserManager.init(mActivity);
        mUserManager.setUser(TestData.getDefaultUser());
        
        mFragment = new EditUserFragment();
        FragmentManager manager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(mFragment, null );
        transaction.commit();
        
        View v = mFragment.getView();
        mNameEdit       = (EditText)v.findViewById(R.id.name_edit);
        mSurnameEdit    = (EditText)v.findViewById(R.id.surname_edit);
        mBioEdit        = (EditText)v.findViewById(R.id.bio_edit);
        mBirthEdit      = (EditText)v.findViewById(R.id.birthDateEdit);
        mContactsEdit   = (EditText)v.findViewById(R.id.contacts_edit);
        mSaveButton     = (Button)v.findViewById(R.id.save_button);
        mCancelButton   = (Button)v.findViewById(R.id.cancel_button);
    }
    
    @Test
    public void test_SaveButtonValidData() {
        String NEW_NAME            = "Agent";
        String NEW_SURNAME         = "Smith";
        String NEW_BIO             = "Evil guy";
        String NEW_BIRTHDATE       = "April 27, 1977";
        String NEW_CONTACT_TYPE    = "email";
        String NEW_CONTACT_VALUE   = "smith@matrix.com";
        
        mNameEdit       .setText(NEW_NAME);
        mSurnameEdit    .setText(NEW_SURNAME);
        mBioEdit        .setText(NEW_BIO);
        mBirthEdit      .setText(NEW_BIRTHDATE);
        mContactsEdit   .setText(NEW_CONTACT_TYPE + ": " + NEW_CONTACT_VALUE); 
        
        mSaveButton.performClick();
        
        User user = mUserManager.getUser();
        assertEquals(NEW_NAME,      user.getName());
        assertEquals(NEW_SURNAME,   user.getSurname());
        assertEquals(NEW_BIO,       user.getBio());
        
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(mDateFormat.parse(NEW_BIRTHDATE));
            assertEquals(calendar, user.getBirthDate());
        } 
        catch (ParseException e) {}
        
        assertEquals(1, user.getContacts().size());
        UserContact contact = user.getContacts().get(0);
        assertEquals(NEW_CONTACT_TYPE,  contact.getType());
        assertEquals(NEW_CONTACT_VALUE, contact.getValue());
    }
    
    @Test
    public void test_SaveButtonInvalidData() {
        String NEW_NAME = "";
        
        mNameEdit.setText(NEW_NAME);
        mSaveButton.performClick();
        
        // data should not be changed
        assertEquals(TestData.getDefaultUser(), mUserManager.getUser());
    }
    
    @Test
    public void test_CancelButtonDiscardChanges() {
        String NEW_NAME = "Wall-e";
        
        mNameEdit.setText(NEW_NAME);
        mCancelButton.performClick();
        
        // data should not be changed
        assertEquals(TestData.getDefaultUser(), mUserManager.getUser());
    }
}