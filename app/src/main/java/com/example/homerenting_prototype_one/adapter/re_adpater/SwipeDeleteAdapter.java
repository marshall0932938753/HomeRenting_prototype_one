package com.example.homerenting_prototype_one.adapter.re_adpater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.homerenting_prototype_one.show.global_function.dip2px;
import static com.example.homerenting_prototype_one.show.global_function.removeNew;

public class SwipeDeleteAdapter extends RecyclerView.Adapter<SwipeDeleteAdapter.ViewHolder> {
    public String type = "";
    private ArrayList<String[]> data;
    private Context context;
    private Class mTarget;

    public SwipeDeleteAdapter(Context context, ArrayList<String[]> data, Class target){
        this.context = context;
        this.data = data;
        mTarget = target;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        int i = 1;

        //time zone
        if(data.get(position).length >= 8){
            holder.date_text.setText(data.get(position)[i++]);
            holder.time_text.setText(data.get(position)[i++]);
        }
        else{
            holder.time_layout.setVisibility(View.GONE);
            holder.main_layout.setPadding(dip2px(holder.item_layout.getContext(), 10),0,0,0);
        }

        //main zone
        holder.name_text.setText(data.get(position)[i++]);
        holder.gender_text.setText(data.get(position)[i++]);
        holder.phone_text.setText(data.get(position)[i++]);
        holder.address_text.setText(data.get(position)[i++]);
        if(data.get(position)[i++].equals("0")) {
            holder.add_text.setVisibility(View.VISIBLE);
        }
        else holder.add_text.setVisibility(View.GONE);

        //icon zone
        if(Arrays.asList(data.get(position)).contains("assigned")){
            holder.new_icon.setVisibility(View.GONE);
            holder.assign_text.setVisibility(View.VISIBLE);
        }
        else {
            if(!data.get(position)[i++].equals("1") || Arrays.asList(data.get(position)).contains("done_today"))
                holder.new_icon.setVisibility(View.INVISIBLE);
        }

        //main zone color
        if(Arrays.asList(data.get(position)).contains("cancel") || Arrays.asList(data.get(position)).contains("done_today")){
            holder.date_text.setTextColor(Color.rgb(152, 152, 152));
            holder.time_text.setTextColor(Color.rgb(152, 152, 152));
            holder.name_text.setTextColor(Color.rgb(112, 112, 112));
        }

        //click
        holder.setOnItemClick(v -> {
            if(mTarget != null){
                String[] row_data = data.get(holder.getAdapterPosition());

                String order_id = row_data[0];
                Bundle bundle = new Bundle();
                bundle.putString("order_id", order_id);

                String newicon = row_data[row_data.length-1];
                if(newicon.equals("1")) removeNew(order_id, context);

                Intent intent = new Intent();
                intent.setClass(context, mTarget);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void deleteItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    public String[] getItem(int position){
        return data.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout item_layout;
        LinearLayout time_layout, main_layout;
        TextView date_text, time_text, name_text, gender_text, add_text, phone_text, address_text, assign_text;
        ImageView add_icon, new_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            item_layout = itemView.findViewById(R.id.row_layout);
            time_layout = itemView.findViewById(R.id.timeLayout);
            main_layout = itemView.findViewById(R.id.mainLayout);
            date_text = itemView.findViewById(R.id.month_date_text);
            time_text = itemView.findViewById(R.id.time_text);
            name_text = itemView.findViewById(R.id.name_text);
            gender_text = itemView.findViewById(R.id.gender_text);
            add_icon = itemView.findViewById(R.id.add_icon);
            add_text = itemView.findViewById(R.id.add_text);
            phone_text = itemView.findViewById(R.id.phone_text);
            address_text = itemView.findViewById(R.id.address_text);
            new_icon = itemView.findViewById(R.id.new_icon_img);
            assign_text = itemView.findViewById(R.id.assign_text);
        }

        public void setOnItemClick(View.OnClickListener listener){
            this.view.setOnClickListener(listener);
        }
    }
}
