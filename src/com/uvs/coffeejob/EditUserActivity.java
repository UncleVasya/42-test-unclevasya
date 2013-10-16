package com.uvs.coffeejob;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class EditUserActivity extends SherlockFragmentActivity {
    private EditText mNameEdit;
    private EditText mSurnameEdit;
    private EditText mBioEdit;
    private TextView mBirthDateText;
    private ListView mContactsList;
    private Button   mAddContactButton;
    private Button   mSelectDateButton;
    
    private UserManager mUserManager = UserManager.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        
        mNameEdit         = (EditText)findViewById(R.id.name_edit);
        mSurnameEdit      = (EditText)findViewById(R.id.surname_edit);
        mBioEdit          = (EditText)findViewById(R.id.bio_edit);
        mBirthDateText    = (TextView)findViewById(R.id.birthDateText);
        mContactsList     = (ListView)findViewById(R.id.contactsList);
        mAddContactButton = (Button)findViewById(R.id.addContactButton);
        mSelectDateButton = (Button)findViewById(R.id.selectDateButton);
        
        mUserManager.init(this);
        
        showUser();
    }
    
    private void showUser() {
        User user = mUserManager.getUser();
        
        mNameEdit.setText(user.getName());
        mSurnameEdit.setText(user.getSurname());
        mBioEdit.setText(user.getBio());
        mBirthDateText.setText(userBirthToStr(user));
        
        showContacts();
    }
    
    private String userBirthToStr(User user) {
        String result = null;
        if (user.getBirthDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            result = format.format(user.getBirthDate().getTime());
        }
        return result;
    }
    
    private void showContacts() {
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> item;
        
        List<UserContact> contacts = mUserManager.getUser().getContacts();
        for(int i=0; i < contacts.size(); ++i){
            UserContact contact = contacts.get(i);
            item = new HashMap<String,String>();
            item.put("type",   contact.getType());
            item.put("value", contact.getValue());
            list.add(item );
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.user_contact_list_item,
                new String[] {"type", "value"},
                new int[] {R.id.contactTypeEdit, R.id.contactValueEdit});
        
        mContactsList.setAdapter(adapter);
    }

}
