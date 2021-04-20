package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

public class DetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String[]> data;
    String TAG = "Furniture_detail";

    public DetailAdapter(ArrayList<String[]> data){
        this.data=data;
    }

    static class DetailViewHolder{
        TextView num_text, name_text, amount_text, ps_text;
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

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        DetailViewHolder viewHolder = null;
        if (context==null)
            context = parent.getContext();
        if (view==null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.furniture_detail_btn, null);
            viewHolder = new DetailViewHolder();
            viewHolder.num_text = view.findViewById( R.id.detail_number );
            viewHolder.name_text = view.findViewById( R.id.detail_name );
            viewHolder.amount_text = view.findViewById( R.id.detail_total );
            viewHolder.ps_text = view.findViewById( R.id.detail_ps );
            view.setTag( viewHolder );
        }
        Log.d(TAG, "position: "+position);
        viewHolder.num_text.setTag( R.id.detail_number,position );
        viewHolder.num_text.setText(String.valueOf( position+1 ));
        viewHolder.name_text.setTag( R.id.detail_name,position );
        viewHolder.name_text.setText(data.get(position)[0]);
        viewHolder.amount_text.setTag( R.id.detail_total,position );
        viewHolder.amount_text.setText(data.get(position)[1]);
        viewHolder.ps_text.setTag( R.id.detail_ps,position );
        viewHolder.ps_text.setText( "" );
        return view;
    }

}
