package com.example.homerenting_prototype_one.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.applandeo.materialcalendarview.EventDay;
import com.example.homerenting_prototype_one.add_order.Add_Order;
import com.example.homerenting_prototype_one.add_order.Add_Valuation;
import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.adapter.base_adapter.New_CalendarAdapter;
import com.example.homerenting_prototype_one.adapter.base_adapter.NoDataAdapter;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.order.Order_Detail;
import com.example.homerenting_prototype_one.valuation.MatchMaking_Detail;
import com.example.homerenting_prototype_one.valuation.Valuation;
import com.example.homerenting_prototype_one.valuation.ValuationBooking_Detail;
import com.example.homerenting_prototype_one.valuation.ValuationCancel_Detail;
import com.example.homerenting_prototype_one.valuation.Valuation_Detail;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.addDatalist;
import static com.example.homerenting_prototype_one.show.global_function.clearDatalist;
import static com.example.homerenting_prototype_one.show.global_function.dip2px;
import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getDay;
import static com.example.homerenting_prototype_one.show.global_function.getMonth;
import static com.example.homerenting_prototype_one.show.global_function.getStartTime;
import static com.example.homerenting_prototype_one.show.global_function.getTime;
import static com.example.homerenting_prototype_one.show.global_function.getYear;
import static com.example.homerenting_prototype_one.show.global_function.removeNew;

public class Calendar extends AppCompatActivity {
    CalendarView calendar;
    com.applandeo.materialcalendarview.CalendarView mCalendar;
    ImageButton valuation_btn, order_btn, calendar_btn, system_btn, setting_btn;
    TextView valuation_bar, order_bar, dateText;
    ListView orderList;
    Button addV_btn, addO_btn, cancel_btn;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View v;
    New_CalendarAdapter calendarAdapter, calendarAdapter_v, calendarAdapter_o;
    ArrayList<String[]> data, data_v, data_o;
    ArrayList<Integer> checkedMonth = new ArrayList<>();

    Boolean VB, OB;

    Context context = Calendar.this;
    String TAG = "Calendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        init();
        setmCalendar();

        globalNav();
    }

    private void getOrder(String date) {
        initData();
        clearDatalist();
        String function_name = "order_member_oneDay";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(this))
                .add("date", date)
                .build();
        Log.i(TAG, "function_name: " + function_name + ", company_id: "+getCompany_id(this)+", date: " + date);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "/user_data.php")
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            //連線失敗
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                runOnUiThread(() -> Toast.makeText(context, "連線失敗", Toast.LENGTH_LONG).show());
            }

            //連線成功
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG, "responseData of getOrder: "+responseData); //顯示資料

                try {
                    //轉換成json格式，array或object
                    final JSONArray responseArr = new JSONArray(responseData);
                    //Log.d(TAG,"responseObj: "+ responseArr);

                    //一筆一筆的取JSONArray中的json資料
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject order = responseArr.getJSONObject(i);
                        Log.d(TAG, "order:" + order);
                        String order_id = order.getString("order_id");
                        String time = order.getString("moving_date");
                        if (time.equals("null")) time = getStartTime(order.getString("valuation_time"));
                        else time = getTime(time);
                        if(time.equals("null")) time = "";
                        String address = order.getString("from_address");
                        String order_status = order.getString("order_status");
                        String valuation_status = order.getString("valuation_status");
                        String newicon = order.getString("new");

                        String[] row_data = {order_id, time, address, order_status, valuation_status, newicon};
                        data.add(row_data);
                        if (order_status.equals("evaluating")) data_v.add(row_data);
                        else{
                            data_o.add(row_data);
                            addDatalist(order_id);
                        }
                    }
                } catch (JSONException e) { //會到這裡通常表示用錯json格式或網頁的資料不是json格式
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if (responseData.equals("null")) {
                            NoDataAdapter noData = new NoDataAdapter();
                            orderList.setAdapter(noData);
                        } else
                            Toast.makeText(context, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show();
                    });
                }

                //顯示資訊
                if (data.size() > 1) {
                    for (int i = 0; i < data.size(); i++)
                        Log.i(TAG, "data: " + Arrays.toString(data.get(i)));
                    calendarAdapter = new New_CalendarAdapter(data);
                    calendarAdapter_v = new New_CalendarAdapter(data_v);
                    calendarAdapter_o = new New_CalendarAdapter(data_o);
                    runOnUiThread(() -> {
                        orderList.setAdapter(calendarAdapter);
                        orderList.setOnItemClickListener((parent, view, position, id) -> {
                            String[] row_data = (String[]) parent.getItemAtPosition(position);
                            Log.i(TAG, "row_data: " + Arrays.toString(row_data));
                            String order_id = row_data[0];

                            Bundle bundle = new Bundle();
                            bundle.putString("order_id", order_id);

                            String newicon = row_data[row_data.length - 1];
                            if (newicon.equals("1"))
                                removeNew(order_id, context);

                            Intent intent = new Intent();
                            String order_status = row_data[3];
                            String valuation_status = row_data[4];
                            Log.d(TAG, "status(" + order_id + "): " + order_status);
                            if (order_status.equals("evaluating")) {
                                switch (valuation_status) {
                                    case "self":
                                        intent.setClass(context, Valuation_Detail.class);
                                        break;
                                    case "booking":
                                        intent.setClass(context, ValuationBooking_Detail.class);
                                        break;
                                    case "match":
                                        intent.setClass(context, MatchMaking_Detail.class);
                                        break;
                                    default:
                                        intent.setClass(context, ValuationCancel_Detail.class);
                                        break;
                                }
                            }
                            else intent.setClass(context, Order_Detail.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        });
                    });
                } else Log.i(TAG, "data: no data");
            }
        });
    }

    private void setList() {
        orderList.setAdapter(null);
        if (VB && OB) orderList.setAdapter(calendarAdapter);
        else if (VB) orderList.setAdapter(calendarAdapter_v);
        else if (OB) orderList.setAdapter(calendarAdapter_o);
    }

    private void setmCalendar(){
        java.util.Calendar calendarTime = java.util.Calendar.getInstance();
        int year = calendarTime.get(java.util.Calendar.YEAR);
        int month = (calendarTime.get(java.util.Calendar.MONTH)+1);
        Log.d(TAG, "check "+year+"/"+month);

        getOrders(String.valueOf(year), String.valueOf(month));

        mCalendar.setOnPreviousPageChangeListener(() -> {
            int previous_month = calendarTime.get(java.util.Calendar.MONTH);
            calendarTime.set(java.util.Calendar.MONTH, previous_month-1);
            if(previous_month == 0) previous_month = 12;
            int previous_year = calendarTime.get(java.util.Calendar.YEAR);
            Log.i(TAG, "calendarTime: "+calendarTime.getTime().toString());
            Log.d(TAG, "page change: "+previous_year+"/"+previous_month);
            getOrders(String.valueOf(previous_year), String.valueOf(previous_month));
        });

        mCalendar.setOnForwardPageChangeListener(() -> {
            int next_month = calendarTime.get(java.util.Calendar.MONTH)+2;
            calendarTime.set(java.util.Calendar.MONTH, next_month-1);
            if(next_month == 13) next_month = 1;
            int next_year = calendarTime.get(java.util.Calendar.YEAR);
            Log.i(TAG, "calendarTime: "+calendarTime.getTime().toString());
            Log.d(TAG, "page change: "+next_year+"/"+next_month);
            getOrders(String.valueOf(next_year), String.valueOf(next_month));
        });

        mCalendar.setOnDayClickListener(eventDay -> {
            Date datetime = eventDay.getCalendar().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(datetime);

            dialog = new AlertDialog.Builder(context);
            inflater = getLayoutInflater();
            v = inflater.inflate(R.layout.calendar_dialog, null);
            dialog.setView(v);
            linking2();
            dateText.setText(date);
            getOrder(date);

            valuation_bar.setOnClickListener(v -> {
                if (VB) {
                    VB = false;
                    valuation_bar.setBackgroundColor(Color.rgb(195, 184, 173));
                    valuation_bar.setTextColor(Color.rgb(140, 132, 132));
                } else {
                    VB = true;
                    valuation_bar.setBackgroundColor(Color.rgb(251, 133, 39));
                    valuation_bar.setTextColor(Color.rgb(255, 255, 255));
                }
                setList();
            });

            order_bar.setOnClickListener(v -> {
                if (OB) {
                    OB = false;
                    order_bar.setBackgroundColor(Color.rgb(195, 184, 173));
                    order_bar.setTextColor(Color.rgb(140, 132, 132));
                } else {
                    OB = true;
                    order_bar.setBackgroundColor(Color.rgb(25, 176, 237));
                    order_bar.setTextColor(Color.rgb(255, 255, 255));
                }
                setList();
            });

            Bundle bundle = new Bundle();
            bundle.putString("date", date);
            addV_btn.setOnClickListener(v -> {
                Intent intent = new Intent(context, Add_Valuation.class);
                intent.putExtras(bundle);
                startActivity(intent);
            });

            addO_btn.setOnClickListener(v -> {
                Intent intent = new Intent(context, Add_Order.class);
                intent.putExtras(bundle);
                startActivity(intent);
            });

            final AlertDialog alertDialog = dialog.create();
            cancel_btn.setOnClickListener(v -> alertDialog.dismiss());
            alertDialog.show();
//            alertDialog.getWindow().setLayout(dip2px(context, 370), dip2px(context, 600));
        });
    }


    private void getOrders(String year, String month){
        List<EventDay> events = new ArrayList<>();

        String function_name = "order_member_oneMonth";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", company_id)
                .add("year", year)
                .add("month", month)
                .build();
        Log.i(TAG, "function_name: "+function_name+", company_id: "+company_id+", year: "+year+", month: "+month);

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
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "responseData of getOrders: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);
                    String last_date = "";
                    int last_isOrder = -1;
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject order = responseArr.getJSONObject(i);
//                        Log.d(TAG, "order:" + order);

                        int isOrder = 1;
                        String order_id = order.getString("order_id");
                        String status = order.getString("order_status");
                        if(status.equals("cancel") || status.equals("done") || status.equals("paid")){
//                            Log.d(TAG, "order_id: "+order_id+", status: order_cancel");
                            continue;
                        }
                        if(status.equals("evaluating")) {
                            isOrder = 0;
                            status = order.getString("valuation_status");
                            if(status.equals("cancel")){
//                                Log.d(TAG, "order_id: "+order_id+", status: valuation_cancel");
                                continue;
                            }
                        }
                        String date;
                        if(order.getString("moving_date").equals("null")) {
                            if(order.getString("valuation_date").equals("null") || order.getString("valuation_status").equals("self")){
                                date = order.getString("last_update");
                                String[] token = date.split(" ");
                                date = token[0];
                            }
                            else date = order.getString("valuation_date");
                        }
                        else {
                            date = order.getString("moving_date");
                            String[] token = date.split(" ");
                            date = token[0];
                        }
                        Log.d(TAG, "order_id: "+order_id+", date: "+date+", isOrder:"+isOrder+", status: "+status);

                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.YEAR, Integer.parseInt(getYear(date)));
                        calendar.set(java.util.Calendar.MONTH, Integer.parseInt(getMonth(date))-1);
                        calendar.set(java.util.Calendar.DATE, Integer.parseInt(getDay(date)));

                        EventDay eventDay;
                        if(date.equals(last_date)){
//                            Log.d(TAG, "isOrder: "+isOrder+", last_isOrder: "+last_isOrder);
                            if(isOrder == 1 && last_isOrder == 0) {
//                                Log.d(TAG, "DOUBLE ORDER");
                                eventDay = new EventDay(calendar, R.drawable.calendar_double_dot);
                                events.set(events.size()-1, eventDay);
                                last_date = date;
                                last_isOrder = 4;
                            }
                            continue;
                        }

                        if(isOrder == 1) {
//                            Log.d(TAG, "BLUE");
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.calendar_dot2);
                            eventDay = new EventDay(calendar, new InsetDrawable(drawable, 55));
                        }
                        else {
//                            Log.d(TAG, "ORANGE");
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.calendar_dot);
                            eventDay = new EventDay(calendar, new InsetDrawable(drawable, 55));
                        }

                        events.add(eventDay);
                        last_date = date;
                        last_isOrder = isOrder;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "getOrders: "+e.getMessage());
                }

                runOnUiThread(() -> mCalendar.setEvents(events));
            }
        });
    }


    private void linking() {
        calendar = findViewById(R.id.main_calendar);
        mCalendar = findViewById(R.id.mCalendar_main);
        valuation_btn = findViewById(R.id.valuationBlue_Btn);
        order_btn = findViewById(R.id.order_imgBtn);
        calendar_btn = findViewById(R.id.calendar_imgBtn);
        system_btn = findViewById(R.id.system_imgBtn);
        setting_btn = findViewById(R.id.setting_imgBtn);
    }

    private void linking2() {
        valuation_bar = v.findViewById(R.id.valuation_bar_C);
        order_bar = v.findViewById(R.id.order_bar_C);
        dateText = v.findViewById(R.id.date_C);
        orderList = v.findViewById(R.id.orderList_C);
        addV_btn = v.findViewById(R.id.add_valuation_btn_C);
        addO_btn = v.findViewById(R.id.add_order_btn_C);
        cancel_btn = v.findViewById(R.id.cancelBtn_C);
    }

    private void init(){
        data = new ArrayList<>();
        data_v = new ArrayList<>();
        data_o = new ArrayList<>();
        initData();
        VB = true;
        OB = true;
        linking();
    }

    private void initData(){
        data.clear();
        data_v.clear();
        data_o.clear();
        String[] row_data = {"編號", "時間", "地址", "0", "0"};
        data.add(row_data);
        data_v.add(row_data);
        data_o.add(row_data);
    }

    private void globalNav(){
        //底下nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(context, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(context, Order.class);
            startActivity(order_intent);
        });
//        calendar_btn.setOnClickListener(v -> {
//            Intent calender_intent = new Intent(context, Calendar.class);
//            startActivity(calender_intent);
//        });
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