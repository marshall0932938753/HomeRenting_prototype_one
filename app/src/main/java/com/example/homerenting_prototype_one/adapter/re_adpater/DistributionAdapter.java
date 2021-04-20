package com.example.homerenting_prototype_one.adapter.re_adpater;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

public class DistributionAdapter extends RecyclerView.Adapter<DistributionAdapter.ViewHolder>{
    private ArrayList<String> mData;
    private int ready = 0;

    public DistributionAdapter(ArrayList<String> data){
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.distribution_item, parent, false);
        ready = ready+1;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.employee.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public int getReady(){
        if(ready == getItemCount()) return -1;
        else return ready;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView employee, salaryPText;
        EditText salaryPEdit, salaryEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employee = itemView.findViewById(R.id.employee_name_DI);
            salaryPText = itemView.findViewById(R.id.salaryP_text_DI);
            salaryPEdit = itemView.findViewById(R.id.salaryP_DI);
            salaryEdit = itemView.findViewById(R.id.salary_DI);
        }
    }
}
