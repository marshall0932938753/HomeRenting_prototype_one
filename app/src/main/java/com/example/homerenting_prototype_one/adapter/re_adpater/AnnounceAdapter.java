package com.example.homerenting_prototype_one.adapter.re_adpater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

import static com.example.homerenting_prototype_one.show.global_function.removeAnnounceNew;

public class AnnounceAdapter extends RecyclerView.Adapter<AnnounceAdapter.ViewHolder> {
    private ArrayList<String[]> data;
    private Context context;

    public AnnounceAdapter(Context context, ArrayList<String[]> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announce_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.title.setText(data.get(position)[1]);
        holder.date.setText(data.get(position)[2]);
        holder.summary.setText(data.get(position)[3]);
        if(data.get(position)[5].equals("0")) holder.newIcon.setVisibility(View.INVISIBLE);

        holder.announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(data.get(position)[1])
                        .setMessage(data.get(position)[4])
                        .setPositiveButton("確認", null)
                        .show();

                removeAnnounceNew(data.get(position)[0], context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout announce;
        ImageView newIcon;
        TextView title, date, summary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            announce = itemView.findViewById(R.id.anncounce_CL_AI);
            newIcon = itemView.findViewById(R.id.newIcon_AI);
            title = itemView.findViewById(R.id.title_AI);
            date = itemView.findViewById(R.id.date_AI);
            summary = itemView.findViewById(R.id.summary_AI);
        }
    }
}
