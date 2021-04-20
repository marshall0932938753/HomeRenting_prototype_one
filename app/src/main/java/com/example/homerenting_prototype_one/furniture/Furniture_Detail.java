package com.example.homerenting_prototype_one.furniture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.adapter.base_adapter.DetailAdapter;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.adapter.base_adapter.NoDataAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.valuation.MatchMaking_Detail;
import com.example.homerenting_prototype_one.valuation.Valuation;
import com.example.homerenting_prototype_one.valuation.ValuationCancel_Detail;
import com.example.homerenting_prototype_one.valuation.Valuation_Detail;

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

import static com.example.homerenting_prototype_one.R.*;
import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;

public class Furniture_Detail extends AppCompatActivity {
    ImageButton valuation_btn;
    ImageButton order_btn;
    ImageButton calendar_btn;
    ImageButton system_btn;
    ImageButton setting_btn;

    String TAG = "Furniture_Detail";
    Context context;

    private ListView detail_list;
    ArrayList<String[]> data;

    private final String PHP = "/furniture.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( layout.activity_furniture__detail );
        ImageButton back_btn = findViewById( id.back_imgBtn);

        context = Furniture_Detail.this;

        linking();

        Bundle detail_bundle = getIntent().getExtras();
//        final String key = detail_bundle.getString("key");
        String order_id = detail_bundle.getString("order_id");
        final String key = "valuation";
//        String order_id = "6";
        Log.d(TAG, "key: " + key + ", order_id: " + order_id);

        detail_list = findViewById( id.furniture_detail_listView );

        data = new ArrayList<>();

        String function_name = "furniture_detail";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id", company_id)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+PHP)
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
                        Toast.makeText(Furniture_Detail.this, "Toast onFailure.", Toast.LENGTH_LONG).show();
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
                        JSONObject furniture = responseArr.getJSONObject(i);
                        final String furniture_name = furniture.getString("furniture_name");
                        final String num = furniture.getString("num");
                        final String furniture_memo = " ";

                        String[] row_data = {furniture_name, num, furniture_memo};
                        data.add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(responseData.equals("null")){
                                Log.d(TAG, "NO DATA");
                                NoDataAdapter noData = new NoDataAdapter();
                                detail_list.setAdapter(noData);
                            }
                            //else Toast.makeText(context, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                //顯示資訊
                for(int i = 0; i < data.size(); i++)
                    Log.i(TAG, "data: "+ Arrays.toString(data.get(i)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DetailAdapter detail_adapter = new DetailAdapter(data);
                        detail_list.setAdapter(detail_adapter);
                    }
                });

            }
        });

//        String[][] datas = {{"test1", "100"}, {"test2", "200"}};
//        data.add(datas[0]);
//        data.add(datas[1]);

        //ArrayList<String[]> datas = new ArrayList<>();

//        for(int i = 0; i < 2; i++){
//            String[] row_data = {"test" + (i+1), String.valueOf(i+1)};
//            data.add(row_data);
//        }

        //data.add(datas.get(0));
        //data.add(datas.get(1));


        back_btn.setOnClickListener(v -> finish());


        //底下nav
        valuation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent valuation_intent = new Intent(Furniture_Detail.this, Valuation.class);
                startActivity(valuation_intent);
            }
        });
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent order_intent = new Intent(Furniture_Detail.this, Order.class);
                startActivity(order_intent);
            }
        });
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calender_intent = new Intent(Furniture_Detail.this, Calendar.class);
                startActivity(calender_intent);
            }
        });
        system_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent system_intent = new Intent(Furniture_Detail.this, System.class);
                startActivity(system_intent);
            }
        });
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_intent = new Intent(Furniture_Detail.this, Setting.class);
                startActivity(setting_intent);
            }
        });
    }

    public void linking(){
        valuation_btn = findViewById(id.valuation_imgBtn);
        order_btn = findViewById(id.order_imgBtn);
        calendar_btn = findViewById(id.calendar_imgBtn);
        system_btn = findViewById(id.system_imgBtn);
        setting_btn = findViewById(id.setting_imgBtn);
    }
}
