package com.example.homerenting_prototype_one.add_order;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.valuation.Valuation;
import com.example.homerenting_prototype_one.valuation.Valuation_Booking;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;

public class Add_Valuation extends AppCompatActivity {

    EditText nameText, cAddressText, phoneText, fromAddressText, toAddressText, noticeText;
    TextView dateText, timeText;
    RadioGroup genderRG;
    Button addBtn;

    String gender = "男";
    String time, time2;

    GregorianCalendar calendar = new GregorianCalendar();

    OkHttpClient okHttpClient = new OkHttpClient();

    Context context = Add_Valuation.this;
    String TAG = "Add_Valuation";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__valuation);

        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        linking();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
        Log.d(TAG, "now: "+now.getYear()+"-"+monthToInt(String.valueOf(now.getMonth()))+"-"+now.getDayOfMonth());

        dateText.setText(now.getYear()+"-"+monthToInt(String.valueOf(now.getMonth()))+"-"+now.getDayOfMonth());
        dateText.setOnClickListener(v -> {
            DatePickerDialog datePicker;
            datePicker = new DatePickerDialog( Add_Valuation.this, (view, year, month, dayOfMonth) -> {
                if(year<now.getYear() || (month+1)<monthToInt(String.valueOf(now.getMonth())) || dayOfMonth<now.getDayOfMonth()) {
                    Toast.makeText(context, "請勿選擇過去的日期", Toast.LENGTH_SHORT);
                    return;
                }
                dateText.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            },calendar.get( GregorianCalendar.YEAR ),calendar.get( GregorianCalendar.MONTH ),calendar.get( GregorianCalendar.DAY_OF_MONTH));
            datePicker.show();
        });

        timeText.setText(now.getHour()+":00");
        timeText.setOnClickListener(v -> {
            TimePickerDialog time_picker = new TimePickerDialog( context, (view, hourOfDay, minute) -> {
                timeText.setText(hourOfDay+":"+minute);
                time = hourOfDay+":"+minute;
                time2 = (hourOfDay+1)+":"+minute;
            },calendar.get(GregorianCalendar.DAY_OF_MONTH ),calendar.get(GregorianCalendar.MINUTE ),true);
            time_picker.show();
        });

        cAddressText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String address = cAddressText.getText().toString();
                String moveOut_address = fromAddressText.getText().toString();
                if(!address.equals("") && !moveOut_address.equals("")){
                    if(address.substring(0, 1).equals(moveOut_address.substring(0, 1)))
                        fromAddressText.setText(address);
                }
                else if(!address.equals("") || moveOut_address.length()<=1){
                    fromAddressText.setText(address);
                }
            }
        });

        addBtn.setOnClickListener(v -> {
            String name = nameText.getText().toString();
            String cAddress = cAddressText.getText().toString();
            String phone = phoneText.getText().toString();
            String fromAddress = fromAddressText.getText().toString();
            String toAddress = toAddressText.getText().toString();
            String notice = noticeText.getText().toString();
            String date = dateText.getText().toString();
            String valuation_time = time+"~"+time2;

            switch(genderRG.getCheckedRadioButtonId()){
                case R.id.male_rbtn_AV:
                    gender = "男";
                    break;
                case R.id.female_rbtn_AV:
                    gender = "女";
                    break;
                case R.id.other_rbtn_AV:
                    gender = "其他";
                    break;
            }
            Log.i(TAG, "gender = "+gender);

            String function_name = "add_valuation";
            String company_id = getCompany_id(context);
            RequestBody body = new FormBody.Builder()
                    .add("function_name", function_name)
                    .add("company_id", company_id)
                    .add("member_name", name)
                    .add("gender", gender)
                    .add("contact_address", cAddress)
                    .add("phone", phone)
                    .add("from_address", fromAddress)
                    .add("to_address", toAddress)
                    .add("additional", notice)
                    .add("valuation_date", date)
                    .add("valuation_time", valuation_time)
                    .build();
            Log.i(TAG,"function_name: " + function_name +
                    ", member_name: " + name +
                    ", gender: " + gender +
                    ", contact_address: " + cAddress +
                    ", phone: " + phone +
                    ", from_address: " + fromAddress+
                    ", to_address: " + toAddress +
                    ", additional: " + notice +
                    ", valuation_date: " + date +
                    ", valuation_time: " + valuation_time);

            Request request = new Request.Builder()
                    .url(BuildConfig.SERVER_URL+"/functional.php")
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        if(responseData.equals("success"))
                            Toast.makeText(context, "新增估價單成功", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, "新增估價單失敗", Toast.LENGTH_LONG).show();
                    });
                    Log.d(TAG, "add_btn, responseData: " + responseData);
                }
            });


            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent finish_valuation_intent = new Intent();
                finish_valuation_intent.setClass(context, Valuation_Booking.class);
                startActivity(finish_valuation_intent);
            }, 1000);
        });

        //底下nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Add_Valuation.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Add_Valuation.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Add_Valuation.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Add_Valuation.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Add_Valuation.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    private int monthToInt(String month){
        switch (month){
            case "JANUARY":
                return 1;
            case "FEBRUARY":
                return 2;
            case "MARCH":
                return 3;
            case "APRIL":
                return 4;
            case "MAY":
                return 5;
            case "JUNE":
                return 6;
            case "JULY":
                return 7;
            case "AUGUST":
                return 8;
            case "SEPTEMBER":
                return 9;
            case "OCTOBER":
                return 10;
            case "NOVEMBER":
                return 11;
            case "DECEMBER":
                return 12;
            default:
                return 0;
        }
    }

    private void linking(){
        nameText = findViewById(R.id.name_AV);
        cAddressText = findViewById(R.id.contactAddress_AV);
        phoneText = findViewById(R.id.phone_AV);
        fromAddressText = findViewById(R.id.fromAddress_AV);
        toAddressText = findViewById(R.id.toAddress_AV);
        noticeText = findViewById(R.id.notice_editText_AV);
        dateText = findViewById(R.id.pickDate_AV);
        timeText = findViewById(R.id.pictTime_AV);
        genderRG = findViewById(R.id.genderRG_AV);
        addBtn = findViewById(R.id.add_valuation_btn_AV);
    }
}
