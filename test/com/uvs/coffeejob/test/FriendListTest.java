package com.uvs.coffeejob.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.uvs.coffeejob.FacebookManager;
import com.uvs.coffeejob.FriendsFragment;
import com.uvs.coffeejob.FriendsFragment.FriendsAdapter;
import com.uvs.coffeejob.FriendsFragment.FriendsAdapter.ViewHolder;
import com.uvs.coffeejob.R;
import com.uvs.coffeejob.User;
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
    
    @Test
    public void test_friendListSorted() {
        Adapter adapter = mListView.getAdapter();
        for (int i=1; i < adapter.getCount(); ++i) {
            User current  = (User) adapter.getItem(i);
            User previous = (User) adapter.getItem(i-1);
            assertTrue(current.getPriority() <= previous.getPriority());
        }
    }
    
    @Test
    public void test_userChangesPriority() {
        FriendsAdapter adapter = (FriendsAdapter) mListView.getAdapter();
        
        User oldFirstFriend = (User) adapter.getItem(0);
        
        View v = adapter.getView(0, null, null);
        Spinner prioritySpinner = ((ViewHolder) v.getTag()).priority;
        adapter.mCurrentFriend = mUserManager.getUserFriends().get(0);
        prioritySpinner.getOnItemSelectedListener()
                       .onItemSelected(prioritySpinner, null, 3, 0);
        
        User newFirstFriend = (User) adapter.getItem(0);
        
        assertFalse(oldFirstFriend.getId().equals(newFirstFriend.getId()));
    }
}