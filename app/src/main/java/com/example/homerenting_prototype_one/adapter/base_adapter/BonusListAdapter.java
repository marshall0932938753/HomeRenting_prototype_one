package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.bouns.Bonus_List_Detail;

import java.util.List;

public class BonusListAdapter extends BaseAdapter implements View.OnClickListener{
    private Context context;
    private List<String> data;

    public BonusListAdapter(List<String>data){
        this.data=data;
    }
    @Override
    public void onClick(View v) {

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
        BonusViewHolder viewHolder = null;
        if (context == null)
            context = parent.getContext();
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item, null);
            viewHolder = new BonusViewHolder();
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
        viewHolder = (BonusViewHolder) convertView.getTag();
        viewHolder.year.setTag( R.id.year_text,position );
        viewHolder.year.setText( data.get( position ) );
        viewHolder.Jan_btn.setTag( R.id.january_btn,position );
        viewHolder.Jan_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context, Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","1月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Jan_btn.setText( "1月" );
        viewHolder.Feb_btn.setTag( R.id.february_btn,position );
        viewHolder.Feb_btn.setText( "2月" );
        viewHolder.Feb_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","2月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Mar_btn.setTag( R.id.march_btn,position );
        viewHolder.May_btn.setText( "3月" );
        viewHolder.Mar_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","3月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Apr_btn.setTag( R.id.april_btn,position );
        viewHolder.Apr_btn.setText( "4月" );
        viewHolder.Apr_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","4月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.May_btn.setTag( R.id.may_btn,position );
        viewHolder.May_btn.setText( "5月" );
        viewHolder.May_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","5月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Jun_btn.setTag( R.id.june_btn,position );
        viewHolder.Jun_btn.setText( "6月" );
        viewHolder.Jun_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","6月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Jul_btn.setTag( R.id.july_btn,position );
        viewHolder.Jul_btn.setText( "7月" );
        viewHolder.Jul_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","7月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Aug_btn.setTag( R.id.august_btn,position );
        viewHolder.Aug_btn.setText( "8月" );
        viewHolder.Aug_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","8月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Sep_btn.setTag( R.id.september_btn,position );
        viewHolder.Sep_btn.setText( "9月" );
        viewHolder.Sep_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","9月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Oct_btn.setTag( R.id.october_btn,position );
        viewHolder.Oct_btn.setText( "10月" );
        viewHolder.Oct_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","10月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Nov_btn.setTag( R.id.november_btn,position );
        viewHolder.Nov_btn.setText( "11月" );
        viewHolder.Nov_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","11月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        viewHolder.Dec_btn.setTag( R.id.december_btn,position );
        viewHolder.Dec_btn.setText( "12月" );
        viewHolder.Dec_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent month_intent = new Intent();
                month_intent.setClass(context,Bonus_List_Detail.class );
                Bundle month_bundle = new Bundle();
                month_bundle.putString( "month","12月" );
                month_intent.putExtras( month_bundle );
                context.startActivity(month_intent);
            }
        } );
        return convertView;
    }
    static class BonusViewHolder{
        TextView year;
        Button Jan_btn, Feb_btn, Mar_btn, Apr_btn, May_btn, Jun_btn,
                Jul_btn, Aug_btn,  Sep_btn, Oct_btn, Nov_btn, Dec_btn;
    }
}
