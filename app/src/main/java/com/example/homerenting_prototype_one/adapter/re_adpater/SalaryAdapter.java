package com.example.homerenting_prototype_one.adapter.re_adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.ViewHolder> {
    private ArrayList<String[]> mData;

    public SalaryAdapter(ArrayList<String[]> data) {
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.salary_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.employee = view.findViewById(R.id.employee_SI);
        holder.salary = view.findViewById(R.id.salary_SI);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.employee.setText(mData.get(position)[0]);
        holder.salary.setText(mData.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView employee, salary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
