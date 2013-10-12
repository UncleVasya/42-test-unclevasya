package com.uvs.coffeejob;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FacebookManager {
    // makes a request only if session is already open;
    // otherwise return null
    public static User getUser() {
        final String TAG = "FacebookManager.getUser()";
        Log.i(TAG, "Entering function");
        
        User user = null;
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            Log.i(TAG, "Session is opened. Making request.");
            Response response = Request.executeAndWait(new Request(session, "me"));
            Log.i(TAG, "Response: \n" + response.toString());
            GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
            
            // parse Facebook graph
            user = new User();
            user.setName(graphUser.getFirstName());
            user.setSurname(graphUser.getLastName());
            user.setBio((String) graphUser.getProperty("bio"));
            
            // photo
            Bitmap photo = null;
            try {
                URL bitmapURL = new URL("https://graph.facebook.com/" + 
                                        graphUser.getId() + "/picture?" +
                                        "width=" + 128 + "&height=" + 128
                );
                InputStream stream = bitmapURL.openConnection().getInputStream();
                photo = BitmapFactory.decodeStream(stream);
            } catch (Exception e) {
                photo = null;
            }
            user.setPhoto(photo);
           
            
            // birth date
            GregorianCalendar birth;
            try {
                SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy", Locale.US);
                birth = new GregorianCalendar();
                birth.setTime(format.parse(graphUser.getBirthday()));
            } catch (Exception e) { // no data or bad format
                birth = null;
            }
            user.setBirthDate(birth);
            
            // contacts
            String email = (String) graphUser.getProperty("email");
            if (email != null) {
                user.addContact("Email", email);
            }
            String link = (String)graphUser.getLink();
            if (link != null) {
                user.addContact("Facebook", 
                                link.substring(link.indexOf("facebook")));
            }
        }
        
        // TODO: override User.toString()
        Log.i(TAG, "Gotten user: \n" + String.valueOf(user));
        Log.i(TAG, "Leaving function");
        return user;
    }
    
}
