package com.uvs.coffeejob;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME       = R.string.app_name + ".db";
    private static final int    DATABASE_VERSION    = 1;

    // ------------------- USERS table------------------------
    private static final String USERS             = "Users";
    // columns
    private static final String USERS_NAME        = "name";
    private static final String USERS_SURNAME     = "surname";
    private static final String USERS_BIO         = "bio";
    private static final String USERS_BIRTHDATE   = "birthdate";
    
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
        String sql = "create table " + USERS + "( " + 
                        BaseColumns._ID   + " integer primary key autoincrement, " + 
                        USERS_NAME        + " string, " + 
                        USERS_SURNAME     + " string, " + 
                        USERS_BIO         + " string, " + 
                        USERS_BIRTHDATE   + " integer" + ");";
        Log.i("DBManager", "onCreate(): \n\n" + sql);
        db.execSQL(sql);
     
        // create CONTACTS table
        sql = "create table " + CONTACTS + "( " + 
                        BaseColumns._ID   + " integer primary key autoincrement, " + 
                        CONTACTS_TYPE     + " string not null, "  + 
                        CONTACTS_VALUE    + " string not null, "  + 
                        CONTACTS_USER     + " integer not null, " + 
                            "foreign key(" + CONTACTS_USER + ") " +
                            "references "  + USERS + "(" + BaseColumns._ID + ")" + ")";
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
            //values.put(USERS_BIO, user.getBio());
        }
        if (user.getBirthDate() != null) {
            //values.put(USERS_BIRTHDATE, user.getBirthDate().getTime().getTime());
        }

        SQLiteDatabase db = getWritableDatabase();
        Long userID = db.insert(USERS, null, values);
        addContacts(user.getContacts(), userID);
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
                
                if (cursor.isNull(1) != true) {
                    user.setName(cursor.getString(1));
                }
                if (cursor.isNull(2) != true) {
                    user.setSurname(cursor.getString(2));
                }
                if (cursor.isNull(3) != true) {
                    user.setBio(cursor.getString(3));
                }
                if (cursor.isNull(4) != true) {
                    GregorianCalendar birthDate = new GregorianCalendar();
                    birthDate.setTime(new Date(cursor.getLong(4)));
                    user.setBirthDate(birthDate);
                } 
                List<UserContact> contacts = getUserContacts(userID);
                for (UserContact contact: contacts) {
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
