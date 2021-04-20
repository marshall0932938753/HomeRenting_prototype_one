package com.example.homerenting_prototype_one.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.adapter.base_adapter.MonthAdapter;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.valuation.Valuation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getMonth;
import static com.example.homerenting_prototype_one.show.global_function.getYear;

public class Setting_Record extends AppCompatActivity {
    private ListView list;
    private ArrayList<ArrayList<String>> data;

    OkHttpClient okHttpClient = new OkHttpClient();
    String TAG = "Setting_Record";
    Context context = Setting_Record.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__record);
        ImageButton back_btn = findViewById(R.id.back_imgBtn);
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        list = findViewById(R.id.year_record_list_SR);

        data = new ArrayList<>();

        getOrder();

        back_btn.setOnClickListener(v -> finish());

        //底下nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Setting_Record.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Setting_Record.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Setting_Record.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Setting_Record.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Setting_Record.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    private void getOrder(){
        String function_name = "all_order_date";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", company_id)
                .build();
        //連線要求
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "/user_data.php")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                //在app畫面上呈現錯誤訊息
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.i(TAG,"responseData: "+responseData); //顯示資料

                try {
                    final JSONArray responseArr = new JSONArray(responseData);

                    int counter = 0;
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject order = responseArr.getJSONObject(i);
                        Log.i(TAG,"order: "+order);

                        String date = order.getString("date");
                        if(date.equals("null") || date.equals("0000-00-00")) continue;
                        String month = getMonth(date);
                        String year = getYear(date);
                        boolean check = false;
                        for(int ii=0; ii<data.size(); ii++){
                            if(data.get(ii).contains(year)){
                                check = true;
                                if(!data.get(ii).contains(month))
                                    data.get(ii).add(month);
                                break;
                            }
                        }
                        if(!check){
                            data.add(new ArrayList<>());
                            data.get(counter).add(year);
                            data.get(counter).add(month);
                            counter = counter+1;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //顯示資料
                if(!responseData.equals("null")) {
                    Log.i(TAG, "data: ");
                    for (int i = 0; i < data.size(); i++) {
                        Log.i(TAG, "(" + data.get(i).get(0) + ")");
                        for (int ii = 1; ii < data.get(i).size(); ii++) {
                            Log.i(TAG, data.get(i).get(ii) + " ");
                        }
                    }
                    final MonthAdapter adapter = new MonthAdapter(data, Record_Detail.class);
                    runOnUiThread(() -> list.setAdapter(adapter));
                }
            }
        });
    }
}
