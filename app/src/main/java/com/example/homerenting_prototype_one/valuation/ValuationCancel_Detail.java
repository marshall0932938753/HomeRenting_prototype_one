package com.example.homerenting_prototype_one.valuation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.furniture.Furniture_Detail;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.order.Order;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getDate;
import static com.example.homerenting_prototype_one.show.global_function.getTime;

public class ValuationCancel_Detail extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient();
    Bundle bundle;

    TextView nameText, nameTitleText, phoneText, valuationtimeText, movingTimeText, fromAddressText, toAddressText;
    TextView remainderText, movedateText, carText, worktimeText, feeText;

    String name, gender, phone, valuationtime, movingTime, fromAddress, toAddress;
    String remainder, movedate, car, worktime, fee;

    String TAG = "Valuation_Cancel_Detail";
    private final String PHP = "/user_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_valuation_cancel__detail );

        bundle = getIntent().getExtras();
        String order_id = bundle.getString("order_id");
        Log.d(TAG, "order_id: " + order_id);

        linking();

        String function_name = "valuation_detail";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id",company_id)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+PHP)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                //在app畫面上呈現錯誤訊息
                runOnUiThread(() -> Toast.makeText(ValuationCancel_Detail.this, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData: " + responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);
                    JSONObject order = responseArr.getJSONObject(0);
                    name = order.getString("member_name");
                    gender = order.getString("gender");
                    phone = order.getString("phone");
                    if(!order.getString("valuation_date").equals("null"))
                        valuationtime = getDate(order.getString("valuation_date"));
                    else valuationtime = "";
                    if(order.getString("valuation_time").equals("null"))
                        valuationtime = valuationtime+" "+order.getString("valuation_time");
                    if(!order.getString("moving_date").equals("null"))
                        movingTime = getDate(order.getString("moving_date")) + " " + getTime(order.getString("moving_date"));
                    else movingTime = "";
                    fromAddress = order.getString("from_address");
                    toAddress = order.getString("to_address");
                    remainder = order.getString("additional");
                    movedate = order.getString("moving_date");
                    worktime = order.getString("estimate_worktime");
                    fee = order.getString("estimate_fee");

                    runOnUiThread(() -> {
                        nameText.setText(name);
                        if(gender.equals("female")) nameTitleText.setText("小姐");
                        else if(gender.equals("male")) nameTitleText.setText("先生");
                        else nameTitleText.setText("");
                        phoneText.setText(phone);
                        movingTimeText.setText(movingTime);
                        fromAddressText.setText(fromAddress);
                        toAddressText.setText(toAddress);
                        remainderText.setText(remainder);
                        worktimeText.setText(worktime);
                        feeText.setText(fee);
//                        carText.setText(car);
                        valuationtimeText.setText(valuationtime);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
//                    runOnUiThread(() -> Toast.makeText(ValuationCancel_Detail.this, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show());
                }
            }
        });





        //返回鍵
        ImageView back_btn = findViewById( R.id.back_imgBtn );
        back_btn.setOnClickListener(v -> finish());

        //家具清單
        Button detail_btn = findViewById(R.id.furniture_btn_CD);
        detail_btn.setOnClickListener(v -> {
            Intent detail_intent = new Intent();
            detail_intent.setClass( ValuationCancel_Detail.this, Furniture_Detail.class );
            bundle.putString( "key","cancel" );
            detail_intent.putExtras( bundle );
            startActivity( detail_intent );
        });

        //底下nav
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(ValuationCancel_Detail.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(ValuationCancel_Detail.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(ValuationCancel_Detail.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(ValuationCancel_Detail.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(ValuationCancel_Detail.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    public void linking(){
        nameText = findViewById(R.id.name_CD);
        nameTitleText = findViewById(R.id.nameTitle_CD);
        phoneText = findViewById(R.id.phone_CD);
        valuationtimeText =findViewById(R.id.valuationTime_CD);
        movingTimeText = findViewById(R.id.movingTime_CD);
        fromAddressText = findViewById(R.id.FromAddress_CD);
        toAddressText = findViewById(R.id.ToAddress_CD);
        remainderText = findViewById(R.id.remainder_CD);
        movedateText = findViewById(R.id.movedate_CD);
        carText = findViewById(R.id.car_CD);
        worktimeText = findViewById(R.id.worktime_CD);
        feeText = findViewById(R.id.price_CD);
    }
}
