package com.uvs.coffeejob;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME       = R.string.app_name + ".db";
    private static final int    DATABASE_VERSION    = 2;

    // ------------------- USERS table------------------------
    private static final String USERS             = "Users";
    // columns
    private static final String USERS_NAME        = "name";
    private static final String USERS_SURNAME     = "surname";
    private static final String USERS_BIO         = "bio";
    private static final String USERS_BIRTHDATE   = "birthdate";
    private static final String USERS_PHOTO       = "userphoto";
    private static final String USERS_FB_ID       = "fb_id";
    private static final String USERS_PRIORITY    = "priority";
    
    // ------------------- CONTACTS table------------------------
    private static final String CONTACTS          = "Contacts";
    //columns
    private static final String CONTACTS_TYPE     = "type";
    private static final String CONTACTS_VALUE    = "value";
    private static final String CONTACTS_USER     = "user";
    
    
    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create USERS table
        String sql = "CREATE TABLE " + USERS + "( " + 
                        BaseColumns._ID   + " integer primary key autoincrement, " + 
                        USERS_NAME        + " string, "  + 
                        USERS_SURNAME     + " string, "  + 
                        USERS_BIO         + " string, "  + 
                        USERS_BIRTHDATE   + " integer, " +
                        USERS_PHOTO       + " blob, "	 +
                        USERS_FB_ID       + " string, "   +
                        USERS_PRIORITY    + " integer " + ");";
        Log.i("DBManager", "onCreate(): \n\n" + sql);
        db.execSQL(sql);
     
        // create CONTACTS table
        sql = "CREATE TABLE " + CONTACTS + "( " + 
                        BaseColumns._ID   + " integer primary key autoincrement, " + 
                        CONTACTS_TYPE     + " string not null, "  + 
                        CONTACTS_VALUE    + " string not null, "  + 
                        CONTACTS_USER     + " integer not null, " + 
                            "FOREIGN KEY(" + CONTACTS_USER + ") " +
                            "REFERENCES "  + USERS + "(" + BaseColumns._ID + ")" + ")";
        Log.i("DBManager", "onCreate(): \n\n" + sql);
        db.execSQL(sql);
    }

    public void addUser(User user) {
        Log.i("DBManager", "addUser()");
        ContentValues values = new ContentValues();
        
        if (user.getName() != null) {
            values.put(USERS_NAME, user.getName());
        }
        if (user.getSurname() != null) {
            values.put(USERS_SURNAME, user.getSurname());
        }
        if (user.getBio() != null) {
            values.put(USERS_BIO, user.getBio());
        }
        if (user.getBirthDate() != null) {
            values.put(USERS_BIRTHDATE, user.getBirthDate().getTime().getTime());
        }
        if (user.getId() != null) {
            values.put(USERS_FB_ID, user.getId());
        }
        values.put(USERS_PRIORITY, user.getPriority());
        
        if (user.getPhoto() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            user.getPhoto().compress(CompressFormat.PNG, 0, stream);
            values.put(USERS_PHOTO, stream.toByteArray());
        }

        SQLiteDatabase db = getWritableDatabase();
        Long userID = db.insert(USERS, null, values);
        addContacts(user.getContacts(), userID);
        db.close();
    }
    
    // currently we store only id and priority for friends
    public void addFriend(User friend) {
        Log.i("DBManager", "addFriend()");
        ContentValues values = new ContentValues();
        
        if (friend.getName() != null) {
            values.put(USERS_NAME, friend.getName());
        }
        if (friend.getSurname() != null) {
            values.put(USERS_SURNAME, friend.getSurname());
        }
        if (friend.getId() != null) {
            values.put(USERS_FB_ID, friend.getId());
        }
        values.put(USERS_PRIORITY, friend.getPriority());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(USERS, null, values);
        db.close();
    }
    
    private void addContacts(List<UserContact> contacts, long userID) {
        Log.i("DBManager", "addContacts()");
        for (UserContact contact: contacts) {
            addContact(contact, userID);
        }
    }
    
    private void addContact(UserContact contact, long userID) {
        Log.i("DBManager", "addContact()");
        ContentValues values = new ContentValues();
        values.put(CONTACTS_TYPE,   contact.getType());
        values.put(CONTACTS_VALUE,  contact.getValue());
        values.put(CONTACTS_USER,   userID);
        
        SQLiteDatabase db = getWritableDatabase();
        db.insert(CONTACTS, null, values);
    }

    public List<User> getUsers() {
        Log.i("DBManager", "getUsers()");
        List<User> users = new ArrayList<User>();

        SQLiteDatabase db = getReadableDatabase();
        try {
            // load data from DB
            Cursor cursor = db.query(USERS, null, null, null, null, null, null);
            // parse data
            while (cursor.moveToNext()) {
                User user = new User();
                long userID = cursor.getLong(0);
                
                if (cursor.isNull(1) != true) { // name
                    user.setName(cursor.getString(1));
                }
                if (cursor.isNull(2) != true) { // surname
                    user.setSurname(cursor.getString(2));
                }
                if (cursor.isNull(3) != true) { // biography
                    user.setBio(cursor.getString(3));
                }
                if (cursor.isNull(4) != true) { // birth date
                    GregorianCalendar birthDate = new GregorianCalendar();
                    birthDate.setTime(new Date(cursor.getLong(4)));
                    user.setBirthDate(birthDate);
                }
                if (cursor.isNull(5) != true) { // photo
                    byte[] blob = cursor.getBlob(5);
                    Bitmap photo = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                    user.setPhoto(photo);
                }
                if (cursor.isNull(6) != true) { // FB id
                    user.setId(cursor.getString(6));
                }
                if (cursor.isNull(7) != true) { // priority
                    user.setPriority(cursor.getInt(7));
                }
                else {
                    user.setPriority(0);
                }
                List<UserContact> contacts = getUserContacts(userID);
                for (UserContact contact: contacts) { // contacts
                    user.addContact(contact);
                }
                
                users.add(user);
            }
            cursor.close();
        } catch (Exception e) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS);
            db.execSQL("DROP TABLE IF EXISTS " + CONTACTS);
            onCreate(db);
        }
        db.close();

        Log.i("DBManager", "users found: " + users.size());
        return users;
    }
    
    private List<UserContact> getUserContacts(long userID) {
        Log.i("DBManager", "getUserContacts()");
        SQLiteDatabase db = this.getReadableDatabase();
        List<UserContact> contacts = new ArrayList<UserContact>();
        try {
            Cursor cursor = db.query(CONTACTS, null, CONTACTS_USER + "=?",
                                     new String[] {String.valueOf(userID)}, 
                                     null, null, null, null);
            while (cursor.moveToNext()) {
                String type  = cursor.getString(1);
                String value = cursor.getString(2);
                contacts.add(new UserContact(type, value));
            }
            cursor.close();
        }
        catch (Exception e) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS);
            db.execSQL("DROP TABLE IF EXISTS " + CONTACTS);
            onCreate(db);
        }
        Log.i("DBManager", "contacts found: " + contacts.size());
        return contacts;
    }

    public void clearDB() {
        Log.i("DBManager", "clearDB()");
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM " + USERS;
        db.execSQL(sql);
        sql = "DELETE FROM " + CONTACTS;
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DBManager", "onUpgrade()  " + oldVersion + " --> " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + USERS);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS);
        onCreate(db);
    }
}