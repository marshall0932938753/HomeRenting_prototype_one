package com.example.homerenting_prototype_one.valuation;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.furniture.Furniture_Detail;
import com.example.homerenting_prototype_one.order.Order;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import static com.example.homerenting_prototype_one.show.global_function.getDate;

public class Valuation_Detail extends AppCompatActivity {
    TextView nameText, nameTitleText, phoneText, selfValTimeText, fromAddressText, toAddressText;
    TextView contactTimeText, cusValTimeText, noticeText, sysValPriceText, valPriceText;

    EditText pickDate_edit, pickTime_edit, pickTime2_edit;

    Button furniture_btn, check_date_btn, phoneCall_btn;

    String name, gender, nameTitle, phone, selfValTime, fromAddress, toAddress;
    String contactTime, valTime, notice;
    String sysValPrice, valPrice;

    String TAG = "Valuation_Detail";

    final OkHttpClient okHttpClient = new OkHttpClient();

    Context context = this;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valuation__detail);
        phoneCall_btn = findViewById(R.id.call_btn);
        final GregorianCalendar calendar = new GregorianCalendar();

        final Bundle bundle = getIntent().getExtras();
        final String order_id = bundle.getString("order_id");
        Log.d(TAG, "order_id: " + order_id);

        linking();

        //將傳至網頁的值
        String function_name = "valuation_detail";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id",company_id)
                .build();

        //連線要求
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/user_data.php")
                .post(body)
                .build();

        //final ProgressDialog dialog = ProgressDialog.show(this,"讀取中","請稍候",true);

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                runOnUiThread(() -> Toast.makeText(Valuation_Detail.this, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                //Log.d(TAG,"responseData: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);
                    JSONObject order = responseArr.getJSONObject(0);
                    Log.d(TAG,"order:" + order);

                    name = order.getString("member_name");
                    gender = order.getString("gender");
                    if(gender.equals("女")) nameTitle= "小姐";
                    else if(gender.equals("男")) nameTitle = "先生";
                    else nameTitle = "";
                    phone = order.getString("phone");
                    selfValTime = order.getString("last_update");
                    fromAddress = order.getString("from_address");
                    toAddress = order.getString("to_address");
                    contactTime = order.getString("contact_time");
                    if(!order.getString("valuation_date").equals("null")){
                        valTime = getDate(order.getString("valuation_date"));
                        valTime = getDate(order.getString("valuation_date"));
                        if(!order.getString("valuation_time").equals("null"))
                            valTime = valTime+" "+order.getString("valuation_time");
                    }
                    else valTime = "無偏好時間";
                    notice = order.getString("additional");

                    runOnUiThread(() -> {
                        //dialog.dismiss();
                        nameText.setText(name);
                        nameTitleText.setText(nameTitle);
                        phoneText.setText(phone);
                        selfValTimeText.setText(selfValTime);
                        fromAddressText.setText(fromAddress);
                        toAddressText.setText(toAddress);
                        contactTimeText.setText(contactTime);
                        cusValTimeText.setText(valTime);
                        noticeText.setText(notice);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
//                    runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show());
                }
            }
        });

        furniture_btn.setOnClickListener(v -> {
            Intent detail_intent = new Intent(  );
            detail_intent.setClass( Valuation_Detail.this, Furniture_Detail.class );
            detail_intent.putExtras(bundle);
            startActivity( detail_intent );
        });

        pickDate_edit.setOnClickListener(v -> {
            DatePickerDialog date_picker = new DatePickerDialog( Valuation_Detail.this, (view, year, month, dayOfMonth) -> {
                pickDate_edit.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            },calendar.get( GregorianCalendar.YEAR ),calendar.get( GregorianCalendar.MONTH ),calendar.get( GregorianCalendar.DAY_OF_MONTH));
            date_picker.getDatePicker().setMinDate(new Date().getTime());
            date_picker.show();
        });

        pickTime_edit.setOnClickListener(v -> {
            TimePickerDialog time_picker = new TimePickerDialog( Valuation_Detail.this, (view, hourOfDay, minute) -> {
                String m = String.valueOf(minute);
                if(minute == 0) m = "00";
                pickTime_edit.setText(hourOfDay+":"+m);
            },calendar.get(GregorianCalendar.DAY_OF_MONTH ),calendar.get(GregorianCalendar.MINUTE ),true);
            time_picker.show();
        });

        pickTime2_edit.setOnClickListener(v -> {
            TimePickerDialog time_picker = new TimePickerDialog( Valuation_Detail.this, (view, hourOfDay, minute) -> {
                String m = String.valueOf(minute);
                if(minute < 10) m = "0"+minute;
                pickTime2_edit.setText(hourOfDay+":"+m);
            },calendar.get(GregorianCalendar.DAY_OF_MONTH ),calendar.get(GregorianCalendar.MINUTE ),true);
            time_picker.show();
        });

        check_date_btn.setOnClickListener(v -> {
            String valDate = pickDate_edit.getText().toString();
            String valTime = pickTime_edit.getText().toString()+"~"+pickTime2_edit.getText().toString();
            boolean check = true;
            if(valDate.isEmpty()){
                pickDate_edit.setError("請輸入日期");
                check = false;
            }
            if(pickTime_edit.getText().toString().isEmpty()){
                pickTime_edit.setError("請輸入時間");
                check = false;
            }
            if(pickTime2_edit.getText().toString().isEmpty()){
                pickTime2_edit.setError("請輸入時間");
                check = false;
            }
            if(!check) return;

            String function_name1 = "update_selfValuation";
            RequestBody body1 = new FormBody.Builder()
                    .add("function_name", function_name1)
                    .add("order_id", order_id)
                    .add("company_id", getCompany_id(context))
                    .add("valuation_date", valDate)
                    .add("valuation_time", valTime)
                    .build();
            Log.d(TAG,"check_price_btn: order_id: " + order_id +
                    ", company_id: " + getCompany_id(context) +
                    ", valuation_date: " + valDate +
                    ", valuation_time: " + valTime);

            Request request1 = new Request.Builder()
                    .url(BuildConfig.SERVER_URL+"/functional.php")
                    .post(body1)
                    .build();

            Call call1 = okHttpClient.newCall(request1);
            call1.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call1, @NotNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(@NotNull Call call1, @NotNull Response response) throws IOException {
                    final String responseData = response.body().string();
                        runOnUiThread(() -> {
                            if(responseData.equals("success")) Toast.makeText(context, "線上估價成功", Toast.LENGTH_LONG).show();
                            else Toast.makeText(context, "上傳失敗", Toast.LENGTH_LONG).show();
                        });
                    Log.d(TAG, "check_price_btn, responseData: " + responseData);
                }
            });

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent = new Intent(context, Valuation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }, 1000);
        });

        setPhoneBtn();

        globalNav();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setPhoneBtn(){
        phoneCall_btn.setOnClickListener(v -> {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
            Log.i(TAG, "現在是"+now);
            if(isContactTime(timeToStr(
                    isWeekend(String.valueOf(now.getDayOfWeek())),
                    isNight(now.getHour())))){
                callIntent();
            }
            else{
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("聯絡時間");
                dialog.setMessage("現在並非客戶偏好的聯絡時間，確定要繼續前往撥電話畫面？");
                dialog.setPositiveButton("確定", (dialog1, which) -> callIntent());
                dialog.setNegativeButton("取消", null);
                dialog.create().show();
            }
        });
    }

    private boolean isWeekend(String dayOfWeek){
        switch (dayOfWeek){
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
                return false;
            case "SUNDAY":
            case "SATURDAY":
                return true;
            default:
                Log.i(TAG, "the today dayOfWeek error");
        }
        return false;
    }

    private boolean isNight(int hour){
        return (hour < 6 || hour > 19);
    }

    private String timeToStr(boolean isWeekend, boolean isNight){
        if(isWeekend && isNight) return "假日晚上";
        if(!isWeekend && isNight) return "平日晚上";
        if(isWeekend) return "假日白天";
        return "平日白天";
    }

    private boolean isContactTime(String currentTime){
        Log.i(TAG, "現在是"+currentTime);
        String[] token = contactTime.split(",");
        for (String ct : token) {
            if (ct.equals(currentTime))
                return true;
        }
        return false;
    }

    private void callIntent(){
        Intent call_intent = new Intent(Intent.ACTION_DIAL);
        call_intent.setData(Uri.parse("tel:"+phone));
        startActivity(call_intent);
    }

    public void linking(){
        nameText = findViewById(R.id.name_VD);
        nameTitleText = findViewById(R.id.nameTitle_VD);
        phoneText = findViewById(R.id.phone_VD);
        selfValTimeText = findViewById(R.id.selfValTime_VD);
        fromAddressText = findViewById(R.id.FromAddress_VD);
        toAddressText = findViewById(R.id.ToAddress_VD);
        furniture_btn = findViewById(R.id.furniture_btn_VD);
        contactTimeText = findViewById(R.id.contactTime_VD);
        cusValTimeText = findViewById(R.id.cusValTime_VD);
        noticeText = findViewById(R.id.notice_VD);
        pickDate_edit = findViewById(R.id.pickDate_editText);
        pickTime_edit = findViewById(R.id.pickTime_editText);
        pickTime2_edit = findViewById(R.id.pickTime2_editText);
        check_date_btn = findViewById(R.id.check_date_btn_VD);
        sysValPriceText = findViewById(R.id.sysValPrice_VD);
        valPriceText = findViewById(R.id.valPrice_VD);
    }

    private void globalNav() {
        ImageView back_btn = findViewById(R.id.back_imgBtn);
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        back_btn.setOnClickListener(v -> finish());

        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Valuation_Detail.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Valuation_Detail.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Valuation_Detail.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Valuation_Detail.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Valuation_Detail.this, Setting.class);
            startActivity(setting_intent);
        });
    }
}
