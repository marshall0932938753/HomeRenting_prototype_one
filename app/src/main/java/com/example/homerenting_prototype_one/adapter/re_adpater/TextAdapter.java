package com.example.homerenting_prototype_one.adapter.re_adpater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder> {
    private ArrayList<String[]> data;
    private String TAG = "TextAdapter";

    public TextAdapter(ArrayList<String[]> data){
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = data.get(position)[1];
        if(data.get(position).length > 2){
            text = text+"噸"+data.get(position)[2]+" "+data.get(position)[3];
            if(data.get(position)[4].equals("1")) text = text + " (verified)";
        }
        holder.item.setText(text);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void deleteItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
        Log.d(TAG, "delete");
    }

    public String[] getItem(int position){
        return data.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.text_item_TI);
        }
    }
}
