package com.example.homerenting_prototype_one.furniture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.adapter.base_adapter.LocationAdapter;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.adapter.base_adapter.NoDataAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.order.Order_Detail;
import com.example.homerenting_prototype_one.order.Today_Detail;
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

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;

public class Furniture_Location extends AppCompatActivity {
    String floor;
    String room_name;

    String TAG = "Furniture_Location";
    String PHP3 = "/furniture.php";


    private ListView location_list;
    ArrayList<String[]> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_furniture__location );
        location_list = findViewById( R.id.furniture_location_listView);
        ImageButton back_btn = findViewById( R.id.back_imgBtn);

        ImageButton valuation_btn = findViewById(R.id.valuation_imgBtn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        Bundle location_bundle = getIntent().getExtras();
        String order_id = location_bundle.getString("order_id");

        data = new ArrayList<>();

        String function_name = "furniture_room_detail";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id", company_id)
                .build();
        Log.d(TAG, "order_id:"+order_id);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+PHP3)
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Furniture_Location.this, "連線失敗", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData: "+responseData);

                try {
                    final JSONArray responseArr = new JSONArray(responseData);

                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject furniture = responseArr.getJSONObject(i);
                        if(!furniture.getString("room_id").equals("null")) {
                            floor = furniture.getString("floor");
                            room_name = furniture.getString("room_type") + furniture.getString("room_name");
                        }
                        else{
                            floor = "";
                            room_name = furniture.getString("space_type");
                        }
                        final String furniture_name = furniture.getString("furniture_name");
                        final String num = furniture.getString("num");
                        String[] row_data = {floor,room_name,furniture_name,num};
                        data.add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(responseData.equals("null")){
                        runOnUiThread(() -> {
                            Log.d(TAG, "NO DATA");
                            NoDataAdapter noData = new NoDataAdapter();
                            location_list.setAdapter(noData);
                        } );
                    }
                    //else Toast.makeText(context, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show();
                }

                //顯示資訊
                for(int i=0; i < data.size(); i++)
                    Log.i(TAG, "data: "+ Arrays.toString(data.get(i)));
                final LocationAdapter LocationAdapter = new LocationAdapter(data);
                runOnUiThread(() -> location_list.setAdapter(LocationAdapter));

            }
        });


        back_btn.setOnClickListener(v -> finish());

        //底下nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Furniture_Location.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Furniture_Location.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Furniture_Location.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Furniture_Location.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Furniture_Location.this, Setting.class);
            startActivity(setting_intent);
        });
    }
}
