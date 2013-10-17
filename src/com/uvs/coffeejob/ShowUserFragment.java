package com.uvs.coffeejob;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Session;
import com.uvs.coffeejob.CalendarFragment.CalendarAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowUserFragment extends SherlockFragment{
    private TextView    mUserName;
    private TextView    mUserBio;
    private TextView    mUserBirth;
    private TextView    mUserContacts;
    private ImageView   mUserPhoto;
    private Button      mCloseBtn;
    
    private UserManager mUserManager = UserManager.getInstance(); 
    
    private UserManager mUserManaer = UserManager.getInstance();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        View view = inflater.inflate(R.layout.show_user_fragment, container, false);
        
        mUserName     = (TextView) view.findViewById(R.id.userNameText);
        mUserBio      = (TextView) view.findViewById(R.id.userBioText);
        mUserBirth    = (TextView) view.findViewById(R.id.userBirthText);
        mUserContacts = (TextView) view.findViewById(R.id.userContactsText);
        mUserPhoto    = (ImageView)view.findViewById(R.id.userPhotoView);
        mCloseBtn     = (Button)   view.findViewById(R.id.closeButton);
        mCloseBtn.setOnClickListener(onClickListener);
        
        clearUserInfo();
        showUserInfo(mUserManager.getUser());
        
        return view;
    }
    
    private OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.closeButton:
                    Session session = Session.getActiveSession();
                    if (session != null) {
                        session.closeAndClearTokenInformation();
                    }
                    ShowUserFragment.this.getActivity().finish();
                    break;
            }
        }
    };
    
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
        //if (user.getPhoto() != null) {
            mUserPhoto.setImageBitmap(user.getPhoto());
        //}
        
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

}