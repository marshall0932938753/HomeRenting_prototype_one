package com.example.homerenting_prototype_one.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.base_adapter.RecordAdapter;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
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
import static com.example.homerenting_prototype_one.show.global_function.getDate;

public class Record_Detail extends AppCompatActivity {
    private ListView list;
    ArrayList<String[]> data;

    String year, month;

    OkHttpClient okHttpClient = new OkHttpClient();
    String TAG = "Record_Detail";
    Context context = Record_Detail.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_record__detail );
        ImageButton back_btn = findViewById(R.id.back_imgBtn);
        TextView title_text = findViewById( R.id.title_text );
        Button export_btn = findViewById(R.id.export_btn);
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        list = findViewById(R.id.record_list_RD);

        data = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        year = bundle.getString("year");
        month = bundle.getString("month");

        getOrder();

        title_text.setText( month+"月工單紀錄" );
        export_btn.setOnClickListener(v -> {
            Intent export_intent = new Intent();
            export_intent.setAction( Intent.ACTION_SEND );
            //export_intent.putExtra( Intent.EXTRA_TEXT,getResources().getString(  ) );
            export_intent.putExtra( Intent.EXTRA_TEXT,"分享此報表" );
            export_intent.setType( "text/plain" );

            Intent share_intent = Intent.createChooser( export_intent, null);
            startActivity( share_intent);
        });


        back_btn.setOnClickListener(v -> finish());

        //底下nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Record_Detail.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Record_Detail.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Record_Detail.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Record_Detail.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Record_Detail.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    private void getOrder(){
        String function_name = "order_member_oneMonth";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", company_id)
                .add("year", year)
                .add("month", month)
                .build();
        Log.i(TAG, "year: "+year+", month: "+month);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "/user_data.php")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback(){
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
                Log.i(TAG,"responseData: "+responseData); //顯示資料
                try {
                    final JSONArray responseArr = new JSONArray(responseData);

                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject order = responseArr.getJSONObject(i);
                        Log.i(TAG,"order: "+order);

                        String order_id = order.getString("order_id");
                        String valuation_status = order.getString("valuation_status");
                        String order_status = order.getString("order_status");
                        String date = order.getString("moving_date");
                        if(date.equals("null")) date = order.getString("valuation_date");
                        if(date.equals("null") || valuation_status.equals("self")) date = order.getString("last_update");
                        date = getDate(date);
                        String member = order.getString("member_name");

                        String[] row_data = {order_id, date, member, valuation_status, order_status};
                        data.add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!responseData.equals("null")){
                    for(int i=0; i < data.size(); i++)
                        Log.i(TAG, "data: "+ Arrays.toString(data.get(i)));
                    runOnUiThread(() -> {
                        RecordAdapter adapter = new RecordAdapter(data);
                        list.setAdapter(adapter);
                    });
                }
            }

        });
    }
}
