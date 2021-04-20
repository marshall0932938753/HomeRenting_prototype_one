package com.example.homerenting_prototype_one.valuation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.adapter.base_adapter.ListAdapter;
import com.example.homerenting_prototype_one.adapter.base_adapter.NoDataAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getEndOfWeek;
import static com.example.homerenting_prototype_one.show.global_function.getMonthStr;
import static com.example.homerenting_prototype_one.show.global_function.getStartOfWeek;
import static com.example.homerenting_prototype_one.show.global_function.getWeek;
import static com.example.homerenting_prototype_one.show.global_function.getwCount;
import static com.example.homerenting_prototype_one.show.global_function.removeNew;
import static com.example.homerenting_prototype_one.show.global_function.setwCount;

public class Valuation extends AppCompatActivity {

    TextView month_text;
    TextView week_text;
    ImageButton lastWeek_btn, nextWeek_btn;
    ListView valuationList;

    ArrayList<String[]> data = new ArrayList<>();
    ListAdapter listAdapter = new ListAdapter(data);

    OkHttpClient okHttpClient = new OkHttpClient();

    String TAG = "Valuation";
    private final String PHP = "/user_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valuation);

        month_text = findViewById(R.id.month_V);
        week_text = findViewById(R.id.week_V);
        lastWeek_btn = findViewById(R.id.lastWeek_btn_V);
        nextWeek_btn = findViewById(R.id.nextWeek_btn_V);
        valuationList = findViewById(R.id.valuation_listView_V);

        Button booking_btn = findViewById(R.id.bookingEvaluation_btn);
        Button matchMaking_btn = findViewById(R.id.matchMaking_Evaluation_btn);
        Button cancel_btn = findViewById(R.id.cancelEvaluation_btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

//        setwCount(0);
        week_text.setText(getWeek());
        month_text.setText(getMonthStr());
        getValuation();

        lastWeek_btn.setOnClickListener(v -> {
            int wCount = getwCount();
            setwCount(wCount-1);
            week_text.setText(getWeek());
            month_text.setText(getMonthStr());
            data.clear();
            getValuation();
        });

        nextWeek_btn.setOnClickListener(v -> {
            int wCount = getwCount();
            setwCount(wCount+1);
            week_text.setText(getWeek());
            month_text.setText(getMonthStr());
            data.clear();
            getValuation();
        });

        //上方nav
        booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookingValuation_intent = new Intent(Valuation.this, Valuation_Booking.class);
                startActivity(bookingValuation_intent);
            }
        });
        matchMaking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent matchMakingValuation_intent = new Intent(Valuation.this, Valuation_MatchMaking.class);
                startActivity(matchMakingValuation_intent);
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelValuation_intent = new Intent(Valuation.this, Valuation_Cancel.class);
                startActivity(cancelValuation_intent);
            }
        });

        //底下nav
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent order_intent = new Intent(Valuation.this, Order.class);
                startActivity(order_intent);
            }
        });
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calender_intent = new Intent(Valuation.this, Calendar.class);
                startActivity(calender_intent);
            }
        });
        system_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent system_intent = new Intent(Valuation.this, System.class);
                startActivity(system_intent);
            }
        });
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_intent = new Intent(Valuation.this, Setting.class);
                startActivity(setting_intent);
            }
        });
    }

    private void getValuation(){
        //傳至網頁的值，傳function_name
        String function_name = "self_valuation_member";
        String startDate =  getStartOfWeek();
        String endDate = getEndOfWeek();
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id",company_id)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .build();
        Log.i(TAG, "getOrder: company_id: "+company_id+", startDate:"+startDate+", endDate:"+endDate+", status:"+"self");

        //連線要求
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + PHP)
                .post(body)
                .build();
        //http://54.166.177.4/user_data.php

        //連線
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            //連線失敗
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //在app畫面上呈現錯誤訊息
                        Toast.makeText(Valuation.this, "Toast onFailure.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            //連線成功
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData: "+responseData); //顯示資料

                try {
                    //轉換成json格式，array或object
                    final JSONArray responseArr = new JSONArray(responseData);

                    //一筆一筆的取JSONArray中的json資料
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject member = responseArr.getJSONObject(i);
                        Log.d(TAG,"member:" + member);

                        //取欄位資料
                        final String order_id = member.getString("order_id");
                        final String name = member.getString("member_name");
                        final String nameTitle;
                        if(member.getString("gender").equals("女")) nameTitle = "小姐";
                        else nameTitle = "先生";
                        final String phone = member.getString("phone");
                        final String contact_address = member.getString("contact_address");
                        String auto = member.getString("auto");
                        final String newicon = member.getString("new");

                        String[] row_data = {order_id, name, nameTitle, phone, contact_address, auto, newicon};
                        data.add(row_data);
                    }
                } catch (JSONException e) { //會到這裡通常表示用錯json格式或網頁的資料不是json格式
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if(responseData.equals("null")){
                            NoDataAdapter noData = new NoDataAdapter();
                            valuationList.setAdapter(noData);
                        }
                        //else Toast.makeText(Valuation.this, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show();
                    });
                }

                //顯示資訊
                if(!responseData.equals("null")){
                    for(int i = 0; i < data.size(); i++)
                        Log.i(TAG, "data: "+ Arrays.toString(data.get(i)));
                    runOnUiThread(() -> {
                        valuationList.setAdapter(listAdapter);
                        valuationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String[] row_data = (String[])parent.getItemAtPosition(position);
                                Log.d(TAG, "row_data: "+ Arrays.toString(row_data));
                                String order_id = row_data[0];

                                Bundle bundle = new Bundle();
                                bundle.putString("order_id", order_id);

                                removeNew(order_id, Valuation.this);

                                Intent intent = new Intent();
                                intent.setClass(Valuation.this, Valuation_Detail.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    });
                }

            }
        });
    }
}
