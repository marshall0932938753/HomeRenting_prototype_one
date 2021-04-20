package com.example.homerenting_prototype_one.order;

import android.content.Context;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.re_adpater.NoDataRecyclerAdapter;
import com.example.homerenting_prototype_one.adapter.re_adpater.SwipeDeleteAdapter;
import com.example.homerenting_prototype_one.helper.RecyclerViewAction;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.adapter.base_adapter.ListAdapter;
import com.example.homerenting_prototype_one.adapter.base_adapter.NoDataAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.valuation.Valuation;

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

import static com.example.homerenting_prototype_one.show.global_function.addDatalist;
import static com.example.homerenting_prototype_one.show.global_function.clearDatalist;
import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getDate;
import static com.example.homerenting_prototype_one.show.global_function.getEndOfWeek;
import static com.example.homerenting_prototype_one.show.global_function.getMonthStr;
import static com.example.homerenting_prototype_one.show.global_function.getStartOfWeek;
import static com.example.homerenting_prototype_one.show.global_function.getTime;
import static com.example.homerenting_prototype_one.show.global_function.getWeek;
import static com.example.homerenting_prototype_one.show.global_function.getwCount;
import static com.example.homerenting_prototype_one.show.global_function.removeNew;
import static com.example.homerenting_prototype_one.show.global_function.setwCount;


public class Order_Booking extends AppCompatActivity {
    ArrayList<String[]> data;

    TextView week_text;
    TextView month_text;
    ImageButton lastWeek_btn, nextWeek_btn;
    //    ListView orderList;
    RecyclerView orderRList;

    private final String PHP = "/user_data.php";
    Context context = this;
    String TAG = "Order_Booking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__booking);
        week_text = findViewById(R.id.week_OB);
        month_text = findViewById(R.id.month_OB);
        lastWeek_btn = findViewById(R.id.lastWeek_btn_OB);
        nextWeek_btn = findViewById(R.id.nextWeek_btn_OB);
        orderRList = findViewById(R.id.order_recyclerView_OB);

        Button order = findViewById(R.id.order_btn);
        Button booking_order = findViewById(R.id.bookingOrder_btn);
        Button today_order = findViewById(R.id.todayOrder_btn);
        Button cancel_order = findViewById(R.id.cancelOrder_btn);
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        data = new ArrayList<>();

        week_text.setText(getWeek());
        month_text.setText(getMonthStr());
        getOrder();

        lastWeek_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wCount = getwCount();
                setwCount(wCount-1);
                week_text.setText(getWeek());
                month_text.setText(getMonthStr());
                data.clear();
                getOrder();
            }
        });

        nextWeek_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wCount = getwCount();
                setwCount(wCount+1);
                week_text.setText(getWeek());
                month_text.setText(getMonthStr());
                data.clear();
                getOrder();
            }
        });



        //上方nav
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent order_intent = new Intent(Order_Booking.this, Order.class);
                startActivity(order_intent);
            }
        });
//        booking_order.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent bookingOrder_intent = new Intent(Order_Booking.this, Order_Booking.class);
//                startActivity(bookingOrder_intent);
//            }
//        });
        today_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent todayOrder_intent = new Intent(Order_Booking.this, Order_Today.class);
                startActivity(todayOrder_intent);
            }
        });
        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelOrder_intent = new Intent(Order_Booking.this, Order_Cancel.class);
                startActivity(cancelOrder_intent);
            }
        });

        //下方nav
        valuation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent valuation_intent = new Intent(Order_Booking.this, Valuation.class);
                startActivity(valuation_intent);
            }
        });
//        order_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent order_intent = new Intent(Order.this, Order.class);
//                startActivity(order_intent);
//            }
//        });
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calender_intent = new Intent(Order_Booking.this, Calendar.class);
                startActivity(calender_intent);
            }
        });
        system_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent system_intent = new Intent(Order_Booking.this, System.class);
                startActivity(system_intent);
            }
        });
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_intent = new Intent(Order_Booking.this, Setting.class);
                startActivity(setting_intent);
            }
        });
    }

    private void getOrder(){
        clearDatalist();
        String function_name = "order_member";
        String startDate =  getStartOfWeek();
        String endDate = getEndOfWeek();
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", company_id)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("status", "assigned")
                .build();
        Log.i(TAG, "getOrder:\n"+"startDate:"+startDate+", endDate:"+endDate+", status:"+"scheduled");

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + PHP)
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Order_Booking.this, "Toast onFailure.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData: "+responseData);

                try {
                    final JSONArray responseArr = new JSONArray(responseData);

                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject member = responseArr.getJSONObject(i);
                        String order_id = member.getString("order_id");
                        String name = member.getString("member_name");
                        String datetime = member.getString("moving_date");
                        String gender = member.getString("gender");
                        if (gender.equals("女")) gender ="小姐";
                        else gender = "先生";
                        String nameTitle = gender;
                        String phone = member.getString("phone");
                        String contact_address = member.getString("contact_address");
                        if(contact_address.equals("null")) contact_address = "";
                        String auto = member.getString("auto");
                        String newicon = member.getString("new");
                        Log.d(TAG,"order_id: "+order_id);

                        //將資料放入陣列
                        String[] row_data = {order_id, getDate(datetime), getTime(datetime), name, nameTitle, phone, contact_address, auto, newicon};
                        data.add(row_data);
                        addDatalist(order_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(responseData.equals("null")){
                                Log.d(TAG, "NO DATA");
                                NoDataRecyclerAdapter noDataAdapter = new NoDataRecyclerAdapter();
                                orderRList.setLayoutManager(new LinearLayoutManager(context));
                                orderRList.setAdapter(noDataAdapter);
                            }
                            //else Toast.makeText(Order_Booking.this, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                //顯示資訊
                if(!responseData.equals("null")){
                    for(int i=0; i < data.size(); i++)
                        Log.i(TAG, "data: "+ Arrays.toString(data.get(i)));
                    setRList();
                }
            }
        });
    }

    private void setRList(){
        final SwipeDeleteAdapter adapter = new SwipeDeleteAdapter(this, data, Order_Detail.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orderRList.setLayoutManager(new LinearLayoutManager(context));
                orderRList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL)); //分隔線
                orderRList.setAdapter(adapter);

                //側滑刪除
//                ItemTouchHelper helper = new ItemTouchHelper(new RecyclerViewAction(context, adapter));
//                helper.attachToRecyclerView(orderRList);
            }
        });
    }
}
