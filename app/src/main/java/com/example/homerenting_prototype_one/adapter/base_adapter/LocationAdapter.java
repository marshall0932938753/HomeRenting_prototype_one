package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.homerenting_prototype_one.R;

import java.util.List;

public class LocationAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<String[]> data;
    public LocationAdapter(List<String[]>data){
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
        LocationViewHolder viewHolder = new LocationViewHolder();
        if (context==null)
            context=parent.getContext();
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.furniture_location_item, null);
            viewHolder = new LocationViewHolder();
            viewHolder.floor_text = convertView.findViewById( R.id.location_floor );
            viewHolder.room_text = convertView.findViewById( R.id.location_room );
            viewHolder.item_text = convertView.findViewById( R.id.location_item );
            viewHolder.amount_text = convertView.findViewById( R.id.location_total );
            convertView.setTag( viewHolder );
        }
        viewHolder = (LocationViewHolder)convertView.getTag();
        viewHolder.floor_text.setTag( R.id.location_floor,position );
        if(!data.get(position)[0].isEmpty())
            viewHolder.floor_text.setText(data.get(position)[0]+"樓 / ");
        else viewHolder.floor_text.setText("");
        viewHolder.room_text.setTag( R.id.location_room,position );
        viewHolder.room_text.setText(data.get(position)[1]);
        viewHolder.item_text.setTag( R.id.location_item,position );
        viewHolder.item_text.setText(data.get(position)[2]);
        viewHolder.amount_text.setTag( R.id.location_total,position );
        viewHolder.amount_text.setText(data.get(position)[3]);
        return convertView;
    }
    static class LocationViewHolder{
        TextView floor_text,room_text,item_text,amount_text;
    }
}
