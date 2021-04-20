package com.example.homerenting_prototype_one.bouns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.base_adapter.MonthAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
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
import static com.example.homerenting_prototype_one.show.global_function.getDay;
import static com.example.homerenting_prototype_one.show.global_function.getMonth;
import static com.example.homerenting_prototype_one.show.global_function.getToday;
import static com.example.homerenting_prototype_one.show.global_function.getYear;

public class Bonus_List extends AppCompatActivity {
    TextView month, totalbonus;
    ListView bonus_list;
    private ArrayList<ArrayList<String>> data;

    String TAG = "Bonus_List";

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus__list);

        month = findViewById(R.id.monthdate_BL);
        totalbonus = findViewById(R.id.total_bonus_BL);
        bonus_list = findViewById( R.id.year_bonus_list_BL);
        ImageButton back_btn = findViewById(R.id.back_imgBtn);

        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        data = new ArrayList<>();

        String today = getToday("yyyy-MM-dd");
        month.setText("至"+getYear(today)+"年"+getMonth(today)+"月"+getDay(today)+"日");

        getOrder();

        back_btn.setOnClickListener(v -> finish());
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Bonus_List.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Bonus_List.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Bonus_List.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Bonus_List.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Bonus_List.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    private void getOrder(){
        String function_name = "all_order_date";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", company_id)
                .add("order_status", "paid")
                .build();
        //連線要求
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "/user_data.php")
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                //在app畫面上呈現錯誤訊息
                runOnUiThread(() -> Toast.makeText(context, "連線失敗", Toast.LENGTH_LONG).show());
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
                                if(!data.get(ii).contains(month)) data.get(ii).add(month);
                                break;
                            }
                        }
                        if(!check){
                            data.add(new ArrayList<String>());
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
                    int totalmonth = 0;
                    Log.i(TAG, "data: ");
                    for (int i = 0; i < data.size(); i++) {
                        Log.i(TAG, "(" + data.get(i).get(0) + ")");
                        for (int ii = 1; ii < data.get(i).size(); ii++) {
                            Log.i(TAG, data.get(i).get(ii) + " ");
                            totalmonth = totalmonth+1;
                        }
                    }

                    final MonthAdapter adapter = new MonthAdapter(data, Bonus_List_Detail.class);
                    final int finalTotalmonth = totalmonth;
                    runOnUiThread(() -> {
                        bonus_list.setAdapter(adapter);
                        totalbonus.setText("共"+ finalTotalmonth +"個月獎金報表");
                    });
                }
            }
        });
    }
}
