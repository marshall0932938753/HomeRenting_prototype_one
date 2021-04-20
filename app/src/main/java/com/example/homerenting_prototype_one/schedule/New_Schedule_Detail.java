package com.example.homerenting_prototype_one.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.order.Order_Detail;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.valuation.Valuation;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getDatalist;
import static com.example.homerenting_prototype_one.show.global_function.getDate;
import static com.example.homerenting_prototype_one.show.global_function.getTime;
import static com.example.homerenting_prototype_one.show.global_function.getlastDatalist;
import static com.example.homerenting_prototype_one.show.global_function.getnextDatalist;

public class New_Schedule_Detail extends AppCompatActivity {
    Bundle bundle;
    String order_id;

    TextView titleText, nameText, nameTitleText, movingDateText, fromAddressText, toAddressText, demandCarText;
    TextView staffText, carText;
    ChipGroup staffGroup,  carGroup;
    Chip chip1;
    ImageButton backBtn, lastBtn, nextBtn;
    Button submitBtn;

    String name, nameTitle, movingDate, fromAddress, toAddress, demandCar;
    String staff, car;

    boolean lock;
    int overlap_counter_s = 0, overlap_counter_c = 0;

    ArrayList<String> staffs_text, cars_text, staffs_vacation, cars_vacation, staffs_lap, cars_lap, new_staffs_lap, new_cars_lap;
    ArrayList<Integer> staffs, cars, staffs_v, cars_v;
    ArrayList<int[]> staffs_l, cars_l, new_staffs_l, new_cars_l;

    Context context = this;

    String TAG = "New_Schedule_Detail";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule_detail);

        lock = false;
        initArray();

        bundle = getIntent().getExtras();
        order_id = bundle.getString("order_id");

        linking(); //將xml裡的元件連至此java

        getChip();
        getOrder();

        ArrayList<String> datalist = getDatalist();
        titleText.setText("人車派遣 "+(datalist.indexOf(order_id)+1)+"/"+datalist.size());

        backBtn.setOnClickListener(v -> finish());

        lastBtn.setOnClickListener(v -> {
            String new_order_id = getlastDatalist(order_id);
            bundle.putString("order_id", new_order_id);
            if(new_order_id == null)
                Toast.makeText(context, "This is the first order.", Toast.LENGTH_LONG).show();
            else{
                submit();
                Intent intent = new Intent(context, New_Schedule_Detail.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(v -> {
            String new_order_id = getnextDatalist(order_id);
            bundle.putString("order_id", new_order_id);
            if(new_order_id == null)
                Toast.makeText(context, "This is the final order.", Toast.LENGTH_LONG).show();
            else{
                submit();
                Intent intent = new Intent(context, New_Schedule_Detail.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        submitBtn.setOnClickListener(v -> submit());

        globalNav();
    }

    private void getChip(){
        Log.d(TAG, "start getChip()");
        lock = true;

        String function_name = "staff-vehicle_data";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/user_data.php")
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                lock = false;
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                //在app畫面上呈現錯誤訊息
                runOnUiThread(() -> Toast.makeText(context, "連線失敗", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of getChip: "+responseData); //顯示資料

                try {
                    //轉換成json格式，array或object
                    final JSONArray responseArr = new JSONArray(responseData);
                    //Log.i(TAG,"responseObj: "+ responseArr);

                    //一筆一筆的取JSONArray中的json資料
                    int i;
                    for (i = 0; i < responseArr.length(); i++) {
                        JSONObject staff = responseArr.getJSONObject(i);
                        if(!staff.has("staff_id")) break;
                        Log.i(TAG, "staff: " + staff);

                        //取欄位資料
                        final String staff_id = staff.getString("staff_id");
                        final String staff_name = staff.getString("staff_name");

                        //在staffGroup底下新增chip，加入ID和Tag
                        runOnUiThread(() -> staffGroup.addView(setChipDetail(staffGroup, staff_id, staff_name)));
                    }

                    for (; i < responseArr.length(); i++) {
                        JSONObject vehicle = responseArr.getJSONObject(i);
                        if(!vehicle.has("vehicle_id")) break;
                        Log.i(TAG, "vehicle: " + vehicle);

                        //取欄位資料
                        final String vehicle_id = vehicle.getString("vehicle_id");
                        final String plate_num = vehicle.getString("plate_num");
                        final String vehicle_weight = vehicle.getString("vehicle_weight");
                        final String vehicle_type = vehicle.getString("vehicle_type");

                        runOnUiThread(() -> {
                            carGroup.addView(setChipDetail(carGroup, vehicle_id, plate_num));
                            Chip carChip = (Chip) carGroup.getChildAt(carGroup.getChildCount()-1);
                        });
                    }
                    int ii = 0;
                    while((staffGroup.getChildCount()-1+carGroup.getChildCount()-1) != responseArr.length()){
                        if((++ii)%1000000 == 0)
                            Log.d(TAG, "waiting in getChip(): staffGroup:"+staffGroup.getChildCount()+", carGroup:"+carGroup.getChildCount());
                    }
                    Log.d(TAG, "waiting in getChip() final: staffGroup:"+staffGroup.getChildCount()+", carGroup:"+carGroup.getChildCount());
                    lock = false;
                } catch (JSONException e) {
                    lock = false;
                    e.printStackTrace();
                    if(!responseData.equals("null") && !responseData.equals("function_name not found."))
                        runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON in getChip", Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private Chip setChipDetail(ChipGroup chipGroup, String id, String name){
        final Chip chip = new Chip(chipGroup.getContext());
        chip.setId(Integer.parseInt(id));
        chip.setTag(Integer.parseInt(id));

        chip.setText(name); //顯示員工姓名
        chip.setCheckable(true); //可點擊

        chip.setTextSize(16); //文字的大小

        //選擇chip的模式:choice
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(chipGroup.getContext(), null, 0 ,R.style.Widget_MaterialComponents_Chip_Choice);
        chip.setChipDrawable(chipDrawable);

        //將邊框設為方形圓角
        chip.setShapeAppearanceModel(chip1.getShapeAppearanceModel());

        if(chipGroup == staffGroup) setChipCheckedListener(chip, name, staffs, staffs_text);
        else setChipCheckedListener(chip, name, cars, cars_text);

        return chip;
    }

    private void setChipCheckedListener(final Chip chip, final String name, final ArrayList<Integer> items, final ArrayList<String> items_text){
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int tag = (int) buttonView.getTag();
            String sname = chip.getText().toString();
            if(isChecked){ //加入選擇
                if(!items_text.contains(name)){
                    items.add(tag);
                    items_text.add(sname);
                }
            }
            else{ //取消選擇
                items.remove(Integer.valueOf(tag));
                items_text.remove(sname);
            }

            if(items == staffs) setStaffText();
            else setCarText();
        });
    }

    private void setStaffText(){
        staff = "";
        for(int i = 0; i < staffs_text.size(); i++)
            staff = staff + staffs_text.get(i) + " ";
        staffText.setText(staff);
    }

    private void setCarText(){
        car = "";
        for(int i = 0; i < cars_text.size(); i++)
            car = car + cars_text.get(i) + " ";
        carText.setText(car);
    }

    private void getOrder(){
        Log.d(TAG, "start getOrder()");

        String function_name = "order_detail";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id", getCompany_id(context))
                .add("assign", "true")
                .build();
        Log.i(TAG, "order_id: "+order_id+", company_id: "+getCompany_id(context)+", assign: true");

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/user_data.php")
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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of getOrder: "+responseData); //顯示資料

                String movingDateWithoutTime = null;
                String datetime = null;
                String endtime = "";
                try {
                    JSONArray responseArr = new JSONArray(responseData);
                    JSONObject order = responseArr.getJSONObject(0);
                    Log.i(TAG,"JSONObject of order:"+order);

                    //取得資料
                    name = order.getString("member_name");
                    String gender = order.getString("gender");
                    if(gender.equals("female")) nameTitle = "小姐";
                    else if(gender.equals("male")) nameTitle = "先生";
                    else nameTitle = "客戶";
                    movingDate = getDate(order.getString("moving_date"))+" "+getTime(order.getString("moving_date"));
                    fromAddress = order.getString("from_address");
                    toAddress = order.getString("to_address");

                    datetime= order.getString("moving_date");
                    String[] date = datetime.split(" ");
                    movingDateWithoutTime = date[0];

                    int estimate_worktime = Integer.parseInt(order.getString("estimate_worktime"));
                    LocalDateTime localDate = LocalDateTime.parse(date[0]+"T"+date[1]);
                    localDate = localDate.plusHours(estimate_worktime);
                    Log.d(TAG, "endtime 1: "+localDate);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    endtime = localDate.format(formatter);
                    Log.d(TAG, "endtime 2: "+endtime);

                    int i;
                    demandCar = "";
                    for (i = 1; i < responseArr.length(); i++) {
                        JSONObject vehicle_demand = responseArr.getJSONObject(i);
                        if(!vehicle_demand.has("num")) break;
                        Log.i(TAG, "vehicle_demand:" + vehicle_demand);
                        if(i != 1) demandCar = demandCar + "\n";
                        demandCar = demandCar+vehicle_demand.getString("vehicle_weight")+"噸"
                                +vehicle_demand.getString("vehicle_type")
                                +vehicle_demand.getString("num")+"輛";
                    }
                    if(i == 1) demandCar = "無填寫需求車輛";
                    Log.d(TAG, "demandCar: "+demandCar);

                    car = "";
                    if(responseArr.length()-i < 1) car = "尚未安排車輛";
                    for (; i < responseArr.length(); i++) {
                        JSONObject vehicle_assign = responseArr.getJSONObject(i);
                        if(!vehicle_assign.has("vehicle_id")) break;
                        Log.i(TAG, "vehicle_assign:" + vehicle_assign);
                        car = car+vehicle_assign.getString("plate_num")+" ";
                        cars_text.add(vehicle_assign.getString("plate_num"));
                        cars.add(Integer.parseInt(vehicle_assign.getString("vehicle_id")));
                    }
                    Log.d(TAG, "car: "+car);

                    if(responseArr.length()-i < 1) staff = "尚未安排人員";
                    else staff = "";
                    for (; i < responseArr.length(); i++) {
                        JSONObject staff_assign = responseArr.getJSONObject(i);
                        if(!staff_assign.has("staff_id")) break;
                        Log.i(TAG, "staff_assign:" + staff_assign);
                        staff = staff+staff_assign.getString("staff_name")+" ";
                        staffs_text.add(staff_assign.getString("staff_name"));
                        staffs.add(Integer.parseInt(staff_assign.getString("staff_id")));
                    }
                    Log.d(TAG, "staff:"+staff);

                    //顯示資料
                    runOnUiThread(() -> {
                        nameText.setText(name);
                        nameTitleText.setText(nameTitle);
                        movingDateText.setText(movingDate);
                        fromAddressText.setText(fromAddress);
                        toAddressText.setText(toAddress);
                        demandCarText.setText(demandCar);
                        staffText.setText(staff);
                        carText.setText(car);
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                    if(!responseData.equals("null") && !responseData.equals("function_name not found.")) {
                        runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON in getOrder", Toast.LENGTH_LONG).show());
                    }
                }

                int ii = 0;
                while (lock){
                    if((++ii)%5000000 == 0) Log.d(TAG, "waiting for lock in getOrder...");
                }
                Log.d(TAG, "getOrder: staffGroup:"+staffGroup.getChildCount()+", carGroup:"+carGroup.getChildCount());
                getVacation(movingDateWithoutTime);
                getOverlap(datetime, endtime);
                setChipCheck(staffGroup, staffs_text, "assign");
                setChipCheck(carGroup, cars_text, "assign");
            }
        });
    }

    private void getVacation(String date){
        if(date == null){
            Log.d(TAG, "date is null in getVacation");
            return;
        }

        Log.d(TAG, "start getVacation()");

        String function_name = "all_vehicle_staff_leave";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .add("date", date)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/user_data.php")
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
                runOnUiThread(() -> Toast.makeText(context, "連線錯誤", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of getVocation: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);

                    int i;
                    for (i = 0; i < responseArr.length(); i++) {
                        JSONObject vehicle_vacation = responseArr.getJSONObject(i);
                        if(!vehicle_vacation.has("vehicle_id")) break;
                        Log.i(TAG, "vehicle_vacation:" + vehicle_vacation);
                        cars_vacation.add(vehicle_vacation.getString("plate_num"));
                        cars_v.add(Integer.parseInt(vehicle_vacation.getString("vehicle_id")));
                    }

                    for (; i < responseArr.length(); i++) {
                        JSONObject staff_vacation = responseArr.getJSONObject(i);
                        if(!staff_vacation.has("staff_id")) break;
                        Log.i(TAG, "staff_vacation:" + staff_vacation);
                        staffs_vacation.add(staff_vacation.getString("staff_name"));
                        staffs_v.add(Integer.parseInt(staff_vacation.getString("staff_id")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(!responseData.equals("null") && !responseData.equals("function_name not found.")) {
                        runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON in getVacation", Toast.LENGTH_LONG).show());
                    }
                }

                int ii = 0;
                while (lock){
                    if((++ii)%1000000 == 0) Log.d(TAG, "waiting for lock in getVacation...");
                }
                Log.d(TAG, "getVacation: staffGroup:"+staffGroup.getChildCount()+", carGroup:"+carGroup.getChildCount());
                setChipCheck(staffGroup, staffs_vacation, "vacation");
                setChipCheck(carGroup, cars_vacation, "vacation");
            }
        });
    }

    private void getOverlap(String date, String endtime){
        if(date == null){
            Log.d(TAG, "date is null in getOvelap");
            return;
        }

        Log.d(TAG, "start getOverlap()");

        String function_name = "overlap_order";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .add("datetime", date)
                .add("endtime", endtime)
                .build();
        Log.d(TAG, "overlap_order. datetime: "+date+", endtime: "+endtime);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/user_data.php")
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
                runOnUiThread(() -> Toast.makeText(context, "連線錯誤", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of getOverlap: "+responseData); //顯示資料

                if(!responseData.equals("no data")){
                    try {
                        JSONArray responseArr = new JSONArray(responseData);

                        int i;
                        for (i = 0; i < responseArr.length(); i++) {
                            JSONObject vehicle_overlap = responseArr.getJSONObject(i);
                            if(!vehicle_overlap.has("vehicle_id")) break;
                            if(vehicle_overlap.getString("order_id").equals(order_id)) continue;
                            Log.i(TAG, "vehicle_overlap:" + vehicle_overlap);
                            if(vehicle_overlap.getString("vehicle_id").equals("null")) break;
                            cars_lap.add(vehicle_overlap.getString("plate_num"));
                            int[] row_data = {Integer.parseInt(vehicle_overlap.getString("order_id")), Integer.parseInt(vehicle_overlap.getString("vehicle_id"))};
                            cars_l.add(row_data);
                        }

                        for (; i < responseArr.length(); i++) {
                            JSONObject staff_overlap = responseArr.getJSONObject(i);
                            if(!staff_overlap.has("staff_id")) break;
                            if(staff_overlap.getString("order_id").equals(order_id)) continue;
                            Log.i(TAG, "staff_overlap:" + staff_overlap);
                            if(staff_overlap.getString("staff_id").equals("null")) break;
                            staffs_lap.add(staff_overlap.getString("staff_name"));
                            int[] row_data = {Integer.parseInt(staff_overlap.getString("order_id")), Integer.parseInt(staff_overlap.getString("staff_id"))};
                            staffs_l.add(row_data);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if(!responseData.equals("null") && !responseData.equals("function_name not found."))
                            runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON in getOverlap", Toast.LENGTH_LONG).show());
                    }
                }

                int ii = 0;
                while (lock){
                    if((++ii)%1000000 == 0) Log.d(TAG, "waiting for lock in getOverlap...");
                }
                Log.d(TAG, "getOverlap: staffGroup:"+staffGroup.getChildCount()+", carGroup:"+carGroup.getChildCount());
                setChipCheck(staffGroup, staffs_lap, "overlap");
                setChipCheck(carGroup, cars_lap, "overlap");
            }
        });
    }

    private void setChipCheck(final ChipGroup chipGroup, final ArrayList<String> items_text, final String type){
        for(int i = 1; i < chipGroup.getChildCount(); i++){
            final Chip chip = (Chip) chipGroup.getChildAt(i);
            runOnUiThread(() -> {
                if(items_text.contains(chip.getText().toString())){
                    if(type.equals("assign")) clearChipBackGround(chip);
                    else if(type.equals("vacation") || type.equals("overlap")){
                        setChipBackGround(chip);
                        String message = null;
                        if(type.equals("vacation")) {
                            message = chip.getText().toString()+"當日不能出動，請問依舊要選擇？";
                            if(chipGroup == staffGroup) setVacationChipCheckedListener(chip, staffs, staffs_text, message);
                            else setVacationChipCheckedListener(chip, cars, cars_text, message);
                        }
                        if(type.equals("overlap")) {
                            message = chip.getText().toString()+"已被其他訂單選用，請問依舊要選擇？";
                            if(chipGroup == staffGroup) {
                                chip.setTag(overlap_counter_s);
                                overlap_counter_s++;
                                setOverlapChipCheckedListener(chip, staffs_l, new_staffs_l, new_staffs_lap, message);
                            }
                            else {
                                chip.setTag(overlap_counter_c);
                                overlap_counter_c++;
                                setOverlapChipCheckedListener(chip, cars_l, new_cars_l, new_cars_lap, message);
                            }
                        }
                    }
                    chip.setChecked(true); //把本單有的員工列為已點擊
                    Log.d(TAG, chip.getText().toString()+"("+type+") check");
                }
            });
        }
    }

    private void setVacationChipCheckedListener(final Chip chip, final ArrayList<Integer> items, final ArrayList<String> items_text, final String message){
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final int tag = (Integer) chip.getTag();
            final String sname = chip.getText().toString();
            if(!isChecked && chip.getTextColors() != chip1.getTextColors()){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("確認選擇");
                builder.setMessage(message);
                builder.setPositiveButton("確定", (dialog, which) -> {
                    clearChipBackGround(chip);
                    if(!items_text.contains(chip.getText().toString())){
                        items.add(tag);
                        items_text.add(sname);
                    }
                });
                builder.setNegativeButton("取消", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                chip.setChecked(true);
            }
            else {
                setChipBackGround(chip);
                items.remove(Integer.valueOf(tag));
                items_text.remove(sname);
                chip.setChecked(true);
            }
        });
    }

    private void setOverlapChipCheckedListener(final Chip chip, final ArrayList<int[]> originItems, final ArrayList<int[]> items, final ArrayList<String> items_text, final String message){
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final int tag = (Integer) chip.getTag();
            final String sname = chip.getText().toString();
            if(!isChecked && chip.getTextColors() != chip1.getTextColors()){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("確認選擇");
                builder.setMessage(message);
                builder.setPositiveButton("確定", (dialog, which) -> {
                    clearChipBackGround(chip);
                    if(!items_text.contains(chip.getText().toString())){
                        items.add(originItems.get(tag));
                        items_text.add(sname);
                    }
                });
                builder.setNegativeButton("取消", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                chip.setChecked(true);
            }
            else {
                setChipBackGround(chip);
                if(items_text.contains(sname)){
                    items.remove(items_text.indexOf(sname));
                    items_text.remove(sname);
                }
                chip.setChecked(true);
            }
        });
    }

    private void setChipBackGround(Chip chip){
        chip.setChipBackgroundColorResource(R.color.bg_chip_state_list); //變換chip點擊背景顏色
        chip.setTextAppearance(R.style.chipTextColor); //變換chip點擊文字顏色
    }

    private void clearChipBackGround(Chip chip){
        chip.setChipBackgroundColor(chip1.getChipBackgroundColor());
        chip.setTextColor(chip1.getTextColors());
    }

    private void submit(){
        String function_name = "modify_staff_vehicle";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id", getCompany_id(context))
                .add("vehicle_assign", String.valueOf(cars))
                .add("staff_assign", String.valueOf(staffs))
                .add("vehicle_transform", arrayToString(new_cars_l))
                .add("staff_transform", arrayToString(new_staffs_l))
                .build();

        Log.i(TAG, "order_id: "+order_id
                +", vehicle_assign: "+cars+cars_text
                +", staff_assign: "+staffs+staffs_text
                +", vehicle_transform: "+arrayToString(new_cars_l)+new_cars_lap
                +", staff_transform: "+arrayToString(new_staffs_l)+new_staffs_lap);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/functional.php")
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
                runOnUiThread(() -> Toast.makeText(context, "連線錯誤", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG, "submit: "+responseData);
                if(responseData.equals("success"))
                    runOnUiThread(() -> Toast.makeText(context, "更新成功", Toast.LENGTH_LONG).show());
                else
                    runOnUiThread(() -> Toast.makeText(context, "更新失敗", Toast.LENGTH_LONG).show());
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(bundle.containsKey("order_detail")){
                Intent intent = new Intent(context, Order_Detail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else finish();
        }, 500);
    }

    private String arrayToString(ArrayList<int[]> array){
        String array_str = "[";
        for(int i = 0; i < array.size(); i++){
            if(i != 0) array_str = array_str + ", ";
            array_str = array_str + "[" + array.get(i)[0] + "," + array.get(i)[1] + "]";
        }
        array_str = array_str+"]";
        return array_str;
    }

    private void initArray(){
        staffs_text = new ArrayList<>();
        staffs = new ArrayList<>();
        staffs_vacation = new ArrayList<>();
        staffs_v = new ArrayList<>();
        staffs_lap = new ArrayList<>();
        staffs_l = new ArrayList<>();
        new_staffs_lap = new ArrayList<>();
        new_staffs_l = new ArrayList<>();
        cars_text = new ArrayList<>();
        cars = new ArrayList<>();
        cars_vacation = new ArrayList<>();
        cars_v = new ArrayList<>();
        cars_lap = new ArrayList<>();
        cars_l = new ArrayList<>();
        new_cars_lap = new ArrayList<>();
        new_cars_l = new ArrayList<>();
    }

    private void linking() {
        titleText = findViewById(R.id.title_SD);
        nameText = findViewById(R.id.name_SD);
        nameTitleText = findViewById(R.id.nameTitle_SD);
        movingDateText = findViewById(R.id.date_SD);
        fromAddressText = findViewById(R.id.FromAddress_SD);
        toAddressText = findViewById(R.id.ToAddress_SD);
        demandCarText = findViewById(R.id.demandCar_SD);
        staffText = findViewById(R.id.staff_SD);
        carText = findViewById(R.id.car_SD);
        staffGroup = findViewById(R.id.staffCG_SD);
        chip1 = findViewById(R.id.chip1_SD);
        carGroup = findViewById(R.id.carCG_SD);
        backBtn = findViewById(R.id.back_btn_SD);
        lastBtn = findViewById(R.id.last_imgBtn_SD);
        nextBtn = findViewById(R.id.next_imgBtn_SD);
        submitBtn = findViewById(R.id.submit_SD);
    }

    private void globalNav(){
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(context, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(context, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(context, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(context, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(context, Setting.class);
            startActivity(setting_intent);
        });
    }
}
