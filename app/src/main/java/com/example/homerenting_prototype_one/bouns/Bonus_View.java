package com.example.homerenting_prototype_one.bouns;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.re_adpater.MutiSalaryAdapter;
import com.example.homerenting_prototype_one.adapter.re_adpater.SalaryAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.valuation.Valuation;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

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
import static com.example.homerenting_prototype_one.show.global_function.getToday;
import static com.example.homerenting_prototype_one.show.global_function.getYear;

public class Bonus_View extends AppCompatActivity {

    TextView title;
    ViewPager2 salaryView;
    ImageButton backBtn;
    Button chartBtn;

    ArrayList<String> employee_names;
    ArrayList<Integer> salaries;

    MutiSalaryAdapter msAdapter;

    String year, month;

    boolean init = true;

    private Context context = this;
    String TAG = "Bonus_View";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus__view);
        Button total_btn = findViewById(R.id.total_salary_btn);

        title = findViewById(R.id.title_BV);
        salaryView = findViewById(R.id.salaryList_BV);
        backBtn = findViewById(R.id.back_imgBtn);
        chartBtn = findViewById(R.id.chart_btn_BV);

        year = getToday("yyyy");
        month = getToday("MM");
        title.setText("員工薪資一覽 "+month+"月");
        setList();

        chartBtn.setOnClickListener(v -> setChart());

        backBtn.setOnClickListener(v -> finish());


        total_btn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("year", year);
            bundle.putString("month", month);
            Intent intent = new Intent();
            intent.setClass(context, Bonus_List_Detail.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        globalNav();
    }

    private ArrayList getData(String year, String month){
//        employee_names = new ArrayList<>();
//        salaries = new ArrayList<>();
        ArrayList<String[]> data = new ArrayList<>();

        String function_name = "pay_oneMonth";
        String company_id = getCompany_id(context);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", company_id)
                .add("year", year)
                .add("month", month)
                .build();
        Log.i(TAG, "company_id: "+company_id+", year: "+year+", month: "+month);

        //連線要求
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
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
                //在app畫面上呈現錯誤訊息
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.i(TAG,"responseData: "+responseData); //顯示資料

//                employee_names.add("");
//                salaries.add(0);

                try {
                    JSONArray responseArr = new JSONArray(responseData);

                    //一筆一筆的取JSONArray中的json資料
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject staff = responseArr.getJSONObject(i);
                        Log.i(TAG,"staff: "+staff);

                        String staff_name = staff.getString("staff_name");
                        String total_payment = staff.getString("total_payment");
//                        employee_names.add(staff_name);
////                        salaries.add(Integer.parseInt(total_payment));

                        String[] row_data = {staff_name, total_payment};
                        data.add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(data.size() < 1){
                    String[] row_data = {"沒有資料", ""};
                    data.add(row_data);
                }

                int i;
                Log.d(TAG, year+"/"+month+", data:"+data.size());
                for(i = 0; i < data.size(); i++) {
                    Log.d(TAG, Arrays.toString(data.get(0)));
                }

                if(init){
                    runOnUiThread(() -> {
                        if(!data.get(0)[1].isEmpty()) setChart(); //圖表
                        init = false;
                    });
                }

            }
        });

        return data;
    }

    private void setChart(){ //圖表
        msAdapter.showDatas();
        int currentItem = salaryView.getCurrentItem();
        employee_names = msAdapter.getStaffName(currentItem);
        salaries = msAdapter.getStaffPay(currentItem);
        Log.d(TAG, "employee_names, salaries: ");
        int i;
        for(i = 0; i < employee_names.size() && i < salaries.size(); i++){
            Log.d(TAG, i+"."+employee_names.get(i)+", "+salaries.get(i));
        }
        Log.d(TAG, "now page: "+currentItem);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.bonus_dialog);

        if(salaries.size() > 0) setBarChart(dialog);
        else dialog.setTitle("no data");

        Button dialog_btn = dialog.findViewById(R.id.check_dialog_btn);
        dialog_btn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setLayout(dip2px(context, 375), dip2px(context, 500)); //1400, 2000
        dialog.setCanceledOnTouchOutside(true); //點其他地方也能取消
        dialog.show();

//        chartBtn.setOnClickListener(v -> dialog.show());
    }

    private void setBarChart(Dialog dialog){
        HorizontalBarChart hBarChart = dialog.findViewById(R.id.hBarChart_BD);

        BarDataSet barDataSet = new BarDataSet(getBarData(), ""); //放入資料(資料List, 該資料的標籤名稱)
        barDataSet.setValueTextSize(20); //數值文字大小
        barDataSet.setValueFormatter(new IndexAxisValueFormatter(){ //去掉數值小數點
            @Override
            public String getFormattedValue(float value) {
                if(value == 0) return "";
                return String.valueOf((int) value);
            }
        });
        barDataSet.setDrawValues(true); //在bar上顯示數值
        barDataSet.setColor(Color.parseColor("#FB8527")); //bar的顏色
        barDataSet.setValueTextColor(Color.parseColor("#FFE7D3")); //數值文字顏色

        BarData barData = new BarData(barDataSet);
        barData.setHighlightEnabled(false); //不能點擊反光

        XAxis xAxis = hBarChart.getXAxis(); //X軸
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //X軸的位置(放下面)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(employee_names)); //將x軸的值當labels的key
        xAxis.setLabelCount(employee_names.size()); //X軸刻度數量
        xAxis.setDrawGridLines(false); //取消垂直網格線
        xAxis.setTextSize(20); //標籤文字大小(sp)

        YAxis yAxis = hBarChart.getAxisLeft();
        yAxis.setSpaceTop(0f); //最高bar上方留空多少%
        yAxis.setDrawAxisLine(false); //軸線顯示
        yAxis.setDrawGridLines(false); //取消水平網格線
//        yAxis.setDrawLabels(false); //Y軸標籤顯示
        yAxis.setGranularity(getAverage());

        LimitLine ll = new LimitLine(getAverage()); //平均線
        ll.setLineColor(Color.parseColor("#19B0ED")); //線的顏色
        ll.setLineWidth(2f); //線的寬度
        yAxis.addLimitLine(ll); //加入平均線

        Legend legend = hBarChart.getLegend(); //圖例
        legend.setEnabled(false); //不顯示圖例

        hBarChart.getAxisRight().setEnabled(false); //取消右側(下方)Y軸
        hBarChart.getDescription().setEnabled(false); //不顯示描述文字
        hBarChart.setDrawValueAboveBar(false); //數值放在bar上方(true:above bar)或頂端(false:top)
        hBarChart.setScaleEnabled(false); //不能縮放

        hBarChart.setData(barData); //圖表放入資料
    }

    private ArrayList getBarData(){
        ArrayList<BarEntry> yValue = new ArrayList<>(); //資料List
        for(int x = 0; x < employee_names.size() && x < salaries.size(); x++){
            yValue.add(new BarEntry(x, salaries.get(x))); //加入新資料
            Log.d(TAG, "staff_name: "+employee_names.get(x)+", total_payment: "+salaries.get(x));
        }
        return yValue;
    }

    private float getAverage(){
        float max = 0;
        for(int i = 0; i < salaries.size(); i++) {
            max = max + salaries.get(i);
        }
        float average = max / (salaries.size()-1);
        return average;
    }

    private void setList(){
        msAdapter = new MutiSalaryAdapter(getData(year, month), Integer.parseInt(month));
        salaryView.setAdapter(msAdapter);
        salaryView.setCurrentItem(1);
        msAdapter.showDatas();

        salaryView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                title.setText("員工薪資一覽 "+msAdapter.getMonth(position)+"月");
                //新增最後一頁
                if(position == msAdapter.getItemCount()-1) {
                    int m = msAdapter.getMonth(position-1, 1);
                    month = intToString(m);
                    if(month.equals("01")){
                        int y = Integer.parseInt(year);
                        year = String.valueOf(++y);
                    }
                    Log.d(TAG, "add next page: "+year+"/"+month+"("+position+")");
                    ArrayList<String[]> temp = getData(year, month);
                    salaryView.post(() -> {
                        int i = 0;
                        while(temp.size() == 0){
                            if((++i)%1000000 == 0)
                                Log.d(TAG, i+". "+year+"/"+month+", temp data:"+temp.size());
                        }
                        msAdapter.setmData(temp, m, position);
                        title.setText("員工薪資一覽 "+msAdapter.getMonth(position)+"月");
                        msAdapter.addPage(new ArrayList<>());
                    });

                }
                //新增前一頁
                if(position == 0) {
                    int m = msAdapter.getMonth(1, 2);
                    month = intToString(m);
                    if(month.equals("12")){
                        int y = Integer.parseInt(year);
                        year = String.valueOf(--y);
                    }
                    Log.d(TAG, "add last page: "+year+"/"+month+"("+position+")");
                    ArrayList<String[]> temp = getData(year, month);
                    salaryView.post(() -> {
                        int i = 0, num = 10000000;
                        while(temp.size() == 0){
                            if((++i)%num == 0)
                                Log.d(TAG, (i/num)+". "+year+"/"+month+", temp data:"+temp.size());
                        }
                        msAdapter.setmData(temp, m, position);
                        title.setText("員工薪資一覽 "+msAdapter.getMonth(1)+"月");
                        msAdapter.addPage(new ArrayList<>(), 0);
                        salaryView.setCurrentItem(1, false);
                    });
                }
            }
        });
    }

    private String intToString(int month) {
        String monthStr = String.valueOf(month);
        if(month < 10) monthStr = "0"+monthStr;
        return monthStr;
    }

    private void globalNav(){
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);
        //底下nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Bonus_View.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Bonus_View.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Bonus_View.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Bonus_View.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Bonus_View.this, Setting.class);
            startActivity(setting_intent);
        });
    }
}
