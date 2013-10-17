package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.uvs.coffeejob.DBManager;
import com.uvs.coffeejob.EditUserActivity;
import com.uvs.coffeejob.R;
import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserContact;
import com.uvs.coffeejob.UserManager;

@RunWith(RobolectricTestRunner.class)
public class EditUserActivityTest {
    private EditUserActivity activity;
    private UserManager userManager = UserManager.getInstance();
    
    private EditText nameEdit;
    private EditText surnameEdit;
    private EditText bioEdit;
    private EditText birthDateText;
    private EditText contactsListEdit;
    private Button   selectDateButton;
    
    
    @Before
    public void setUp() {
        userManager.init(new Activity());
        userManager.setUser(TestData.getDefaultUser());
        activity = Robolectric.buildActivity(EditUserActivity.class).create().get();
        
        nameEdit         = (EditText)activity.findViewById(R.id.name_edit);
        surnameEdit      = (EditText)activity.findViewById(R.id.surname_edit);
        bioEdit          = (EditText)activity.findViewById(R.id.bio_edit);
        birthDateText    = (EditText)activity.findViewById(R.id.birthDateText);
        contactsListEdit = (EditText)activity.findViewById(R.id.contacts_edit);
        selectDateButton = (Button)activity.findViewById(R.id.select_date_button);
    }
    
    @Test
    public void test_ViewsPresent() {       
        assertNotNull(nameEdit);
        assertNotNull(surnameEdit);
        assertNotNull(bioEdit);
        assertNotNull(birthDateText);
        assertNotNull(contactsListEdit);
        assertNotNull(selectDateButton);
    }
    
    @Test
    public void test_userShownCorrectly() {
        User user = userManager.getUser();
        
        assertEquals(user.getName(),    String.valueOf(nameEdit.getText()));
        assertEquals(user.getSurname(), String.valueOf(surnameEdit.getText()));
        assertEquals(user.getBio(),     String.valueOf(bioEdit.getText()));
        // birth
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        String user_birth = format.format(user.getBirthDate().getTime());
        assertEquals(user_birth, String.valueOf(birthDateText.getText()));
        // contacts
        //assertEquals(user.getContacts().size(), contactsListEdit.getCount());
        
        
    }

}
