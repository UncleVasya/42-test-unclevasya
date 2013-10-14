package com.uvs.coffeejob.test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.uvs.coffeejob.User;
import com.uvs.coffeejob.UserContact;

public class TestData {
    // DEFAULT USER
    public static final String DEFAULT_NAME    = "Oleg";
    public static final String DEFAULT_SURNAME = "Ovcharenko";
    public static final String DEFAULT_BIO     = "Nothing interesting here. " + 
                                                 "Just a man from Belogorsk.";
    public static final GregorianCalendar DEFAULT_BIRTH = 
            new GregorianCalendar               (1991, 00, 01);
    public static final List<UserContact> DEFAULT_CONTACTS = 
            new ArrayList<UserContact>(){{
                add(                            getDefaultContact_1());
                add(                            getDefaultContact_2());
                add(                            getDefaultContact_3());
    }};
    
    // DEFAULT USER CONTACTS
    public static final String DEFAULT_CONTACT_1_TYPE    = "ICQ";
    public static final String DEFAULT_CONTACT_1_VALUE   = "1234567";
    
    public static final String DEFAULT_CONTACT_2_TYPE    = "Jabber";
    public static final String DEFAULT_CONTACT_2_VALUE   = "uvs.jabber.org";
    
    public static final String DEFAULT_CONTACT_3_TYPE    = "Phone";
    public static final String DEFAULT_CONTACT_3_VALUE   = "9379992";
 
    
    public static User getDefaultUser() {
        User user = new User();
        user.setName        (DEFAULT_NAME);
        user.setSurname     (DEFAULT_SURNAME);
        user.setBio         (DEFAULT_BIO);
        user.setBirthDate   (DEFAULT_BIRTH);
        
        for (UserContact contact: DEFAULT_CONTACTS) {
            user.addContact(contact);
        }
        return user;
    }
    
    public static UserContact getDefaultContact_1() {
        return new UserContact(DEFAULT_CONTACT_1_TYPE, DEFAULT_CONTACT_1_VALUE);
    }
    
    public static UserContact getDefaultContact_2() {
        return new UserContact(DEFAULT_CONTACT_2_TYPE, DEFAULT_CONTACT_2_VALUE);
    }
    
    public static UserContact getDefaultContact_3() {
        return new UserContact(DEFAULT_CONTACT_3_TYPE, DEFAULT_CONTACT_3_VALUE);
    }
}