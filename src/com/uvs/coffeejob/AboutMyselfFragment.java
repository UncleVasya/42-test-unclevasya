package com.uvs.coffeejob;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class AboutMyselfFragment extends SherlockFragment {
    private Button mCloseButton;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        View view = inflater.inflate(R.layout.about_myself_fragment, container, false);
        setHasOptionsMenu(false);
        
        mCloseButton = (Button)view.findViewById(R.id.closeButton);
        mCloseButton.setOnClickListener(mOnClickListener);
        
        return view;
    }
    
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };
}