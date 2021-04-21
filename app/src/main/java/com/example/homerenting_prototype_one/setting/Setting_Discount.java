package com.example.homerenting_prototype_one.setting;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.valuation.Valuation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

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
import static com.example.homerenting_prototype_one.show.global_function.getMonth;
import static com.example.homerenting_prototype_one.show.global_function.getToday;
import static com.example.homerenting_prototype_one.show.global_function.getYear;

public class Setting_Discount extends AppCompatActivity {
    TableLayout discountTable;
    Switch valuate, deposit, cancel;
    Button checkBtn, addBtn, deleteBtn;
    ImageButton backBtn;

    ArrayList<String[]> period_discounts;
    ArrayList<String> delete_discounts;

    boolean deleteMode = false, disable = false;

    final int DELETE_INDEX = 0, NAME_INDEX = 1, SWITCH_INDEX = 2, DISCOUNT_INDEX = 3, START_INDEX = 5, END_INDEX = 7, ID_INDEX = 8;
    final int START_TIME_VIEW = 0, END_TIME_VIEW = 1;

    String TAG = "Setting_Discount";
    Context context = this;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__discount);

        discountTable = findViewById(R.id.discount_table_SDC);
        checkBtn = findViewById(R.id.check_btn_SDC);
        backBtn = findViewById(R.id.back_imgBtn);
        addBtn = findViewById(R.id.add_btn_SDC);
        deleteBtn = findViewById(R.id.delete_discount_btn);

        period_discounts = new ArrayList<>();
        delete_discounts = new ArrayList<>();

        getFreeRow();
        getPeriodRow(true);
        getData();
        addBtn.setOnClickListener(v -> {
            //輸入欄
            final EditText discount_edit = new EditText(context);
            //設定margin
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 80;
            params.rightMargin = params.leftMargin;
            discount_edit.setLayoutParams(params);
            //要放到FrameLayout裡，margin才有用
            FrameLayout container = new FrameLayout(context);
            container.addView(discount_edit);

            new AlertDialog.Builder(context)
                    .setTitle("新增優惠")
                    .setView(container)
                    .setPositiveButton("確認", (dialog, which) -> {
                        String new_discount_name = discount_edit.getText().toString();
                        if(new_discount_name.isEmpty()){
                            Toast.makeText(context, "未輸入優惠名稱", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String thisYear = getToday("yyyy");String[] period_discount = {"-1", new_discount_name, "-1", thisYear+"-01-01", thisYear+"-12-31", "false"};
                        period_discounts.add(period_discount);
                        Log.d(TAG, "add period_discount("+(period_discounts.size()-1)+"/"+period_discounts.size()+"): "
                                +Arrays.toString(period_discounts.get(period_discounts.size()-1)));
                        runOnUiThread(() -> discountTable.addView(addNewRow("-1", new_discount_name, -1, thisYear+"-01-01", thisYear+"-12-31", false, discountTable.getChildCount())));
                    })
                    .setNegativeButton("取消",null)
                    .create()
                    .show();
        });

        deleteBtn.setOnClickListener(v -> {
            if(!deleteMode){
                for(int i = 0; i < discountTable.getChildCount(); i++){
                    TableRow tr = (TableRow) discountTable.getChildAt(i);
                    Button db = (Button) tr.getChildAt(0);
                    db.setVisibility(View.VISIBLE);
                }
                deleteMode = true;
            }
            else{
                for(int i = 0; i < discountTable.getChildCount(); i++){
                    TableRow tr = (TableRow) discountTable.getChildAt(i);
                    Button db = (Button) tr.getChildAt(0);
                    db.setVisibility(View.GONE);
                }
                deleteMode = false;
            }
        });

        checkBtn.setOnClickListener(v -> {
            Log.d(TAG, "valuate is "+valuate.isChecked());
            Log.d(TAG, "deposit is "+deposit.isChecked());
            Log.d(TAG, "cancel is "+cancel.isChecked());

            getPeriodRow(false);

            Log.d(TAG, "size of period discount: "+period_discounts.size());
            Log.d(TAG, "period discount: "+itemsToString(period_discounts));

            Log.d(TAG, "size of delete discount: "+delete_discounts.size());
            Log.d(TAG, "delete discount: "+delete_discounts);

            updateDiscount();
            finish();
        });


        backBtn.setOnClickListener(v -> {
            Log.d(TAG, "valuate is "+valuate.isChecked());
            Log.d(TAG, "deposit is "+deposit.isChecked());
            Log.d(TAG, "cancel is "+cancel.isChecked());

            getPeriodRow(false);

            Log.d(TAG, "size of period discount: "+period_discounts.size());
            Log.d(TAG, "period discount: "+itemsToString(period_discounts));

            Log.d(TAG, "size of delete discount: "+delete_discounts.size());
            Log.d(TAG, "delete discount: "+delete_discounts);
        });

        globalNav();
    }

    private void getFreeRow(){
        for(int i = 0; i < 3; i++){
            TableRow freeItem = (TableRow) discountTable.getChildAt(i);
            int switchPosition = 2;
            switch (i){
                case 0:
                    valuate = (Switch) freeItem.getChildAt(switchPosition);
                    break;
                case 1:
                    deposit = (Switch) freeItem.getChildAt(switchPosition);
                    break;
                case 2:
                    cancel = (Switch) freeItem.getChildAt(switchPosition);
                    break;
            }
        }
        Log.d(TAG, "valuate is "+valuate.isChecked());
        Log.d(TAG, "deposit is "+deposit.isChecked());
        Log.d(TAG, "cancel is "+cancel.isChecked());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPeriodRow(boolean init){
        for(int i = 3; i < discountTable.getChildCount(); i++){
            final String[] period_discount = getRowData(i);
            if(init){
                final int finalI = i-3;
                final TableRow discountItem = (TableRow) discountTable.getChildAt(i);
                final Switch switcher = (Switch) discountItem.getChildAt(SWITCH_INDEX);
                final TextView startView = (TextView) discountItem.getChildAt(START_INDEX);
                final TextView endView = (TextView) discountItem.getChildAt(END_INDEX);

                setSwitch(switcher, startView, endView, finalI, period_discount);

                if(switcher.isChecked()){
                    startView.setOnClickListener(null);
                    endView.setOnClickListener(null);
                }
                else{
                    setDateBtn(startView, START_TIME_VIEW, finalI);
                    setDateBtn(endView, END_TIME_VIEW, finalI);
                }
                period_discounts.add(period_discount);
                Log.d(TAG, "add period_discount("+(period_discounts.size()-1)+"/"+period_discounts.size()+"): "
                        +Arrays.toString(period_discounts.get(period_discounts.size()-1)));

                final Button deleteBtn = (Button) discountItem.getChildAt(DELETE_INDEX);
                deleteBtn.setOnClickListener(v -> {
                    discountTable.removeView(discountItem);
                    delete_discounts.add(period_discount[0]);
                    period_discounts.remove(finalI);
                });
            }
            else period_discounts.set((i-3), period_discount);
        }
    }

    private String[] getRowData(int i){
        TableRow discountItem = (TableRow) discountTable.getChildAt(i);
        //0 : X
        TextView nameText = (TextView) discountItem.getChildAt(NAME_INDEX);
        Switch enableSw = (Switch) discountItem.getChildAt(SWITCH_INDEX);
        EditText discountEdit = (EditText) discountItem.getChildAt(DISCOUNT_INDEX);
        //4 : %
        TextView startView = (TextView) discountItem.getChildAt(START_INDEX);
        //6 : ─
        TextView endView = (TextView) discountItem.getChildAt(END_INDEX);
        TextView discountIdText = (TextView) discountItem.getChildAt(ID_INDEX);

        String name = nameText.getText().toString();
        if(name.isEmpty()) name = "";
        String enable = String.valueOf(enableSw.isChecked());
        if(enable.isEmpty()) enable = "";
        String discount = discountEdit.getText().toString();
        if(discount.isEmpty()) discount = "0";
        String startTime = startView.getText().toString();
        if(startTime.isEmpty()) startTime = "";
        String endTime = endView.getText().toString();
        if(endTime.isEmpty()) endTime = "";
        String discountId = discountIdText.getText().toString();

        String[] period_discount = {discountId, name, discount, startTime, endTime, enable};
        return period_discount;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setSwitch(final Switch switcher, final TextView startView, final TextView endView, final int index, final String[] period_discount){
        switcher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(switcher.isChecked()){
                startView.setOnClickListener(null);
                endView.setOnClickListener(null);
            }
            else{
                setDateBtn(startView, START_TIME_VIEW, index);
                setDateBtn(endView, END_TIME_VIEW, index);
                String startDate = startView.getText().toString();
                String endDate = endView.getText().toString();
                final LocalDate disableTime = LocalDate.now();
                LocalDate start = LocalDate.of(Integer.parseInt(getYear(startDate)), Integer.parseInt(getMonth(startDate)), Integer.parseInt(getDay(startDate)));
                LocalDate end = LocalDate.of(Integer.parseInt(getYear(endDate)), Integer.parseInt(getMonth(endDate)), Integer.parseInt(getDay(endDate)));

                if(start.isBefore(disableTime) && end.isAfter(disableTime) && !disable){
                    runOnUiThread(() -> switcher.setChecked(true));

                    Log.d(TAG, start+" < "+disableTime+" < "+end);
                    new AlertDialog.Builder(context)
                            .setTitle("現在進行折扣中，要強制取消嗎？")
                            .setPositiveButton("確認", (dialog, which) -> runOnUiThread(() -> {
                                disable = true;
                                switcher.setChecked(false);
                                disable = false;
                            }))
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
                }
                else Log.d(TAG, "start: "+start+", end: "+end+", disableTime: "+disableTime);
            }
        });
    }

    private void setDateBtn(final TextView dateBtn, int type, int index){
        Log.d(TAG, "setDateBtn: "+index);
        dateBtn.setOnClickListener(v -> {
            GregorianCalendar calendar = new GregorianCalendar();
            DatePickerDialog date_picker = new DatePickerDialog( context, (view, year, month, dayOfMonth) -> {
                String monthStr = String.valueOf(month+1);
                if((month+1) < 10) monthStr = "0"+monthStr;
                String dayStr = String.valueOf(dayOfMonth);
                if(dayOfMonth < 10) dayStr = "0"+dayStr;
                dateBtn.setText(year+"-"+monthStr+"-"+dayStr);
                period_discounts.get(index-3)[type+3] = year+"-"+monthStr+"-"+dayStr;
                Log.d(TAG, "date change("+(index-3)+"/"+period_discounts.size()+"): "+ Arrays.toString(period_discounts.get(index-3)));
            },calendar.get(GregorianCalendar.YEAR),calendar.get(GregorianCalendar.MONTH),calendar.get(GregorianCalendar.DAY_OF_MONTH));
            date_picker.getDatePicker().setMinDate(new Date().getTime());
            date_picker.show();
        });

    }

    private void getData(){
        String function_name = "discount_data";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .build();

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
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.i(TAG,"responseData of getData: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);

                    JSONObject freeItems = responseArr.getJSONObject(0);
                    Log.i(TAG, "freeItems: "+freeItems);
                    boolean valuateBl = freeItems.getString("valuate").equals("1");
                    boolean depositBl = freeItems.getString("deposit").equals("1");
                    boolean cancelBl = freeItems.getString("cancel").equals("1");


                    runOnUiThread(() -> {
                        valuate.setChecked(valuateBl);
                        deposit.setChecked(depositBl);
                        cancel.setChecked(cancelBl);
                    });

                    int i;
                    for(i = 1; i < responseArr.length(); i++){
                        JSONObject discountItem = responseArr.getJSONObject(i);
                        Log.i(TAG, "discountItem: "+discountItem);
                        final String discountId = discountItem.getString("discount_id");
                        final String discountName = discountItem.getString("discount_name");
                        final int percent = discountItem.getInt("discount");
                        final String startTime = discountItem.getString("start_date");
                        final String endTime = discountItem.getString("end_date");
                        String disableTime = discountItem.getString("disable_time");
                        boolean enable = discountItem.getString("enable").equals("1");
//                        if(!disableTime.isEmpty()) enable = true;

                        String[] period_discount = {discountId, discountName, String.valueOf(percent), startTime, endTime, String.valueOf(enable)};

                        final boolean finalEnable = enable;
                        runOnUiThread(() -> {
                            int row_index = 0;
                            if (discountName.equals("短期優惠")) row_index = 3;
                            else if (discountName.equals("長期優惠")) row_index = 4;

                            if(row_index != 0){
                                TableRow discountItem1 = (TableRow) discountTable.getChildAt(row_index);
                                //0 : X
                                Switch enableSw = (Switch) discountItem1.getChildAt(SWITCH_INDEX);
                                EditText discountEdit = (EditText) discountItem1.getChildAt(DISCOUNT_INDEX);
                                //4 : %
                                TextView startView = (TextView) discountItem1.getChildAt(START_INDEX);
                                //6 : ─
                                TextView endView = (TextView) discountItem1.getChildAt(END_INDEX);
                                TextView discountIdText = (TextView) discountItem1.getChildAt(ID_INDEX);

                                enableSw.setChecked(finalEnable);
                                discountEdit.setText(String.valueOf(percent));
                                startView.setText(startTime);
                                endView.setText(endTime);
                                discountIdText.setText(discountId);

                                period_discounts.set(row_index-3, period_discount);
                            }
                            else {
                                period_discounts.add(period_discount);
                                Log.d(TAG, "add period_discount("+(period_discounts.size()-1)+"/"+period_discounts.size()+"): "
                                        +Arrays.toString(period_discounts.get(period_discounts.size()-1)));

                                discountTable.addView(addNewRow(discountId, discountName, percent, startTime, endTime, finalEnable, discountTable.getChildCount()));
                            }
                        });
                    }
                    if(i <= 1) Log.i(TAG, "no period discount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private TableRow addNewRow(final String discountId, final String discountName, int percent, String startTime, String endTime, final boolean enable, int index){
        String[] period_discount = {discountId, discountName, String.valueOf(percent), startTime, endTime, String.valueOf(enable)};
        final TableRow newDiscount = new TableRow(context);

        Button deleteBtn = new Button(context);
        TextView nameText = new TextView(context);
        Switch enableSw = new Switch(context);
        EditText discountEdit = new EditText(context);
        TextView percentIcon =  new TextView(context);
        TextView startView = addNewDate(startTime, START_TIME_VIEW, index);
        TextView toIcon =  new TextView(context);
        TextView endView = addNewDate(endTime, END_TIME_VIEW, index);
        TextView discountIdText = new TextView(context);

        int dp40 = dip2px(context, 40);
        int dp15 = dip2px(context, 15);
        int dp8 = dip2px(context, 8);
        int dp5 = dip2px(context, 5);

        newDiscount.setGravity(Gravity.CENTER_VERTICAL);
        deleteBtn.setBackgroundResource(R.drawable.ic_baseline_cancel_24);
        TableRow.LayoutParams p = new TableRow.LayoutParams(dp15, dp15);
        p.setMargins(0, 0, dp15, 0);
        deleteBtn.setLayoutParams(p);
        if(!deleteMode) deleteBtn.setVisibility(View.GONE);
        else deleteBtn.setVisibility(View.VISIBLE);
        deleteBtn.setOnClickListener(v -> {
            discountTable.removeView(newDiscount);
            delete_discounts.add(discountId);
            pdRemoveBy(discountName);
        });
        nameText.setText(discountName);
        nameText.setTextSize(20);
        nameText.setTextColor(Color.parseColor("#000000"));
        nameText.setPadding(0, 0, 0, dp5);
        enableSw.setPadding(dp15, 0, 0, 0);
        runOnUiThread(() -> enableSw.setChecked(enable));
        setSwitch(enableSw, startView, endView, index, period_discount);
        if(percent >= 0) discountEdit.setText(String.valueOf(percent));
        else discountEdit.setHint("1");
        discountEdit.setWidth(dp40);
        discountEdit.setGravity(Gravity.END);
        percentIcon.setText("%");
        percentIcon.setTextSize(18);
        percentIcon.setPadding(0, 0, 0 ,dp8);
        toIcon.setText("─");
        toIcon.setPadding(dp5, 0, dp5, 0);
        discountIdText.setText(discountId);
        discountIdText.setVisibility(View.GONE);

        newDiscount.addView(deleteBtn);
        newDiscount.addView(nameText);
        newDiscount.addView(enableSw);
        newDiscount.addView(discountEdit);
        newDiscount.addView(percentIcon);
        newDiscount.addView(startView);
        newDiscount.addView(toIcon);
        newDiscount.addView(endView);
        newDiscount.addView(discountIdText);

        return newDiscount;
    }

    private TextView addNewDate(String date, int type, int index){
        int dp60 = dip2px(context, 60);
        int dp3 = dip2px(context, 3);
        TextView dateView =  new TextView(context);
        dateView.setWidth(dp60);
        dateView.setText(date);
        dateView.setTextSize(18);
        dateView.setGravity(Gravity.CENTER);
        dateView.setBackgroundResource(R.drawable.edittext_rectanngle);
        dateView.setPadding(dp3, dp3, dp3, dp3); //要在backgoundResource後設置才有用
        setDateBtn(dateView, type, index);
        return dateView;
    }

    private void pdRemoveBy(String discountName){
        for(int i = 0; i < period_discounts.size(); i++){
            if(period_discounts.get(i)[0].equals(discountName)){
                period_discounts.remove(i);
                return;
            }
        }
    }

    private void updateDiscount(){
        String function_name = "modify_discount";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", getCompany_id(context))
                .add("valuate", String.valueOf(valuate.isChecked()))
                .add("deposit", String.valueOf(deposit.isChecked()))
                .add("cancel", String.valueOf(cancel.isChecked()))
                .add("periodItems", itemsToString(period_discounts))
                .add("deleteItems", String.valueOf(delete_discounts))
                .build();
        Log.i(TAG, "valuate: "+valuate.isChecked()+", deposit: "+deposit.isChecked()+", cancel: "+cancel.isChecked()
                +", periodItems: "+itemsToString(period_discounts)+", deleteItems: "+delete_discounts);

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
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "responseData of update_discount: " + responseData);
            }
        });
    }

    private String itemsToString(ArrayList<String[]> items){
        if(items.size() == 0) return "";

        String itemStr;
        itemStr = "[";
        for(int i = 0; i < items.size(); i++){
            if(i != 0) itemStr = itemStr + ", ";
            itemStr = itemStr
                    +"[\""+items.get(i)[0]
                    +"\", \""+items.get(i)[1]
                    +"\", \""+items.get(i)[2]
                    +"\", \""+items.get(i)[3]
                    +"\", \""+items.get(i)[4]
                    +"\", \""+items.get(i)[5]+"\"]";
        }
        itemStr = itemStr+"]";

        return itemStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        Log.d(TAG, "valuate is "+valuate.isChecked());
        Log.d(TAG, "deposit is "+deposit.isChecked());
        Log.d(TAG, "cancel is "+cancel.isChecked());

        getPeriodRow(false);

        Log.d(TAG, "size of period discount: "+period_discounts.size());
        Log.d(TAG, "period discount: "+itemsToString(period_discounts));

        Log.d(TAG, "size of delete discount: "+delete_discounts.size());
        Log.d(TAG, "delete discount: "+delete_discounts);

        updateDiscount();
        finish();
        super.onBackPressed();
    }

    private void globalNav(){
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Setting_Discount.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Setting_Discount.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Setting_Discount.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Setting_Discount.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Setting_Discount.this, Setting.class);
            startActivity(setting_intent);
        });
    }
}
