package com.uvs.coffeejob;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class SplashFragment extends SherlockFragment {
    private UserManager mUserManager = UserManager.getInstance();
    private static RetrieveUserFriendsTask mTask;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        View view = inflater.inflate(R.layout.splash_fragment, container, false);
        setHasOptionsMenu(false);
        
        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mTask = new RetrieveUserFriendsTask();
        mTask.execute();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
    }
    
 
    private class RetrieveUserFriendsTask extends AsyncTask<Void, Void, List<User>> {        
        private InterruptListener taskExecutor;
        
        @Override
        protected void onPreExecute() {
            mUserManager.setInterrupted(false);
        }
        
        @Override
        protected List<User> doInBackground(Void...params) {
            final String TAG = "RetrieveUserTask.doInBackground()";
            
            Log.i(TAG, "Entering function");
            taskExecutor = mUserManager;
            return mUserManager.getUserFriends();
        }
        
        @Override
        protected void onPostExecute(List<User> friends) {
            getActivity().getSupportFragmentManager().popBackStack();
            
            if (friends != null) {
                Fragment fragment = new FriendsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, fragment)
                           .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                           .addToBackStack(null)
                           .commit();
            }
            else {
                Log.i("SplashScreen", "No connection");
                Toast.makeText(getActivity(), R.string.pleaseFindNetworkConnection, 
                               Toast.LENGTH_LONG)
                     .show();
            }
        }
        
        @Override
        protected void onCancelled() {
            final String TAG = "RetrieveUserTask.onCancelled()";
            
            Log.i(TAG, "Interrupting task");
            taskExecutor.interrupt();
        }
    }

}
