package com.example.homerenting_prototype_one.adapter.re_adpater;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.setting.Evaluation_Detail;
import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    ArrayList<String[]> data;

    public CommentAdapter(Context context, ArrayList<String[]> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.star.setText(data.get(position)[1]);
        holder.name.setText(data.get(position)[2]);
        holder.nameTitle.setText(data.get(position)[3]);
        holder.date.setText(data.get(position)[4]);
        holder.comment.setText(data.get(position)[5]);

        holder.commentCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("comment_id", data.get(position)[0]);
                Intent intent = new Intent(context, Evaluation_Detail.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout commentCL;
        TextView star, name, nameTitle, date, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentCL = itemView.findViewById(R.id.comment_CL_CI);
            star = itemView.findViewById(R.id.star_CI);
            name = itemView.findViewById(R.id.name_CI);
            nameTitle = itemView.findViewById(R.id.nameTitle_CI);
            date = itemView.findViewById(R.id.date_CI);
            comment = itemView.findViewById(R.id.comment_CI);
        }
    }
}
