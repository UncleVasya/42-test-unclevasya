package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.uvs.coffeejob.FacebookManager;
import com.uvs.coffeejob.FriendsFragment;
import com.uvs.coffeejob.R;
import com.uvs.coffeejob.UserManager;

@RunWith (RobolectricTestRunner.class)
public class FriendListTest {
    private SherlockFragmentActivity mActivity;
    private FriendsFragment mFragment;
    private FragmentManager mFrManager;
    private ListView mListView;
    private TextView mFriendsCount;
    
    private UserManager mUserManager = UserManager.getInstance();
    
    @Before
    public void SetUp() {        
        mActivity = Robolectric.buildActivity(SherlockFragmentActivity.class)
                    .create().start().resume().get();
        
        mUserManager.init(mActivity);
        mUserManager.setUser(TestData.getDefaultUser());
        mUserManager.setFriends(new FacebookManager().getDebugFriends());
        
        mFragment = new FriendsFragment();
        mFrManager = mActivity.getSupportFragmentManager();
        
        FragmentTransaction transaction = mFrManager.beginTransaction();
        transaction.add(mFragment, null );
        transaction.commit();
        
        mListView     = (ListView)mFragment.getView().findViewById(R.id.friendsList);
        mFriendsCount = (TextView)mFragment.getView().findViewById(R.id.friendsCountText);
    }
    
    @Test
    public void test_displayedFriendsCount() {
        int expected_count = mUserManager.getUserFriends().size();
        assertEquals(expected_count, 
                     Integer.parseInt(mFriendsCount.getText().toString()));
        assertEquals(expected_count, mListView.getCount());
    }
}