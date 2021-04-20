package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;
import java.util.Arrays;

public class New_CalendarAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String[]> data;
    private Boolean select_v, select_o;
    String TAG = "New_CalendarAdapter";

    public New_CalendarAdapter(ArrayList<String[]> data){
        this.data = data;
        select_v = true;
        select_o = false;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        Object object = data.get(position);
        return data.indexOf(object);
    }

    static class CalendarViewHolder{
        TextView counter, time, address;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        CalendarViewHolder viewHolder = null;
        if (context==null)
            context = parent.getContext();
        if (view==null) {
            view = LayoutInflater.from(context).inflate(R.layout.calendar_day_layout, null);
        }
        viewHolder = new CalendarViewHolder();
        viewHolder.counter = view.findViewById(R.id.counter_Ca);
        viewHolder.time = view.findViewById(R.id.time_Ca);
        viewHolder.address = view.findViewById(R.id.address_Ca);
        view.setTag(viewHolder);

        Log.d(TAG, position+". "+ Arrays.toString(data.get(position)));
        viewHolder.counter.setText(String.valueOf(position));
        if(!data.get(position)[3].equals("evaluating"))
            viewHolder.counter.setBackgroundColor(Color.rgb(25, 176, 237)); //藍色
        viewHolder.time.setText(data.get(position)[1]);
        viewHolder.address.setText(data.get(position)[2]);

        if(position==0){
            viewHolder.counter.setText("編號");
            viewHolder.counter.setTypeface(null, Typeface.NORMAL);
            viewHolder.counter.setBackgroundColor(0x00000000);
            viewHolder.counter.setTextColor(Color.rgb(0,0,0));
            viewHolder.time.setTextColor(Color.rgb(0,0,0));
            viewHolder.address.setTextColor(Color.rgb(0,0,0));
        }

        return view;
    }
}
