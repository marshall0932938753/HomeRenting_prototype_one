package com.example.homerenting_prototype_one.bouns;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.adapter.base_adapter.BonusAdapter;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.re_adpater.SalaryAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.sync_scroll.ScrollManager;
import com.example.homerenting_prototype_one.sync_scroll.SyncedHorizontalScrollView;
import com.example.homerenting_prototype_one.system.System;
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

import static com.example.homerenting_prototype_one.show.global_function.dip2px;
import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getDay;

public class Bonus_List_Detail extends AppCompatActivity {
    private ArrayList<ArrayList<String[]>> data;
    private ArrayList<Integer> date;

    ScrollManager scrollManager;

    String year, month;

    volatile boolean lock = false; //會被多個執行緒用到的變數

    private Context context = this;
    private int DATA_WIDTH;
    private String TAG = "Bonus_List_Detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_bonus__list__detail );

        ImageButton back_btn = findViewById( R.id.back_imgBtn);
        TextView title_text = findViewById( R.id.month_title_text );
        Button export_btn = findViewById(R.id.export_bonus_btn);

        Bundle bundle = getIntent().getExtras();
        year = bundle.getString("year");
        month = bundle.getString("month");

        data = new ArrayList<>();
        date = new ArrayList<>();
        scrollManager = new ScrollManager();
        DATA_WIDTH = dip2px(context, 90);

        title_text.setText( month+"月獎金報表" );

        lock = true;
        getData();
        getDate();

        int i = 0;
        while (lock){
            if(i++%1000000 == 0) Log.d(TAG, "wait for getting data lock...");
        }
        setHeaderRow();
        setFixedTable();
        setSalaryTable();


        back_btn.setOnClickListener(v -> finish());
        export_btn.setOnClickListener(v -> {
            Intent export_intent = new Intent();
            export_intent.setAction( Intent.ACTION_SEND );
            export_intent.putExtra( Intent.EXTRA_TEXT,"分享此報表" );
            export_intent.setType( "text/plain" );

            Intent share_intent = Intent.createChooser( export_intent, null);
            startActivity( share_intent);
        });

        globalNav();
    }

    private void getData(){
        String function_name = "pay_daily";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .add("year", year)
                .add("month", month)
                .build();
        Log.i(TAG, "getData: year: "+year+", month: "+month);

        //連線要求
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "/user_data.php")
                .post(body)
                .build();

        //連線
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

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.i(TAG,"responseData of getData: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);

                    int staff_count = -1;
                    String current_staff = "";
                    String current_day = "";
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject staff_pay = responseArr.getJSONObject(i);
                        Log.i(TAG,"staff_pay: "+staff_pay);

                        String staff_name = staff_pay.getString("staff_name");
                        String day = getDay(staff_pay.getString("moving_date"));
                        int pay = Integer.parseInt(staff_pay.getString("pay"));
                        if(pay == -1) pay = 0;

                        String[] row_data = {staff_name, day, String.valueOf(pay)};

                        if(!staff_name.equals(current_staff)){ //如果不同人，data 加一
                            current_staff = staff_name;
                            staff_count = staff_count+1;
                            data.add(new ArrayList<String[]>());
                        }
                        else if(day.equals(current_day)){ //如果同個人而且和上一筆同一天，把pay加上去
                            row_data = data.get(staff_count).get(data.get(staff_count).size()-1); //上一筆
                            int day_pay = Integer.parseInt(row_data[2]);
                            day_pay = day_pay + pay;
                            row_data[2] = String.valueOf(day_pay);
                            data.get(staff_count).set(data.get(staff_count).size()-1, row_data);
                            continue;
                        }

                        current_day = day;
                        data.get(staff_count).add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "data:");
                for(int i = 0; i < data.size(); i++){
                    for(int ii = 0; ii < data.get(i).size(); ii++)
                        Log.d(TAG, Arrays.toString(data.get(i).get(ii)));
                }
            }
        });
    }

    private void getDate(){
        String function_name = "month_order_date";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .add("year", year)
                .add("month", month)
                .build();
        Log.i(TAG, "getDate: year: "+year+", month: "+month);

        //連線要求
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "/user_data.php")
                .post(body)
                .build();

        //連線
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                //在app畫面上呈現錯誤訊息
                runOnUiThread(() -> Toast.makeText(context, "連線失敗", Toast.LENGTH_LONG).show());

                lock = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.i(TAG,"responseData of getDate: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);

                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject pay_date = responseArr.getJSONObject(i);
                        Log.i(TAG,"pay_date: "+pay_date);

                        int pay_day = Integer.parseInt(getDay(pay_date.getString("moving_date")));
                        if(!date.contains(pay_day)) date.add(pay_day);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < date.size(); i++)
                    Log.d(TAG, "date: "+date.get(i));
                lock = false;
            }
        });
    }

    private void setFixedTable(){
        TableLayout fixedTable = findViewById(R.id.fixedColumn_BLD);
        for(int i = 0; i < data.size(); i++){
            int dp2 = dip2px(context, 2);
            TableRow row = new TableRow(context);
            row.addView(newTextView(data.get(i).get(0)[0]));

            View rowView = newRowDividerView();
            TableRow.LayoutParams rowParams = (TableRow.LayoutParams) rowView.getLayoutParams();
            rowParams.setMargins(dp2, 0, dp2, 0);
            rowView.setLayoutParams(rowParams);
            row.addView(rowView);

            row.addView(newTextView(String.valueOf(getSumSalary(data.get(i)))));
            row.addView(newRowDividerView());
            fixedTable.addView(row);

            fixedTable.addView(newDividerView());
        }
    }

    private void setHeaderRow(){
        LinearLayout headerRow = findViewById(R.id.headerRow_LL_BLD);
        SyncedHorizontalScrollView scrollView = new SyncedHorizontalScrollView(context);
        scrollView.setHorizontalScrollBarEnabled(false); //隱藏滑動條
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER); //關閉反彈動畫
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);

        for(int i = 0; i < date.size(); i++){
            TextView datetext = newTextView(month+"/"+date.get(i));
            datetext.setTextSize(20);
            datetext.setTextColor(Color.parseColor("#FFFFFF"));
            datetext.setTypeface(null, Typeface.BOLD);
            l.addView(datetext);

            View view = newRowDividerView();
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            l.addView(view);
        }

        scrollView.addView(l);
        headerRow.addView(scrollView);
        scrollManager.addScrollClient(scrollView);
    }

    private void setSalaryTable(){
        LinearLayout fillableArea = findViewById(R.id.fillableArea_BLD);
        SyncedHorizontalScrollView scrollView = new SyncedHorizontalScrollView(context);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER); //關閉反彈動畫
        TableLayout salaryTable = new TableLayout(context);
        for(int i = 0; i < data.size(); i++){ //第幾個人
            TableRow tr = new TableRow(context);
            ArrayList<String[]> staff = data.get(i);
//            Log.d(TAG, "staff: "+staff.get(0)[0]);
            for(int ii = 0; ii < date.size(); ii++){ //第幾天
//                Log.d(TAG, "date: "+month+"/"+date.get(ii));
                String salary = "無資料";
                for(int iii = 0; iii < staff.size(); iii++){ //第幾筆資料
                    if(Integer.parseInt(staff.get(iii)[1]) == date.get(ii)){ //有沒有第ii天的資料
                        salary = staff.get(iii)[2];
//                        Log.d(TAG, "find salary at date.get("+iii+")");
                        break;
                    }
                    if(Integer.parseInt(staff.get(iii)[1]) > date.get(ii)){
//                        Log.d(TAG, "cannot find salary after "+staff.get(iii)[1]+" ("+iii+")");
                        break;
                    }
                }
//                Log.d(TAG, month+"/"+date.get(ii)+" salary: "+salary);
                TextView salaryText = newTextView(salary);
                tr.addView(salaryText);
                tr.addView(newRowDividerView());
//                Log.d(TAG, i+". tr.childCount: "+tr.getChildCount());
            }
            salaryTable.addView(tr);
            salaryTable.addView(newDividerView());
//            Log.d(TAG, "table.childCount: "+salaryTable.getChildCount());
        }
        scrollView.addView(salaryTable);
        fillableArea.addView(scrollView);
        scrollManager.addScrollClient(scrollView);
    }

    private TextView newTextView(String text){
        int dp4 = dip2px(context, 4);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setWidth(DATA_WIDTH);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, dp4, 0, dp4);
        textView.setTextSize(14);
        return textView;
    }

    private View newRowDividerView(){
        int dp1 = dip2px(context, 1);
        View view = new View(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(dp1 , TableRow.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.parseColor("#000000"));
        return view;
    }

    private View newDividerView(){
        int dp1 = dip2px(context, 1);
        View view = new View(context);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT , dp1);
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.parseColor("#D3D3D3"));
        return view;
    }

    private int getSumSalary(ArrayList<String[]> staff){
        int sum = 0;
        for(int i = 0; i < staff.size(); i++){
            int salary = Integer.parseInt(staff.get(i)[2]);
            sum = sum + salary;
        }
        return sum;
    }

    private void globalNav(){
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Bonus_List_Detail.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Bonus_List_Detail.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Bonus_List_Detail.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Bonus_List_Detail.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Bonus_List_Detail.this, Setting.class);
            startActivity(setting_intent);
        });
    }
}
