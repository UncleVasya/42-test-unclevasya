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
    private static final String USERS       = "Users";
    // columns
    private static final String NAME        = "name";
    private static final String SURNAME     = "surname";
    private static final String BIO         = "bio";
    private static final String BIRTHDATE   = "birthdate";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + USERS + "( " + 
                        BaseColumns._ID + " integer primary key autoincrement, " + 
                        NAME            + " string not null, " + 
                        SURNAME         + " string not null, " + 
                        BIO             + " string not null, " + 
                        BIRTHDATE       + " integer not null" + ");";
        db.execSQL(sql);
        Log.i("DBManager", "onCreate(): \n\n" + sql);
    }

    public void addUser(User user) {
        Log.i("DBManager", "addUser()");
        ContentValues values = new ContentValues();
        values.put(NAME,        user.getName());
        values.put(SURNAME,     user.getSurname());
        values.put(BIO,         user.getBio());
        values.put(BIRTHDATE,   user.getBirthDate().getTime().getTime());
        
        SQLiteDatabase db = getWritableDatabase();
        db.insert(USERS, null, values);
        db.close();
    }

    public List<User> getUsers() {
        Log.i("DBManager", "getUsers()");
        List<User> users = new ArrayList<User>();

        SQLiteDatabase db = getReadableDatabase();
        try {
            // load data from DB
            Cursor cursor = db.query(USERS, null, null, null, null, null,
                    null);
            // parse data
            while (cursor.moveToNext()) {
                User user = new User();
                user.setName(cursor.getString(1));
                user.setSurname(cursor.getString(2));
                user.setBio(cursor.getString(3));
                GregorianCalendar birthDate = new GregorianCalendar();
                birthDate.setTime(new Date(cursor.getLong(4)));
                user.setBirthDate(birthDate);
                users.add(user);
            }
            // close DB
            cursor.close();
        } catch (Exception e) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS);
            onCreate(db);
        }
        ;
        db.close();

        return users;
    }

    public void clearDB() {
        Log.i("DBManager", "clearDB()");
        String sql = "delete from " + USERS;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DBManager", "onUpgrade()  " + oldVersion + " --> " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + USERS);
        onCreate(db);
    }
}
