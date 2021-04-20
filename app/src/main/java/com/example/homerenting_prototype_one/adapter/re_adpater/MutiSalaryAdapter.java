package com.example.homerenting_prototype_one.adapter.re_adpater;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;
import java.util.Arrays;

public class MutiSalaryAdapter extends RecyclerView.Adapter<MutiSalaryAdapter.MutiSalaryViewHolder> {
    private ArrayList<ArrayList<String[]>> mDatas = new ArrayList<>();
    private ArrayList<Integer> months = new ArrayList<>();
    private Context mContext = null;
    private String TAG = MutiSalaryAdapter.class.getSimpleName();

    public MutiSalaryAdapter(ArrayList<String[]> data, int month) {
        mDatas.add(new ArrayList<>());
        mDatas.add(data);
        mDatas.add(new ArrayList<>());
        months.add(-1);
        months.add(month);
        months.add(-1);
    }

    @NonNull
    @Override
    public MutiSalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null) mContext = parent.getContext();
        return new MutiSalaryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MutiSalaryViewHolder holder, int position) {
        SalaryAdapter salaryAdapter = new SalaryAdapter(mDatas.get(position));
        holder.list.setLayoutManager(new LinearLayoutManager(mContext));
        holder.list.setAdapter(salaryAdapter);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public int getMonth(int position) { return months.get(position); }

    public int getMonth(int position, int type) {
        switch (type) {
            case 0:
                return months.get(position);
            case 1:
                int next = months.get(position)+1;
                if(next > 12) next = next-12;
                return next;
            case 2:
                int last = months.get(position)-1;
                if(last < 1) last = 12;
                return last;
            default:
                return -1;
        }
    }

    public ArrayList<String> getStaffName(int position){
        ArrayList<String> employee_names = new ArrayList<>();
        int i;
        for(i = 0; i < mDatas.get(position).size(); i++){
            employee_names.add(mDatas.get(position).get(i)[0]);
        }
        return employee_names;
    }

    public ArrayList<Integer> getStaffPay(int position){
        ArrayList<Integer> salaries = new ArrayList<>();
        int i;
        for(i = 0; i < mDatas.get(position).size(); i++){
            if(!mDatas.get(position).get(i)[1].isEmpty() && !mDatas.get(position).get(i)[1].equals(""))
                salaries.add(Integer.valueOf(mDatas.get(position).get(i)[1]));
        }
        return salaries;
    }

    public void addPage(ArrayList<String[]> data) {
        mDatas.add(data);
        months.add(-1);
        notifyDataSetChanged();
    }

    public void addPage(ArrayList<String[]> data, int position) {
        mDatas.add(position, data);
        months.add(position, -1);
        notifyDataSetChanged();
    }

    public void setmData(ArrayList<String[]> data, int month, int position) {
        mDatas.set(position, data);
        if(month == 13) month = 1;
        else if(month == 0) month = 12;
        months.set(position, month);
        notifyItemChanged(position);
        showDatas();
    }

    public void showDatas(){
        int i, ii;
        Log.d(TAG, "index. (month) {staff_name, pay}");
        for(i = 0; i < mDatas.size(); i++){
            Log.d(TAG, i+".("+months.get(i)+"){");
            for(ii = 0; ii < mDatas.get(i).size(); ii++){
                Log.d(TAG, "{"+mDatas.get(i).get(ii)[0]+", "+mDatas.get(i).get(ii)[1]+"}");
            }
            Log.d(TAG, "}");
        }
    }

    protected class MutiSalaryViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView list;

        public MutiSalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            list = itemView.findViewById(R.id.list_L);
        }
    }
}
