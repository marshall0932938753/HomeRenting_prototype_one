package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.homerenting_prototype_one.R;

import java.util.List;

public class BonusAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<String> data;
    public BonusAdapter(List<String>data){
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
        if (context==null) {
            context = parent.getContext();
        }
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bonus_item, null);
            viewHolder = new BonusViewHolder();
            viewHolder.name = convertView.findViewById(R.id.bonus_name_text);
            viewHolder.total = convertView.findViewById(R.id.bonus_total_text2);
            viewHolder.first = convertView.findViewById( R.id.bonus_first_text );
            viewHolder.second = convertView.findViewById(R.id.bonus_sec_text);
            viewHolder.third = convertView.findViewById( R.id.bonus_third_text );
            viewHolder.four = convertView.findViewById( R.id.bonus_four_text );
            viewHolder.five = convertView.findViewById( R.id.bonus_five_text );
            viewHolder.six = convertView.findViewById( R.id.bonus_six_text );
            viewHolder.seven = convertView.findViewById( R.id.bonus_seven_text );
            viewHolder.eight = convertView.findViewById( R.id.bonus_eight_text );
            viewHolder.nine = convertView.findViewById(R.id.bonus_nine_text);
            viewHolder.ten = convertView.findViewById( R.id.bonus_ten_text );
            viewHolder.eleven = convertView.findViewById(R.id.bonus_eleven_text);
            viewHolder.twelve = convertView.findViewById(R.id.bonus_twelve_text);
            viewHolder.thirteen = convertView.findViewById(R.id.bonus_thirteen_text);
            viewHolder.fourteen = convertView.findViewById(R.id.bonus_fourteen_text);
            viewHolder.fifteen = convertView.findViewById(R.id.bonus_fifteen_text);
            viewHolder.sixteen = convertView.findViewById(R.id.bonus_sixteen_text);
            viewHolder.seventeen = convertView.findViewById(R.id.bonus_seventeen_text);
            viewHolder.eighteen = convertView.findViewById(R.id.bonus_eighteen_text);
            viewHolder.nineteen = convertView.findViewById(R.id.bonus_nineteen_text);
            viewHolder.twenty = convertView.findViewById(R.id.bonus_twenty_text);
            viewHolder.tOne = convertView.findViewById(R.id.bonus_twoOne_text);
            viewHolder.tTwo = convertView.findViewById(R.id.bonus_twoTwo_text);
            viewHolder.tThree = convertView.findViewById(R.id.bonus_twoThree_text);
            viewHolder.tFour = convertView.findViewById(R.id.bonus_twoFour_text);
            viewHolder.tFive = convertView.findViewById(R.id.bonus_twoFive_text);
            viewHolder.tSix = convertView.findViewById(R.id.bonus_twoSix_text);
            viewHolder.tSeven = convertView.findViewById(R.id.bonus_twoSeven_text);
            viewHolder.tEight = convertView.findViewById(R.id.bonus_twoEight_text);
            viewHolder.tNine = convertView.findViewById(R.id.bonus_twoNine_text);
            viewHolder.thirty = convertView.findViewById(R.id.bonus_thirty_text);
            viewHolder.thirtyOne = convertView.findViewById(R.id.bonus_thirtyOne_text);
            convertView.setTag( viewHolder );
        } else{
            viewHolder = (BonusViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(data.get(position));
        viewHolder.first.setTag( R.id.bonus_first_text,position);
        viewHolder.second.setTag( R.id.bonus_sec_text,position );
        viewHolder.third.setTag( R.id.bonus_third_text,position );
        viewHolder.four.setTag( R.id.bonus_four_text,position );
        viewHolder.five.setTag( R.id.bonus_five_text,position );
        viewHolder.six.setTag( R.id.bonus_six_text,position );
        viewHolder.seven.setTag( R.id.bonus_seven_text,position );
        viewHolder.eight.setTag( R.id.bonus_eight_text,position );
        viewHolder.nine.setTag( R.id.bonus_nine_text,position );
        viewHolder.ten.setTag( R.id.bonus_ten_text,position );
        viewHolder.eleven.setTag( R.id.bonus_eleven_text,position );
        viewHolder.twelve.setTag( R.id.bonus_thirteen_text,position );
        viewHolder.thirteen.setTag( R.id.bonus_thirteen_text,position );
        viewHolder.fourteen.setTag( R.id.bonus_fourteen_text,position );
        viewHolder.fifteen.setTag( R.id.bonus_fifteen_text,position );
        viewHolder.sixteen.setTag( R.id.bonus_sixteen_text,position);
        viewHolder.seventeen.setTag( R.id.bonus_seventeen_text,position );
        viewHolder.eighteen.setTag( R.id.bonus_eighteen_text,position );
        viewHolder.nineteen.setTag( R.id.bonus_nineteen_text,position );
        viewHolder.twenty.setTag( R.id.bonus_twenty_text,position );
        viewHolder.tOne.setTag( R.id.bonus_twoOne_text,position );
        viewHolder.tTwo.setTag( R.id.bonus_twoOne_text,position );
        viewHolder.tThree.setTag( R.id.bonus_twoThree_text,position );
        viewHolder.tFour.setTag( R.id.bonus_twoFour_text,position );
        viewHolder.tFive.setTag( R.id.bonus_twoFive_text,position );
        viewHolder.tSix.setTag( R.id.bonus_twoSix_text,position );
        viewHolder.tSeven.setTag( R.id.bonus_twoSeven_text,position );
        viewHolder.tEight.setTag( R.id.bonus_twoEight_text,position );
        viewHolder.tNine.setTag( R.id.bonus_twoNine_text,position );
        viewHolder.thirty.setTag( R.id.bonus_thirty_text,position );
        viewHolder.thirtyOne.setTag( R.id.bonus_thirtyOne_text,position );
        return convertView;
    }

    static class BonusViewHolder{
        TextView name, total, first,second,third,four,five,six,seven,eight,nine,ten,
                eleven,twelve,thirteen,fourteen,fifteen,sixteen,seventeen,eighteen,nineteen,twenty,
                tOne,tTwo,tThree,tFour,tFive,tSix,tSeven,tEight,tNine,thirty,thirtyOne;
    }
}
