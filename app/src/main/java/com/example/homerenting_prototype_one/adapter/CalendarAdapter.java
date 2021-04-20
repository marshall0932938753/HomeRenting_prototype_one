package com.example.homerenting_prototype_one.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarAdapter extends ArrayAdapter<Date> {
    private LayoutInflater inflater;
    private HashSet<Date> eventDays;
    public CalendarAdapter(@NonNull Context context, ArrayList<Date> days, HashSet<Date> eventDays) {
        super( context, R.layout.calendar_day_layout, days );
        this.eventDays = eventDays;
        inflater = LayoutInflater.from( context );
    }

    public View getView(int position, View view, ViewGroup parent){
        // day in question
        Calendar calendar = Calendar.getInstance();
        Date date = getItem( position );
        calendar.setTime( date );
        int day = calendar.get( Calendar.DATE );
        int month = calendar.get( Calendar.MONTH );
        int year = calendar.get( Calendar.YEAR );

        // today
        Date today = new Date();
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(today);

        // inflate item if it does not exist yet
        if (view==null){
            view = inflater.inflate( R.layout.calendar_day_layout, parent, false);
        }
        ((TextView)view).setTypeface( null, Typeface.NORMAL );
        ((TextView)view).setTextColor( Color.BLACK );

        if (month!=calendarToday.get( Calendar.MONTH )||year!=calendarToday.get( Calendar.YEAR )){
            ((TextView)view).setTextColor( Color.parseColor( "#E0E0E0" ));
        }else if (day==calendarToday.get( Calendar.DATE )){
            ((TextView)view).setTextColor( Color.WHITE );
            ((TextView)view).setGravity( Gravity.CENTER );
            //view.setBackgroundResource( R.drawable. );
        }
        ((TextView)view).setText( String.valueOf( calendar.get( Calendar.DATE ) ) );
        return view;
    }
}
