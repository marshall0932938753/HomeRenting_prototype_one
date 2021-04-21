package com.example.homerenting_prototype_one.adapter.re_adpater;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.R;

import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private ArrayList<String[]> cars;

    private int limit = 40;

    Context context;

    String TAG = "CarAdapter";

    public CarAdapter(ArrayList cars){
        this.cars = cars;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.weight.setText(cars.get(position)[0]);
        holder.type.setText(cars.get(position)[1]);
        holder.num.setText(cars.get(position)[2]);

        setCarStr(holder.weight, position, 0);
//        setCarStr(holder.type, position, 1);
        setCarStr(holder.num, position, 2);

        final String[] weights = {"3.5噸", "3.5噸", "5噸", "6-8噸", "10噸"};

        ArrayAdapter<CharSequence> weightAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, weights);
        holder.weight_sp.setAdapter(weightAdapter);
        holder.weight_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int w_position, long id) {
                holder.weight.setText(weights[position]);

                if(w_position >= weights.length) cars.get(position)[0] = null;
                else cars.get(position)[0] = weights[w_position];
                holder.weight.setText(cars.get(position)[0]);
                Toast.makeText(context, "您選擇的噸數是: " + holder.weight_sp.getSelectedItem(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        final String[] types = {"箱型車", "平斗車"}; //其他
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, types);
        holder.type_sp.setAdapter(typeAdapter);
        holder.type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int t_position, long id) {
                if(t_position >= types.length) cars.get(position)[1] = null;
                else cars.get(position)[1] = types[t_position];
                holder.type.setText(cars.get(position)[1]);
                Toast.makeText(context, "您選擇的車型是: " + holder.type_sp.getSelectedItem(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        holder.setIsRecyclable(false);
        if(holder.getAdapterPosition() == 0) showAddBtn(holder.add, holder.delete);
        else {
            holder.add.setVisibility(View.GONE);
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.delete.setOnClickListener(v -> {
            holder.weight.setError(null);
            holder.type.setError(null);
            holder.num.setError(null);
            cars.remove(holder.getAdapterPosition());
//                notifyItemInserted(cars.size()-1);
            notifyDataSetChanged();

            for(int i = holder.getAdapterPosition(); i < getItemCount(); i++){
                setCarStr(holder.weight, i, 0);
                setCarStr(holder.type, i, 1);
                setCarStr(holder.num, i, 2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public void showAddBtn(Button add, Button delete){
        add.setVisibility(View.VISIBLE);
        delete.setVisibility(View.GONE);
        add.setOnClickListener(v -> {
            if (cars.size() < limit){
                String[] n = {"", "", ""};
                cars.add(n);
                notifyItemInserted(cars.size()-1);
            }
        });
    }

    public void setCarStr(EditText editText, int position, int i){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String str = editText.getText().toString();
                if(!str.isEmpty()) cars.get(position)[i] = str;
                else if(!cars.get(position)[i].equals("")) cars.get(position)[i] = "";

                Log.d(TAG, "cars.get("+position+")["+i+"] = "+cars.get(position)[i]);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText weight, type, num;
        TextView ton;
        Spinner type_sp, weight_sp;
        Button add, delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weight = itemView.findViewById(R.id.weight_CI);
            weight.setFocusable(false);
            weight.setFocusableInTouchMode(false);
            type = itemView.findViewById(R.id.type_CI);
            num = itemView.findViewById(R.id.num_CI);
            //ton = itemView.findViewById(R.id.ton_CI);
            type_sp = itemView.findViewById(R.id.type_sp_CI);
            add = itemView.findViewById(R.id.add_btn_CI);
            delete = itemView.findViewById(R.id.delete_btn_CI);
            weight_sp = itemView.findViewById(R.id.car_choice);
        }
    }
}
