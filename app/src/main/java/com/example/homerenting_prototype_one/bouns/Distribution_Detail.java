package com.example.homerenting_prototype_one.bouns;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.re_adpater.DistributionAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.valuation.Valuation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getDate;
import static com.example.homerenting_prototype_one.show.global_function.getTime;

public class Distribution_Detail extends AppCompatActivity {
    TextView nameText, nameTitleText, fromAddressText, toAddressText, movingTimeText, feeText;
    TextView csalaryPText, csalaryText, dsalaryText;
    EditText csalaryPEdit, csalaryEdit;
    RecyclerView salaryDistribution;
    ImageButton backBtn;
    Button checkBtn;

    Bundle bundle;

    private DistributionAdapter distributionAdapter;

    private String order_id, name, gender, movingTime, fromAddress, toAddress, fee;
    private String TAG = "Distribution_Detail";

    private ArrayList<String> staffs, staffIds;
    private ArrayList<Integer> salaries;
    int net;
    boolean lock = false;
    boolean init = true;

    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution__detail);

        linking();

//        bundle = new Bundle();
//        bundle.putString("order_id", "147");
        bundle = getIntent().getExtras();
        order_id = bundle.getString("order_id");

        staffs = new ArrayList<>();
        staffIds = new ArrayList<>();
        salaries = new ArrayList<>();

        //傳值
        getData();

        backBtn.setOnClickListener(v -> finish());

        globalNav();
    }

    private void getData(){
        String function_name = "order_detail";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id", getCompany_id(this))
                .build();
        Log.d(TAG, "order_id:"+order_id);

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

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of order_detail: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);
                    JSONObject order = responseArr.getJSONObject(0);
                    Log.i(TAG,"order:"+order);

                    //取得資料
                    name = order.getString("member_name");
                    gender = order.getString("gender");
                    movingTime = getDate(order.getString("moving_date"))+" "+getTime(order.getString("moving_date"));
                    fromAddress = order.getString("from_address");
                    toAddress = order.getString("to_address");
                    fee = order.getString("accurate_fee");

                    int i;
                    //跳過車輛
                    for (i = 1; i < responseArr.length(); i++) {
                        JSONObject vehicle_assign = responseArr.getJSONObject(i);
                        if(!vehicle_assign.has("vehicle_type")) break;
                    }

                    //取得員工資訊
                    for (; i < responseArr.length(); i++) {
                        JSONObject staff_assign = responseArr.getJSONObject(i);
                        if(!staff_assign.has("staff_id")) break;
                        Log.i(TAG, "staff:" + staff_assign);
                        staffs.add(staff_assign.getString("staff_name"));
                        staffIds.add(staff_assign.getString("staff_id"));

                        String pay = staff_assign.getString("pay");
                        salaries.add(Integer.parseInt(pay));
                    }
                    salaries.add(-1); //公司分潤

                    //顯示基本資訊
                    runOnUiThread(() -> {
                        nameText.setText(name);
                        if(gender.equals("female")) nameTitleText.setText("小姐");
                        else if(gender.equals("male")) nameTitleText.setText("先生");
                        else nameTitleText.setText("");
                        movingTimeText.setText(movingTime);
                        fromAddressText.setText(fromAddress);
                        toAddressText.setText(toAddress);
                        feeText.setText(fee);
                        dsalaryText.setText(fee);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //顯示安排人力
                distributionAdapter = new DistributionAdapter(staffs);
                runOnUiThread(() -> {
                    salaryDistribution.setLayoutManager(new LinearLayoutManager(context));
                    salaryDistribution.setAdapter(distributionAdapter);
                    Log.d(TAG, "setAdapter");
                });

                for(int i = 0; distributionAdapter.getReady() != -1; i++){ //lock
                    if(i%1000000 == 0) Log.d(TAG, "ready: "+distributionAdapter.getReady());
                }

                lock = true;
                setCsalaryEdit();
                for(int i = 0; lock; i++){ //lock
                    if(i%1000000 == 0) Log.d(TAG, "waiting for company distribution: "+csalaryText.getText().toString());
                }
                for(int i = 0; i < distributionAdapter.getItemCount(); i++)
                    getItem(i); //取得分配薪水的edittext
                setCheckBtn(); //設置確認送出按鈕
            }
        });
    }

    private void getItem(final int position){
        View view = salaryDistribution.getLayoutManager().findViewByPosition(position);
        if(view != null){
            runOnUiThread(() -> feeText.setText(fee));
            Log.d(TAG, "view "+position+" is not null");
            EditText salaryPEdit = view.findViewById(R.id.salaryP_DI);
            TextView salaryPText = view.findViewById(R.id.salaryP_text_DI);
            EditText salaryEdit = view.findViewById(R.id.salary_DI);
            TextView salaryText = view.findViewById(R.id.salary_text_DI);

            setSalaryEdit(salaryPEdit, salaryPText, salaryEdit, salaryText, position);
            if(init && (salaries.get(position) == -1 || salaries.get(position) == 0)){
                float divP = 100f/(salaries.size()-1);
                int ds = Integer.parseInt(fee)-salaries.get(salaries.size()-1);
                int div = Math.round((float) ds/(salaries.size()-1));
                runOnUiThread(() -> {
                    salaryPText.setText(String.valueOf(divP));
                    salaryPEdit.setText(String.valueOf(divP));
                    salaryText.setText(String.valueOf(div));
                    salaryEdit.setText(String.valueOf(div));
                });
                Log.d(TAG, position+". init "+divP+"%("+div+")");
                salaries.set(position, div);
                if(position == salaries.size()-1){
                    init = false;
                    Log.d(TAG, "init finish(position "+position+")");
                }
            }

            if(salaries.get(position) != -1)
                runOnUiThread(() -> salaryEdit.setText(String.valueOf(salaries.get(position))));
        }
        else{
            runOnUiThread(() -> {
                feeText.setText(fee+"("+position+" null)");
                Log.d(TAG, "view "+position+" is null");
                getItem(position);
            });
        }
    }

    private void setCsalaryEdit() {
        runOnUiThread(() -> csalaryPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                int ds = Integer.parseInt(fee); //剩餘可分配金額
                String salaryPStr = csalaryPEdit.getText().toString(); //取得百分比的值(string)
                if(salaryPStr.isEmpty()) salaryPStr = "0";
                Log.d(TAG, "(csp) salary(p): "+salaryPStr);

                int salary = -1; //會放到arraylist的值，沒有值預設是-1
                String salaryStr = "0"; //會顯示出來的值，沒有值預設是0
                if(!salaryPStr.equals("0")){
                    float salaryP = Float.parseFloat(salaryPStr); //取得百分比的值(float)
                    salary = Math.round((Integer.parseInt(fee))*salaryP/100); //算出數值
                    ds = Integer.parseInt(fee)-salary; //取得剩餘可分配金額
                    if(salary == 0) salary = -1; //如果salary是0，就回復原狀
                    else salaryStr = String.valueOf(salary); //否則取得salary的值(string)
                }
                csalaryText.setText(salaryStr); //顯示根據百分比連動的數值

                salaries.set(salaries.size()-1, salary); //修改arraylist中的公司金額
//                setFeeText(); //修改金額的算式

                dsalaryText.setText(String.valueOf(ds)); //顯示剩餘可分配金額

                //修改員工的百分比或數值
                for(int i = 0; i < distributionAdapter.getItemCount(); i++)
                    snycESalary(i);
            }
        }));

        runOnUiThread(() -> csalaryPText.setOnClickListener(v -> {
            String salaryPStr = csalaryPText.getText().toString(); //取得百分比
            csalaryPEdit.setText(salaryPStr);  //同步Edit版版分比
            String salaryStr = csalaryEdit.getText().toString(); //取得數值
            if(salaryStr.isEmpty()) salaryStr = "0";
            csalaryText.setText(salaryStr); //同步文字版數值

            //改為修改百分比模式
            csalaryPText.setVisibility(View.GONE);
            csalaryPEdit.setVisibility(View.VISIBLE);
            csalaryEdit.setVisibility(View.GONE);
            csalaryText.setVisibility(View.VISIBLE);
        }));

        runOnUiThread(() -> csalaryEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                int ds = Integer.parseInt(fee); //剩餘可分配金額

                String salaryStr = csalaryEdit.getText().toString(); //取得數值
                int csalary = -1;
                if(!salaryStr.isEmpty()) {
                    csalary = Integer.parseInt(salaryStr); //將數值轉換成integer
                    ds = Integer.parseInt(fee)-csalary; //取得剩餘可分配金額
                }
                Log.d(TAG, "(s) salary: "+csalary);
                salaries.set(salaries.size()-1, csalary); //修改arraylist中的公司金額
//                        setFeeText(); //修改金額的算式

                String csalaryPStr = "0";
                if(csalary != -1 && csalary != 0) {
                    float salaryP =  ((float) csalary/(Integer.parseInt(fee)))*100; //算出百分比
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2); //只顯示到小數點後兩位
                    csalaryPStr = nf.format(salaryP); //取得百分比(string)
                    Log.d(TAG, "company. salary percent("+csalary+"/"+fee+"): "+csalaryPStr);
                }
                csalaryPText.setText(csalaryPStr); //顯示百分比

                dsalaryText.setText(String.valueOf(ds)); //顯示剩餘可分配金額

                //修改員工的百分比或數值
                for(int i = 0; i < distributionAdapter.getItemCount(); i++)
                    snycESalary(i);
            }
        }));

        runOnUiThread(() -> csalaryText.setOnClickListener(v -> {
            String salaryPStr = csalaryPEdit.getText().toString(); //取得Edit版百分比
            csalaryPText.setText(salaryPStr); //同步文字版百分比
            String salaryStr = csalaryText.getText().toString(); //取得文字版數值
            csalaryEdit.setText(salaryStr); //同步Edit版數值

            //改為修改數值模式
            csalaryPText.setVisibility(View.VISIBLE);
            csalaryPEdit.setVisibility(View.GONE);
            csalaryEdit.setVisibility(View.VISIBLE);
            csalaryText.setVisibility(View.GONE);
        }));
        getCompanyDetail();
    }

    private void getCompanyDetail(){
        String function_name = "company_detail";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(this))
                .build();
        Log.d(TAG, "order_id:"+order_id);

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

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG,"responseData of company: "+responseData); //顯示資料

                JSONArray responseArr = null;
                try {
                    responseArr = new JSONArray(responseData);
                    JSONObject company = responseArr.getJSONObject(0);
                    Log.i(TAG,"company: "+company);

                    String company_dis = company.getString("last_distribution");
                    runOnUiThread(() -> {
                        csalaryPEdit.setText(company_dis);
                        csalaryPText.setText(company_dis);

                        int ds = Integer.parseInt(fee); //剩餘可分配金額
                        String salaryPStr = company_dis; //取得百分比的值(string)
                        if(salaryPStr.isEmpty()) salaryPStr = "0";
                        Log.d(TAG, "(csp) salary(p): "+salaryPStr);

                        int salary = -1; //會放到arraylist的值，沒有值預設是-1
                        String salaryStr = "0"; //會顯示出來的值，沒有值預設是0
                        if(!salaryPStr.equals("0")){
                            float salaryP = Float.parseFloat(salaryPStr); //取得百分比的值(float)
                            salary = Math.round((Integer.parseInt(fee))*salaryP/100); //算出數值
                            ds = Integer.parseInt(fee)-salary; //取得剩餘可分配金額
                            if(salary == 0) salary = -1; //如果salary是0，就回復原狀
                            else salaryStr = String.valueOf(salary); //否則取得salary的值(string)
                        }
                        csalaryText.setText(salaryStr); //顯示根據百分比連動的數值
                        csalaryEdit.setText(salaryStr);

                        salaries.set(salaries.size()-1, salary); //修改arraylist中的公司金額
                        dsalaryText.setText(String.valueOf(ds)); //顯示剩餘可分配金額
                        lock = false;
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void snycESalary(int position){
        View view = salaryDistribution.getLayoutManager().findViewByPosition(position);
        if(view != null) {
            EditText salaryPEdit = view.findViewById(R.id.salaryP_DI);
            TextView salaryPText = view.findViewById(R.id.salaryP_text_DI);
            EditText salaryEdit = view.findViewById(R.id.salary_DI);
            TextView salaryText = view.findViewById(R.id.salary_text_DI);

            int ds = Integer.parseInt(dsalaryText.getText().toString()); //取得剩餘可分配金額
            int salary = -1;
            if (salaryPEdit.getVisibility() == View.VISIBLE) {
                String salaryStr = salaryEdit.getText().toString();
                if(salaryStr.isEmpty()) salary = 0;
                else salary = Integer.parseInt(salaryStr);
                String salaryPStr = "0";
                if (salary != -1 && salary != 0) {
                    float salaryP = ((float) salary / ds) * 100; //算出百分比
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2); //只顯示到小數點後兩位
                    salaryPStr = nf.format(salaryP); //取得百分比(string)
                    Log.d(TAG, (position + 1) + ". salary percent(" + salary + "/" + ds + "): " + salaryPStr);
                }
                salaryPEdit.setText(salaryPStr); //顯示百分比
            }
            else {
                String salaryPStr = salaryPEdit.getText().toString();
                float salaryP = 0f;
                if(!salaryPStr.isEmpty()) salaryP = Float.parseFloat(salaryPStr+"f");
                String salaryStr = "0";
                if(salaryP != 0){
                    salary = Math.round(salaryP*ds/100);
                    salaryStr = String.valueOf(salary);
                    Log.d(TAG, (position+1)+". salary: "+salaryP+" * "+ds+" = "+salaryStr);
                }
                salaryEdit.setText(salaryStr);
            }
            salaries.set(position, salary); //修改arraylist中的員工金額
//            setFeeText(); //修改金額的算式
        }
        else{
            Log.d(TAG, "view "+position+" is null");
            snycESalary(position);
        }
    }

    private void setSalaryEdit(EditText salaryPEdit, TextView salaryPText, EditText salaryEdit, TextView salaryText, int position){
        runOnUiThread(() -> salaryPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String salaryPStr = salaryPEdit.getText().toString(); //取得百分比的值(string)
                if(salaryPStr.isEmpty()) salaryPStr = "0"; //轉成數值
                Log.d(TAG, "(sp) salary(p): "+salaryPStr);

                int salary = -1; //會放到arraylist的值，沒有值預設是-1
                String salaryStr = "0"; //會顯示出來的值，沒有值預設是0
                if(!salaryPStr.equals("0")){
                    float salaryP = Float.parseFloat(salaryPStr); //取得百分比的值(float)
                    int ds = Integer.parseInt(dsalaryText.getText().toString()); //取得剩餘可分配金額
                    salary = Math.round(ds*salaryP/100);
                    if(salary == 0) salary = -1; //如果salary是0，就回復原狀
                    else salaryStr = String.valueOf(salary); //否則取得salary的值(string)
                }
                salaryText.setText(salaryStr); //顯示根據百分比連動的數值
                salaries.set(position, salary); //修改arraylist中的員工金額
//                setFeeText(); //修改金額的算式
            }
        }));

        runOnUiThread(() -> salaryPText.setOnClickListener(v -> {
            String salaryPStr = salaryPText.getText().toString(); //取得百分比的值(string)
            salaryPEdit.setText(salaryPStr); //修改Edit版百分比的值(與文字版同步)
            String salaryStr = salaryEdit.getText().toString(); //取得數值
            if(salaryStr.isEmpty()) salaryStr = "0";
            salaryText.setText(salaryStr); //修改文字版數值的值(與Edit版同步)

            //改為修改百分比模式
            salaryPText.setVisibility(View.GONE);
            salaryPEdit.setVisibility(View.VISIBLE);
            salaryEdit.setVisibility(View.GONE);
            salaryText.setVisibility(View.VISIBLE);
        }));

        runOnUiThread(() -> salaryEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String salaryStr = salaryEdit.getText().toString(); //取得數值
                int salary = -1;
                if(!salaryStr.isEmpty()) salary = Integer.parseInt(salaryStr); //將數值轉換成integer
                Log.d(TAG, "(s) salary: "+salary);
                salaries.set(position, salary); //修改arraylist中的員工金額
//                setFeeText(); //修改金額的算式

                String salaryPStr = "0";
                if(salary != -1 && salary != 0) {
                    int ds = Integer.parseInt(dsalaryText.getText().toString()); //取得剩餘可分配金額
                    float salaryP =  ((float) salary/ds)*100; //算出百分比
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2); //只顯示到小數點後兩位
                    salaryPStr = nf.format(salaryP); //取得百分比(string)
                    Log.d(TAG, (position+1)+". salary percent("+salary+"/"+ds+"): "+salaryPStr);
                }
                salaryPText.setText(salaryPStr); //顯示百分比
            }
        }));

        runOnUiThread(() -> salaryText.setOnClickListener(v -> {
            String salaryPStr = salaryPEdit.getText().toString();
            salaryPText.setText(salaryPStr);
            String salaryStr = salaryText.getText().toString();
            salaryEdit.setText(salaryStr);

            salaryPText.setVisibility(View.VISIBLE);
            salaryPEdit.setVisibility(View.GONE);
            salaryEdit.setVisibility(View.VISIBLE);
            salaryText.setVisibility(View.GONE);
        }));
    }

    private void setFeeText(){
        int total = 0;
        for(int i = 0; i < salaries.size(); i++){
            if(salaries.get(i) == -1) continue;
            total = total + salaries.get(i);
        }
        if(total > 0) net = Integer.parseInt(fee) - total;
        else net = Integer.parseInt(fee) + total;

        if(total == 0) feeText.setText(fee);
        else{
            double percentage = 100;
            if(net != 0) percentage = (net/(double)Integer.parseInt(fee))*100;
            DecimalFormat df = new DecimalFormat("0.00");
            String p = df.format(percentage);
            if(total > 0) feeText.setText(fee+" - "+total+" = "+net+"("+p+"%)");
            else feeText.setText(fee+" "+total+" = "+net+"("+p+"%)");
        }
    }

    private void setCheckBtn(){
        checkBtn.setOnClickListener(v -> {
            if(net < 0) Toast.makeText(context, "工錢總額大於搬家費用", Toast.LENGTH_SHORT).show();
            else{
                boolean checkAll = true;
                for(int i = 0; i < salaries.size()-1; i++){
                    Log.i(TAG, "salaries("+i+"): "+salaries.get(i));
                    if(salaries.get(i) == -1){
                        checkAll = false;
                        Log.d(TAG, "not complete thd distribution");
                        continue;
                    }
                    update_staff_salary(i);
                    updateComDis();
                }
                if((net == 0 || net < 10) && checkAll){
                    Log.d(TAG, "complete, finish the distirbution");
                    changeStatus();

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent intent = new Intent(context, Bonus_Distribution.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
                    }, 1000);
                }
                else Toast.makeText(context, "金額尚未分潤完畢", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update_staff_salary(final int i){
        String function_name = "distribute_pay";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("staff_id", staffIds.get(i))
                .add("pay", String.valueOf(salaries.get(i)))
                .build();
        Log.d(TAG, "order_id: "+order_id+", staff_id: "+staffIds.get(i)+", pay: "+salaries.get(i));

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
                Log.d(TAG, "responseData of update_staff_salary("+i+"): " + responseData);
            }
        });
    }

    private void updateComDis(){
        String company_dis = csalaryPText.getText().toString();
        if(csalaryPEdit.getVisibility() == View.VISIBLE) company_dis = csalaryPEdit.getText().toString();
        String function_name = "update_companyDistribute";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .add("last_distribution", company_dis)
                .build();
        Log.i(TAG, "company_distribution: "+company_dis);

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
                runOnUiThread(() -> Toast.makeText(context, "連線失敗", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG, "responseData of update company distribution: " + responseData);
            }
        });
    }

    private void changeStatus(){
        String function_name = "change_status";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .add("company_id", getCompany_id(context))
                .add("table", "orders")
                .add("status", "paid")
                .build();
        Log.d(TAG, "order_id: "+order_id+", table: orders, status: paid");

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
                Log.d(TAG, "responseData of change_status: " + responseData);
            }
        });
    }

    private void linking(){
        nameText = findViewById(R.id.member_name_DD);
        nameTitleText = findViewById(R.id.nameTitle_DD);
        fromAddressText = findViewById(R.id.fromAddress_DD);
        toAddressText = findViewById(R.id.toAddress_DD);
        movingTimeText = findViewById(R.id.movingTime_DD);
        feeText = findViewById(R.id.price_DD);
        csalaryPText = findViewById(R.id.csalaryP_text_DD);
        csalaryPEdit = findViewById(R.id.csalaryP_DD);
        csalaryText = findViewById(R.id.csalary_text_DD);
        csalaryEdit = findViewById(R.id.csalary_DD);
        dsalaryText = findViewById(R.id.distribution_price_DD);
        salaryDistribution = findViewById(R.id.salaryDistribution_DD);
        backBtn = findViewById(R.id.back_imgBtn);
        checkBtn = findViewById(R.id.check_bonus_btn);
    }

    private void globalNav(){
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);
        //底下nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Distribution_Detail.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Distribution_Detail.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Distribution_Detail.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Distribution_Detail.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Distribution_Detail.this, Setting.class);
            startActivity(setting_intent);
        });
    }
}
