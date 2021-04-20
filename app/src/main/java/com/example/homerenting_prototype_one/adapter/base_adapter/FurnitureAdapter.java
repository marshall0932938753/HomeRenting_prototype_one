package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;


public class FurnitureAdapter extends BaseAdapter{
    private Context context;
    private static String space;
    private static String order_id;
    private ArrayList<String[]> data;
    String TAG = "FurnitureAdapter";

    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;

    public FurnitureAdapter(ArrayList<String[]> data, String space){
        this.data = data;
        this.space = space;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (context == null)
            context = parent.getContext();
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.furniture_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.furniture_text);
            viewHolder.number = convertView.findViewById(R.id.furniture_number_text);
            viewHolder.originalNumber = convertView.findViewById(R.id.originalNum_FI);
            viewHolder.to = convertView.findViewById(R.id.to_FI);
            viewHolder.minus_btn = convertView.findViewById(R.id.minus_btn);
            viewHolder.plus_btn = convertView.findViewById(R.id.plus_btn);
            viewHolder.add_btn = convertView.findViewById(R.id.add_furniture_btn);
            viewHolder.item = convertView.findViewById(R.id.item_LL_FI);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setTag(R.id.furniture_text,position);
        viewHolder.name.setText(data.get(position)[1]);

        //設置數量
        viewHolder.originalNumber.setTag(R.id.furniture_number_text,position);
        viewHolder.number.setTag(R.id.furniture_number_text,position);
        if(!data.get(position)[5].equals("999")) setEditedNum(viewHolder, position);
        else setOriginalNum(viewHolder, position);


        //增加減少按鈕
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.minus_btn.setOnClickListener(v -> decrease(finalViewHolder, position));
        viewHolder.plus_btn.setOnClickListener(v -> increase(finalViewHolder, position));
        //持續增減
        setBtn(viewHolder.plus_btn, viewHolder, position, true);
        setBtn(viewHolder.minus_btn, viewHolder, position, false);

        return convertView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setBtn(Button btn, ViewHolder viewHolder, int position, boolean isAdd) {
        //開啟持續增減(按下去的時候)
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(isAdd) mAutoIncrement = true;
                else mAutoDecrement = true;
                new Handler().post(new RptUpdater(viewHolder, position));
                return true;
            }
        });

        //關閉持續增減(放開手的時候)
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction()==MotionEvent.ACTION_CANCEL) {
                    if(isAdd && mAutoIncrement) mAutoIncrement = false;
                    else if (!isAdd && mAutoDecrement) mAutoDecrement = false;
                }
                return false;
            }
        });
    }

    class RptUpdater implements Runnable {
        ViewHolder viewHolder;
        int position;

        public RptUpdater(ViewHolder viewHolder, int position) {
            this.viewHolder = viewHolder;
            this.position = position;
        }

        @Override
        public void run() {
            if (mAutoIncrement) {
                increase(viewHolder, position);
                new Handler().postDelayed(new RptUpdater(viewHolder, position), 100);
            } else if (mAutoDecrement) {
                decrease(viewHolder, position);
                new Handler().postDelayed(new RptUpdater(viewHolder, position), 100);
            }
        }
    }

    private void increase(ViewHolder viewHolder, int position){
        int num = Integer.parseInt(viewHolder.number.getText().toString());
        data.get(position)[3] = String.valueOf(++num);
        setDataNum(viewHolder, position, String.valueOf(num));
        viewHolder.number.setText(String.valueOf(num));
    }

    private void decrease(ViewHolder viewHolder, int position) {
        int num = Integer.parseInt(viewHolder.number.getText().toString());
        if(num > 0) {
            data.get(position)[3] = String.valueOf(--num);
            setDataNum(viewHolder, position, String.valueOf(num));
        }
        viewHolder.number.setText(String.valueOf(num));
    }

    private void setDataNum(ViewHolder viewHolder, int position, String num) {
        if(data.get(position)[2].equals(num)) {
            data.get(position)[3] = "-1";
            data.get(position)[5] = "999";
            setOriginalNum(viewHolder, position);
        }
        else {
            data.get(position)[5] = getCompany_id(context);
            setEditedNum(viewHolder, position);
        }
    }

    private void setEditedNum(ViewHolder viewHolder, int position) {
        viewHolder.originalNumber.setText(data.get(position)[2]);
        viewHolder.originalNumber.setVisibility(View.VISIBLE);
        viewHolder.to.setVisibility(View.VISIBLE);
        viewHolder.item.setBackgroundColor(Color.parseColor("#FFE7E7"));
        viewHolder.number.setText(data.get(position)[3]);
//        Log.d(TAG, position+". ("+space+")edited data: "+ Arrays.toString(data.get(position)));
    }

    private void setOriginalNum(ViewHolder viewHolder, int position) {
        viewHolder.originalNumber.setVisibility(View.GONE);
        viewHolder.to.setVisibility(View.GONE);
        viewHolder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));
        viewHolder.number.setText(data.get(position)[2]);
//        Log.d(TAG, position+". ("+space+")original data: "+ Arrays.toString(data.get(position)));
    }

    static class ViewHolder{
        TextView name, number, originalNumber, to;
        Button minus_btn, plus_btn;
        Button add_btn;
        LinearLayout item;
    }

    public void setOrder_id(String order_id){
        this.order_id = order_id;
    }

    public static String getOrder_id(){
        return order_id;
    }
}