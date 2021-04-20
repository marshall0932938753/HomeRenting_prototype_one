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

public class Valuation_Cancel extends AppCompatActivity {

    TextView month_text;
    TextView week_text;
    ImageButton lastWeek_btn, nextWeek_btn;
    ListView valuationCancelList;

    ArrayList<String[]> data = new ArrayList<>();
    ListAdapter listAdapter;

    OkHttpClient okHttpClient = new OkHttpClient();

    String TAG = "Valuation_Cancel";
    private final String PHP = "/user_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valuation__cancel);
        month_text = findViewById(R.id.month_VC);
        week_text = findViewById(R.id.week_VC);
        lastWeek_btn = findViewById(R.id.lastWeek_btn_VC);
        nextWeek_btn = findViewById(R.id.nextWeek_btn_VC);
        valuationCancelList = findViewById(R.id.order_listView_VC);

        Button self_btn = findViewById(R.id.selfEvaluation_btn);
        Button booking_btn = findViewById(R.id.bookingEvaluation_btn);
        Button matchMaking_btn = findViewById(R.id.matchMaking_Evaluation_btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);


        week_text.setText(getWeek());
        month_text.setText(getMonthStr());
        getValuationCancel();

        lastWeek_btn.setOnClickListener(v -> {
            int wCount = getwCount();
            setwCount(wCount-1);
            week_text.setText(getWeek());
            month_text.setText(getMonthStr());
            data.clear();
            getValuationCancel();
        });

        nextWeek_btn.setOnClickListener(v -> {
            int wCount = getwCount();
            setwCount(wCount+1);
            week_text.setText(getWeek());
            month_text.setText(getMonthStr());
            data.clear();
            getValuationCancel();
        });

        //上方nav
        self_btn.setOnClickListener(v -> {
            Intent selfValuation_intent = new Intent(Valuation_Cancel.this, Valuation.class);
            startActivity(selfValuation_intent);
        });
        booking_btn.setOnClickListener(v -> {
            Intent bookingValuation_intent = new Intent(Valuation_Cancel.this, Valuation_Booking.class);
            startActivity(bookingValuation_intent);
        });
        matchMaking_btn.setOnClickListener(v -> {
            Intent matchMakingValuation_intent = new Intent(Valuation_Cancel.this, Valuation_MatchMaking.class);
            startActivity(matchMakingValuation_intent);
        });

        //底下nav
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Valuation_Cancel.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Valuation_Cancel.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Valuation_Cancel.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Valuation_Cancel.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    private void getValuationCancel(){
        //將傳至網頁的值
        String function_name = "cancel_valuation_member";
        String startDate =  getStartOfWeek();
        String endDate = getEndOfWeek();
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(this))
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("status", "cancel")
                .build();

        //連線要求
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+PHP)
                .post(body)
                .build();
        //http://54.166.177.4/user_data.php

        //連線
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Valuation_Cancel.this, "連線失敗", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                //Log.d(TAG, "responseData" + responseData);

                try {
                    final JSONArray responseArr = new JSONArray(responseData);
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject member = responseArr.getJSONObject(i);
                        Log.d(TAG, "member: " + member);

                        String order_id = member.getString("order_id");
                        String name = member.getString("member_name");
                        String nameTitle;
                        if(member.getString("gender").equals("女")) nameTitle = "小姐";
                        else nameTitle = "先生";
                        String phone = member.getString("phone");
                        String contact_address = member.getString("contact_address");
                        if(contact_address.equals("null")) contact_address = "";
                        String auto = member.getString("auto");
                        String newicon = member.getString("new");

                        String[] row_data = {order_id, name, nameTitle, phone, contact_address, auto, newicon, "cancel"};
                        data.add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if(responseData.equals("null")){
                            NoDataAdapter noData = new NoDataAdapter();
                            valuationCancelList.setAdapter(noData);
                        }
                        //else Toast.makeText(Valuation_Cancel.this, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show();
                    });
                }

                //顯示資訊
                if(!responseData.equals("null")) {
                    for (int i = 0; i < data.size(); i++)
                        Log.i(TAG, "data: " + Arrays.toString(data.get(i)));
                    listAdapter = new ListAdapter(data);
                    runOnUiThread(() -> {
                        valuationCancelList.setAdapter(listAdapter);
                        valuationCancelList.setOnItemClickListener((parent, view, position, id) -> {
                            String[] row_data = (String[]) parent.getItemAtPosition(position);
                            Log.d(TAG, "row_data: " + Arrays.toString(row_data));
                            String order_id = row_data[0];

                            Bundle bundle = new Bundle();
                            bundle.putString("order_id", order_id);

                            removeNew(order_id, Valuation_Cancel.this);

                            Intent intent = new Intent();
                            intent.setClass(Valuation_Cancel.this, ValuationCancel_Detail.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        });
                    });
                }
            }
        });
    }
}
