package com.example.homerenting_prototype_one.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homerenting_prototype_one.model.DataModel;
import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {
    private ArrayList<DataModel> dataSet;
    Context mContext;

    private static class ViewHolder{
        TextView txtDate;
        TextView txtTime;
        TextView txtName;
        TextView txtGender;
        TextView txtPhone;
        TextView txtAddress;
        ImageView imgIcon;
    }
    public CustomAdapter(ArrayList<DataModel> data, Context context){
        super(context, R.layout.row_item,data);
        this.dataSet = data;
        this.mContext = context;
    }
    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        Object object = getItem(position);
        DataModel dataModel = (DataModel) object;
//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        DataModel dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtDate = convertView.findViewById(R.id.data_text);
            viewHolder.txtTime = convertView.findViewById(R.id.time_text);
            viewHolder.txtName = convertView.findViewById(R.id.name_SD);
            viewHolder.txtGender = convertView.findViewById(R.id.gender_text);
            viewHolder.txtPhone = convertView.findViewById(R.id.phone_text);
            viewHolder.txtAddress = convertView.findViewById(R.id.address_text);
            viewHolder.imgIcon =convertView.findViewById(R.id.new_icon_img);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtDate.setText(dataModel.getDate());
        viewHolder.txtTime.setText(dataModel.getTime());
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtGender.setText(dataModel.getGender());
        viewHolder.txtPhone.setText(dataModel.getPhone());
        viewHolder.txtAddress.setText(dataModel.getAddress());
        viewHolder.imgIcon.setOnClickListener(this);
        viewHolder.imgIcon.setTag(position);

        return convertView;
    }
}
