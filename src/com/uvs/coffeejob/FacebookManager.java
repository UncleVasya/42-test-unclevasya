package com.uvs.coffeejob;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphUser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FacebookManager implements InterruptListener {
    private volatile boolean mInterrupted = false;
    
    public void interrupt() {
        mInterrupted = true;
    }
    
    public boolean isInterrupted() {
        return mInterrupted;
    }
    
    public static String userIdToURL(String id) {
        return "https://www.facebook.com/" + id;
    }
    
    // makes a request only if session is already open;
    // otherwise return null
    public User getUser() {
        final String TAG = "FacebookManager.getUser()";
        Log.i(TAG, "Entering function");
        
        User user = null;
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // check if task is interrupted before we do any heavy job
            if (isInterrupted()) {
                Log.i(TAG, "Task is interrupted");
                return null;
            }
            Log.i(TAG, "Session is opened. Making request.");
            Response response = Request.executeAndWait(new Request(session, "me"));
            Log.i(TAG, "Response: \n" + response.toString());
            
            GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
            if (graphUser != null) {
                // parse Facebook graph
                user = new User();
                user.setName(graphUser.getFirstName());
                user.setSurname(graphUser.getLastName());
                user.setBio((String) graphUser.getProperty("bio"));
                user.setId(graphUser.getId());
                
                if (isInterrupted()) {
                    Log.i(TAG, "Task is interrupted");
                    return null;
                }
               user.setPhoto(getUserPhoto(user));
               
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
        }
        
        if (isInterrupted()) {
            Log.i(TAG, "Task is interrupted");
            return null;
        }
        // TODO: override User.toString()
        Log.i(TAG, "Gotten user: \n" + String.valueOf(user));
        Log.i(TAG, "Leaving function");
        return user;
    }
    
    // makes a request only if session is already open;
    // otherwise return null
    public List<User> getUserFriends() {
        final String TAG = "FacebookManager.getUserFriends()";
        Log.i(TAG, "Entering function");
        
        List<User> friends = null;
        
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // check if task is interrupted before we do any heavy job
            if (isInterrupted()) {
                Log.i(TAG, "Task is interrupted");
                return null;
            }
            
            // make request
            Log.i(TAG, "Session is opened. Making request.");
            Response response = Request.executeAndWait(new Request(session, "me/friends"));
            Log.i(TAG, "Response: \n" + response.toString());
            
            // parse response
            GraphMultiResult result = response.getGraphObjectAs(GraphMultiResult.class);
            Log.i(TAG, "result: " + result);
            if (result == null) {
                return null;
            }
            List<GraphUser> graphFriends = result.getData().castToListOf(GraphUser.class);
            Log.i(TAG, "graphFriends count: " + graphFriends.size());
            
            UserManager userManager = UserManager.getInstance();
            
            // parse Facebook graph
            friends = new ArrayList<User>();
            for (GraphUser graphFriend: graphFriends) {
                User friend = new User();
                String[] full_name = graphFriend.getName().split(" ");
                friend.setName(full_name[0]);
                if (full_name.length > 1) { // we have surname too
                    friend.setSurname(full_name[full_name.length-1]);
                } 
                friend.setId(graphFriend.getId());
                // get priority from DB or set default one
                User fromDB = userManager.getFriendFromDB(friend.getId()) ;
                if (fromDB != null && fromDB.getPriority() != 0) {
                    friend.setPriority(fromDB.getPriority());
                }
                else {
                    friend.setPriority(User.DEFAULT_PRIORITY);
                }
                friends.add(friend);
            }
        }
        
        if (isInterrupted()) {
            Log.i(TAG, "Task is interrupted");
            return null;
        }
        
        //friends = getDebugFriends();

        int count = (friends == null? 0: friends.size());
        Log.i(TAG, "Friends found: " + count);
        Log.i(TAG, "Leaving function");
        return friends;
    }
    
    public List<User> getDebugFriends() {
        List<User> friends = new ArrayList<User>();
        for (int i=0; i < 10; ++i) {
            if (isInterrupted()) {
                Log.i("getDebugFriends()", "Task is interrupted");
                return null;
            }
            User friend = new User();
            friend.setName("Agent");
            friend.setSurname("Smith " + (i+1));
            //friend.setPhoto(UserManager.getInstance().getUser().getPhoto());
            friend.setId(String.valueOf(i+1));
            friend.setPriority(i%5+1);
            friends.add(friend);
        }
        return friends;
    }
    
    public Bitmap getUserPhoto(User user) {
        Bitmap photo = null;
        try {
            URL bitmapURL = new URL("https://graph.facebook.com/" + 
                                    user.getId() + "/picture?" +
                                    "width=" + 70 + "&height=" + 70
            );
            InputStream stream = bitmapURL.openConnection().getInputStream();
            photo = BitmapFactory.decodeStream(stream);
        } 
        catch (Exception e) {
            photo = null;
        }
        return photo;
    }
}