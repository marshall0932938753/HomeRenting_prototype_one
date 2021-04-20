package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.setting.Record_Detail;

import java.util.ArrayList;
import java.util.Objects;

public class MonthAdapter extends BaseAdapter {
    private Context context;
    private final Class mTarget;
    ArrayList<ArrayList<String>> data;

    String TAG = "MonthAdapter";

    public  MonthAdapter(ArrayList<ArrayList<String>> data, Class target){
        this.data = data;
        mTarget = target;
    }

    @Override
    public int getCount() {
        return data == null ? 0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return  data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonthViewHolder viewHolder = null;
        if (context == null)
            context = parent.getContext();
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item, null);
            viewHolder = new MonthViewHolder();
            viewHolder.year = convertView.findViewById(R.id.year_text);
            viewHolder.Jan_btn = convertView.findViewById(R.id.january_btn);
            viewHolder.Feb_btn = convertView.findViewById(R.id.february_btn);
            viewHolder.Mar_btn = convertView.findViewById(R.id.march_btn);
            viewHolder.Apr_btn = convertView.findViewById( R.id.april_btn );
            viewHolder.May_btn = convertView.findViewById( R.id.may_btn );
            viewHolder.Jun_btn = convertView.findViewById( R.id.june_btn );
            viewHolder.Jul_btn = convertView.findViewById( R.id.july_btn );
            viewHolder.Aug_btn = convertView.findViewById( R.id.august_btn );
            viewHolder.Sep_btn = convertView.findViewById( R.id.september_btn );
            viewHolder.Oct_btn = convertView.findViewById( R.id.october_btn );
            viewHolder.Nov_btn = convertView.findViewById(R.id.november_btn);
            viewHolder.Dec_btn = convertView.findViewById( R.id.december_btn );
            convertView.setTag( viewHolder );
        }
        viewHolder = (MonthViewHolder) convertView.getTag();
        viewHolder.year.setTag( R.id.year_text,position );
        viewHolder.year.setText(data.get(position).get(0));

        for(int i=1; i <= 12; i++)
            setBtn(Objects.requireNonNull(getBtn(viewHolder, i)), data.get(position).get(0), i, hasOrder(position, i));

        return convertView;
    }

    static class MonthViewHolder{
        TextView year;
        Button Jan_btn, Feb_btn, Mar_btn, Apr_btn, May_btn, Jun_btn,
                Jul_btn, Aug_btn, Sep_btn, Oct_btn, Nov_btn, Dec_btn;
    }

    private Button getBtn(MonthViewHolder viewHolder, int month){
        switch (month){
            case 1:
                return viewHolder.Jan_btn;
            case 2:
                return viewHolder.Feb_btn;
            case 3:
                return viewHolder.Mar_btn;
            case 4:
                return viewHolder.Apr_btn;
            case 5:
                return viewHolder.May_btn;
            case 6:
                return viewHolder.Jun_btn;
            case 7:
                return viewHolder.Jul_btn;
            case 8:
                return viewHolder.Aug_btn;
            case 9:
                return viewHolder.Sep_btn;
            case 10:
                return viewHolder.Oct_btn;
            case 11:
                return viewHolder.Nov_btn;
            case 12:
                return viewHolder.Dec_btn;
        }
        return null;
    }

    private void setBtn(Button btn, final String year, final int month, boolean has){
        btn.setText( month+"月" );
        if(!has){
            btn.setTextColor(Color.rgb(122, 164, 179));
            btn.setBackgroundResource(R.drawable.frame_background);
        }
        else{
            btn.setTextColor(Color.parseColor("#19B0ED"));
            btn.setBackgroundResource(R.drawable.month_btn_drawable);
            btn.setOnClickListener(v -> {
                if(mTarget != null) {
                    Intent month_intent = new Intent();
                    month_intent.setClass(context, mTarget);
                    Bundle month_bundle = new Bundle();
                    month_bundle.putString("year", year);
                    month_bundle.putString("month", String.valueOf(month));
                    month_intent.putExtras(month_bundle);
                    context.startActivity(month_intent);
                }
            });
        }
    }

    private boolean hasOrder(int position, int month){
        String monthStr = String.valueOf(month);
        if(month < 10) monthStr = "0"+monthStr;
        return data.get(position).contains(monthStr);
    }
}
