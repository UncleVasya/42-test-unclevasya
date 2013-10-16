package com.uvs.coffeejob;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class CalendarFragment extends SherlockFragment {
    private Button calendarHeader;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private CalendarAdapter adapter;
    private GregorianCalendar _calendar;
    private int month, year;
    
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_widget, container, false);
        
        _calendar = (GregorianCalendar) GregorianCalendar.getInstance(Locale.US);
        month = _calendar.get(Calendar.MONTH);
        year = _calendar.get(Calendar.YEAR);

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(onClickListener);

        calendarHeader = (Button) view.findViewById(R.id.calendarHeader);
        Log.i("bla", calendarHeader.toString());
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        calendarHeader.setText(format.format(_calendar.getTime()));

        nextMonth = (ImageView)view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(onClickListener);

        calendarView = (GridView) view.findViewById(R.id.calendar);

        adapter = new CalendarAdapter(view.getContext(), 
                                      R.id.calendar_item, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
        
        return view;
    }
    
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v){
            if (v == prevMonth) {
                if (month <= 1) {
                    month = 11;
                    year--;
                }
                else {
                    month--;
                }        
            }
            else if (v == nextMonth){
                if (month >= 11) {
                    month = 0;
                    year++;
                } 
                else {
                    month++;
                }
            }
            _calendar.set(Calendar.MONTH, month);
            _calendar.set(Calendar.YEAR, year);
            
            adapter = new CalendarAdapter(v.getContext(), 
                                          R.id.calendar_item, month, year);
            adapter.notifyDataSetChanged();
            calendarView.setAdapter(adapter);
            
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            calendarHeader.setText(format.format(_calendar.getTime()));
        }
    };

    public class CalendarAdapter extends BaseAdapter implements OnClickListener {
        private final Context _context;
        private final List<Integer> dayNums;
        private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        private Button gridcell;

        public CalendarAdapter(Context context, int textViewResourceId, int month, int year) {
            super();
            this._context = context;
            this.dayNums = new ArrayList<Integer>();

            addData(month, year);
        }

        public Integer getItem(int position) {
            return dayNums.get(position);
        }

        @Override
        public int getCount() {
            return dayNums.size();
        }

        private void addData(int mm, int yy) {
            int daysInMonth = daysOfMonth[mm];
            if (_calendar.isLeapYear(_calendar.get(Calendar.YEAR)) && mm == 1) {
                ++daysInMonth;
            }

            for (int i = 1; i <= daysInMonth; i++) {
                dayNums.add(i);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(
                                          Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.calendar_item, parent, false);
            }

            gridcell = (Button) row.findViewById(R.id.calendar_item);
            gridcell.setOnClickListener(this);

            gridcell.setText(String.valueOf(position+1));
            gridcell.setTag(position+1);

            if (position+1 == _calendar.get(Calendar.DAY_OF_MONTH)) {
                gridcell.setTextColor(Color.BLUE);
            }

            return row;
        }

        @Override
        public void onClick(View view) {
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            _calendar.set(Calendar.DAY_OF_MONTH, (Integer) view.getTag());      
            calendarHeader.setText(format.format(_calendar.getTime()));
            adapter.notifyDataSetChanged();
            calendarView.setAdapter(adapter);
        }
    }
}