package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.homerenting_prototype_one.R;

public class NoDataAdapter extends BaseAdapter {
    private String[] data;
    String TAG = "noDataAdapter";

    //public NoDataAdapter(String[] data) { this.data=data;}

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        ConstraintLayout noData_CL;
        TextView noDate_text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_data,null);
            viewHolder = new ViewHolder();
            viewHolder.noData_CL = convertView.findViewById(R.id.noData_CL);
            viewHolder.noDate_text = convertView.findViewById(R.id.noData_text);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.noData_CL.setTag(R.id.noData_CL, position);
        viewHolder.noDate_text.setTag(R.id.noData_text, position);
        viewHolder.noDate_text.setText("現在沒有資料");
        Log.d(TAG, "position: "+position);
        return convertView;
    }
}
