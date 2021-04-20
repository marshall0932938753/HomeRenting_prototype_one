package com.example.homerenting_prototype_one.system;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.valuation.Valuation;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

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
import static com.example.homerenting_prototype_one.show.global_function.getToday;

public class System_Vacation extends AppCompatActivity {
    CalendarView calendar;
    ChipGroup staffGroup,  carGroup;

    ArrayList<String> staffs_text, cars_text;
    ArrayList<Integer> staffs, cars;

    String current_date;
    boolean lock = false;

    Context context = this;
    String TAG = "System_Vacation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system__vacation);
        calendar = findViewById(R.id.calendar_SV);
        staffGroup = findViewById(R.id.staffCG_SV);
        carGroup = findViewById(R.id.carCG_SV);

        staffs_text = new ArrayList<>();
        staffs = new ArrayList<>();
        cars_text = new ArrayList<>();
        cars = new ArrayList<>();

        getChip();

        current_date = getToday("yyyy-MM-dd");
//        getVacation("2020-09-27");
        getVacation(current_date);
        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            update_leave(current_date);

            String monthStr = String.valueOf(month + 1);
            if (month + 1 < 10) monthStr = "0" + monthStr;
            String dayOfMonthStr = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10) dayOfMonthStr = "0" + dayOfMonthStr;
            String date = year + "-" + monthStr + "-" + dayOfMonthStr;
            Log.i(TAG, "Date Change: " + date);
            current_date = date;
            initArray();
            getVacation(date);
        });

        globalNav();
    }

    private void getChip(){
        lock = true;
        final Chip chip1 = findViewById(R.id.chip1_SV); //控制形狀用的chip

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
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                //在app畫面上呈現錯誤訊息
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                //Log.d(TAG,"responseData: "+responseData); //顯示資料

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
                        runOnUiThread(() -> staffGroup.addView(setChipDetail(staffGroup, staff_id, staff_name, chip1)));
                    }

                    for (; i < responseArr.length(); i++) {
                        JSONObject vehicle = responseArr.getJSONObject(i);
                        if(!vehicle.has("vehicle_id")) break;
                        Log.i(TAG, "vehicle: " + vehicle);

                        //取欄位資料
                        final String vehicle_id = vehicle.getString("vehicle_id");
                        final String plate_num = vehicle.getString("plate_num");

                        runOnUiThread(() -> carGroup.addView(setChipDetail(carGroup, vehicle_id, plate_num, chip1)));
                    }
                    int ii = 0;
                    while((staffGroup.getChildCount()-1+carGroup.getChildCount()-1) != responseArr.length()){
                        if((++ii)%1000000 == 0)
                            Log.d(TAG, "waiting in getChip(): staffGroup:"+staffGroup.getChildCount()+", carGroup:"+carGroup.getChildCount());
                    }
                    lock = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private Chip setChipDetail(ChipGroup chipGroup, String id, String name, Chip chip1){
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

        chip.setChipBackgroundColorResource(R.color.bg_chip_state_list); //變換chip點擊背景顏色
        chip.setTextAppearance(R.style.chipTextColor); //變換chip點擊文字顏色

        if(chipGroup == staffGroup) setChipCheckedListener(chip, name, staffs, staffs_text);
        else setChipCheckedListener(chip, name, cars, cars_text);

        return chip;
    }

    private void setChipCheckedListener(final Chip chip, final String name, final ArrayList<Integer> items, final ArrayList<String> items_text){
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int tag = (int) buttonView.getTag();
            String sname = chip.getText().toString();
            if(isChecked){ //加入選擇
                if(!items_text.contains(name)) {
                    items.add(tag);
                    items_text.add(sname);
                }
            }
            else{ //取消選擇
                items.remove(Integer.valueOf(tag));
                items_text.remove(sname);
            }
            Log.i(TAG, "click chip: " + sname);

            String item_name;
            if(items == staffs) item_name = "staffs";
            else item_name = "cars";
            Log.i(TAG, item_name+"_text: " + items_text);
            Log.i(TAG, item_name+": " + items);

        });
    }

    private void initArray(){
        staffs.clear();
        staffs_text.clear();
        cars.clear();
        cars_text.clear();
    }

    private void getVacation(String date){
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
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of getVocation: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);

                    int i;
                    for (i = 0; i < responseArr.length(); i++) {
                        JSONObject vehicle_assign = responseArr.getJSONObject(i);
                        if(!vehicle_assign.has("vehicle_id")) break;
                        Log.i(TAG, "vehicle_assign:" + vehicle_assign);
                        cars_text.add(vehicle_assign.getString("plate_num"));
                        cars.add(Integer.parseInt(vehicle_assign.getString("vehicle_id")));
                    }

                    for (; i < responseArr.length(); i++) {
                        JSONObject staff_assign = responseArr.getJSONObject(i);
                        if(!staff_assign.has("staff_id")) break;
                        Log.i(TAG, "staff_assign:" + staff_assign);
                        staffs_text.add(staff_assign.getString("staff_name"));
                        staffs.add(Integer.parseInt(staff_assign.getString("staff_id")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(!responseData.equals("null")) {
                        runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show());
                    }
                }

                int ii = 0;
                while (lock){
                    if((++ii)%1000000 == 0) Log.d(TAG, "waiting for lock in getVacation...");
                }
                Log.d(TAG, "getVacation: staffGroup:"+staffGroup.getChildCount()+", carGroup:"+carGroup.getChildCount());
                setChipCheck(staffGroup, staffs_text);
                setChipCheck(carGroup, cars_text);
            }
        });
    }

    private void setChipCheck(ChipGroup chipGroup, ArrayList<String> items_text){
        for(int i = 1; i < chipGroup.getChildCount(); i++){
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setChecked(items_text.contains(chip.getText().toString())); //把本單有的員工列為已點擊
        }
    }

    private void update_leave(final String date){
        String function_name = "modify_vehicle_staff_leave";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .add("date", date)
                .add("staffItems", String.valueOf(staffs))
                .add("vehicleItems", String.valueOf(cars))
                .build();
        Log.d(TAG, "update_leave: date: "+date+", staffs:"+staffs+staffs_text+", cars:"+cars+cars_text);

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
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of update_leave("+date+"): "+responseData); //顯示資料
            }
        });
    }

    private void globalNav(){
        ImageButton back_btn = findViewById(R.id.back_imgBtn);
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        back_btn.setOnClickListener(v -> {
            update_leave(current_date);
            Log.d(TAG, "back. date: "+current_date+", staffs:"+staffs+staffs_text+", cars:"+cars+cars_text);
        });
        valuation_btn.setOnClickListener(v -> {
            update_leave(current_date);
            Intent valuation_intent = new Intent(System_Vacation.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            update_leave(current_date);
            Intent order_intent = new Intent(System_Vacation.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            update_leave(current_date);
            Intent calender_intent = new Intent(System_Vacation.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            update_leave(current_date);
            Intent system_intent = new Intent(System_Vacation.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            update_leave(current_date);
            Intent setting_intent = new Intent(System_Vacation.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    @Override
    public void onBackPressed() {
        update_leave(current_date);
        super.onBackPressed();
    }
}
