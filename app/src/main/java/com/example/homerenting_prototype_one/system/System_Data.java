package com.example.homerenting_prototype_one.system;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.helper.RecyclerViewAction;
import com.example.homerenting_prototype_one.adapter.re_adpater.TextAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.valuation.Valuation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;

public class System_Data extends AppCompatActivity {

//    public String[] employees = {"王小明","陳聰明","劉光明","吳輝明","邱朝明","葉大明","劉案全",
//            "王守興","彭玉唱","徐將義","劉曹可","秦因文","方子優","古蘭花","朱柯基"};
//    public String[] cars = {"3.5噸平斗車NRT-134","3.5噸平斗車HWE-353","3.5噸平斗車ITE-774","3.5噸平斗車BTU-255","3.5噸箱型車YEU-712",
//            "3.5噸箱型車NKC-456","3.5噸箱型車ZXE-654","7.7噸箱型車RSF-673","7.7噸箱型車ESF-553","7.7噸箱型車KMS-352"};

    ListView employee_list, car_list;
    RecyclerView employeeRList, carRList;
    TextView employee_text, car_text;
    Button addEmployee_btn, addCar_btn;
    ImageButton back_btn, valuation_btn, order_btn, calendar_btn, system_btn, setting_btn;

    private List<String> employee_aList, car_aList;
    private ArrayList<String[]> employees, vehicles;
    private ArrayList<String> new_employee;

    TextAdapter e_adapter, c_adapter;

    int currentList;
    String new_carType;

    Context context = this;
    String TAG = "System_Data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system__data);

        linking();
        init();

        getData();
//        setList();

        back_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(System_Data.this, System.class);
            startActivity(system_intent);
        });

        employee_text.setOnClickListener(v -> showEmployeeList());

        car_text.setOnClickListener(v -> showCarList());

        setAddEmployeeButton();
        setAddCarButton();

        globalNav();
    }


    private void linking(){
        employeeRList = findViewById(R.id.staff_vehicle_rv_STD);
        carRList = findViewById(R.id.vh_rv_STD);

        back_btn = findViewById(R.id.back_imgBtn);

        employee_text = findViewById(R.id.employee_bar);
        car_text = findViewById(R.id.car_bar);

        addEmployee_btn = findViewById(R.id.add_employee_btn);
        addCar_btn = findViewById(R.id.add_car_btn);

        valuation_btn = findViewById(R.id.valuationBlue_Btn);
        order_btn = findViewById(R.id.order_imgBtn);
        calendar_btn = findViewById(R.id.calendar_imgBtn);
        system_btn = findViewById(R.id.system_imgBtn);
        setting_btn = findViewById(R.id.setting_imgBtn);
    }

    private void init(){
        employees = new ArrayList<>();
        vehicles = new ArrayList<>();
        new_employee = new ArrayList<>();

        currentList = 0;

        employeeRList.setLayoutManager(new LinearLayoutManager(context));
        employeeRList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        carRList.setLayoutManager(new LinearLayoutManager(context));
        carRList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    private void showEmployeeList(){
        employeeRList.setVisibility(View.VISIBLE);
        carRList.setVisibility(View.GONE);

        if(currentList == 1){
            Ani a = new Ani(0);
            a.setDuration(250);
            employee_text.startAnimation(a);
            addEmployee_btn.setVisibility(View.VISIBLE);
            addCar_btn.setVisibility(View.GONE);
            currentList = 0;
        }
    }

    private void showCarList(){
        employeeRList.setVisibility(View.GONE);
        carRList.setVisibility(View.VISIBLE);
        if(currentList == 0){
            Ani a = new Ani(1);
            a.setDuration(250);
            car_text.startAnimation(a);
            addEmployee_btn.setVisibility(View.GONE);
            addCar_btn.setVisibility(View.VISIBLE);
            currentList = 1;
        }
    }

    private void getData(){
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
                String responseData = response.body().string();
//                Log.d(TAG,"responseData: "+responseData); //顯示資料

                try {
                    //轉換成json格式，array或object
                    final JSONArray responseArr = new JSONArray(responseData);
                    //Log.i(TAG,"responseObj: "+ responseArr);

                    //一筆一筆的取JSONArray中的json資料
                    int i;
                    for (i = 0; i < responseArr.length(); i++) {
                        JSONObject staff = responseArr.getJSONObject(i);
                        if (!staff.has("staff_id")) break;
                        Log.i(TAG, "staff: " + staff);

                        //取欄位資料
                        String staff_id = staff.getString("staff_id");
                        String staff_name = staff.getString("staff_name");
                        String[] row_data = {staff_id, staff_name};
                        employees.add(row_data);
                    }

                    for (; i < responseArr.length(); i++) {
                        JSONObject vehicle = responseArr.getJSONObject(i);
                        if (!vehicle.has("vehicle_id")) break;
                        Log.i(TAG, "vehicle: " + vehicle);

                        //取欄位資料
                        String vehicle_id = vehicle.getString("vehicle_id");
                        String weight = vehicle.getString("vehicle_weight");
                        String type = vehicle.getString("vehicle_type");
                        String plate_num = vehicle.getString("plate_num");
                        String verified = vehicle.getString("verified");
                        String[] row_data = {vehicle_id, weight, type, plate_num, verified};
                        vehicles.add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(context, "Toast onResponse failed because JSON", Toast.LENGTH_LONG).show());
                }

                //顯示資訊
                if(!responseData.equals("null")){
                    for(int i=0; i < employees.size(); i++)
                        Log.i(TAG, "employees: "+ Arrays.toString(employees.get(i)));
                }
                runOnUiThread(() -> {
                    setCarRList();
                    setEmployeeRList();
                });
            }
        });
    }

    private void setEmployeeRList(){ //顯示員工List
        employeeRList.setAdapter(null);
        e_adapter = new TextAdapter(employees);
        employeeRList.setAdapter(e_adapter);

        //側滑刪除
        ItemTouchHelper ehelper = new ItemTouchHelper(new RecyclerViewAction(context, e_adapter));
        ehelper.attachToRecyclerView(employeeRList);
    }

    private void setCarRList(){ //顯示車輛List
        employeeRList.setAdapter(null);
        c_adapter = new TextAdapter(vehicles);
        carRList.setAdapter(c_adapter);

        //側滑刪除
        ItemTouchHelper chelper = new ItemTouchHelper(new RecyclerViewAction(context, c_adapter));
        chelper.attachToRecyclerView(carRList);
    }

    private void setAddEmployeeButton(){
        addEmployee_btn.setOnClickListener(v -> {
            //輸入欄
            final EditText employee_edit = new EditText(context);
            //設定margin
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 80;
            params.rightMargin = params.leftMargin;
            employee_edit.setLayoutParams(params);
            //要放到FrameLayout裡，margin才有用
            FrameLayout container = new FrameLayout(context);
            container.addView(employee_edit);

            new AlertDialog.Builder(context)
                    .setTitle( "新增員工" )
                    .setView(container)
                    .setPositiveButton( "確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String new_employee_name = employee_edit.getText().toString();

                            //確認員工名字有無重複(目前不允許同名同姓)
                            if(!isEmployeeExist(new_employee_name)){
                                String[] row_data = {"-1", new_employee_name};
                                employees.add(row_data);
                            }
                            //新增後的ArrayList
                            for(int i=0; i < employees.size(); i++)
                                Log.i(TAG, "employees: "+ Arrays.toString(employees.get(i)));

                            //呈現在list上
                            e_adapter.notifyDataSetChanged();
                            showEmployeeList();

                            //寫入資料庫
                            if(!TextUtils.isEmpty(employee_edit.getText().toString())){
                                add_staff(new_employee_name);
                            }
                        }
                    } )
                    .setNegativeButton( "取消",null )
                    .create()
                    .show();
        });
    }

    private void setAddCarButton(){
        addCar_btn.setOnClickListener(v -> {
            final AlertDialog.Builder car_dialog = new AlertDialog.Builder(context);
            car_dialog.setTitle("新增車輛");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.add_car_dialog, null);
            car_dialog.setView(view);

            //輸入欄
            final EditText weight_edit = view.findViewById(R.id.weight_edit_ACD);
            final EditText type_edit = view.findViewById(R.id.type_edit_ACD);
            final EditText plateNum_edit = view.findViewById(R.id.plateNum_edit_ACD);

            Spinner type_sp = view.findViewById(R.id.type_sp_ACD);
            final String[] types = {"箱型車", "平斗車"}; //其他
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, types);
            type_sp.setAdapter(typeAdapter);
            type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(context, "type("+position+"): "+types[position], Toast.LENGTH_SHORT).show();
                    if(position == 2) { //2:其他
                        type_edit.setVisibility(View.VISIBLE);
                        new_carType = null;
                    }
                    else {
                        type_edit.setVisibility(View.GONE);
                        new_carType = types[position];
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            car_dialog.setPositiveButton("確認", (dialog, which) -> {
                //輸入的內容
                String new_weight = weight_edit.getText().toString();
                String new_plateNum = plateNum_edit.getText().toString();

                if(new_carType == null) new_carType = type_edit.getText().toString();
                else type_edit.setText(new_carType);

                //空白防呆+防止重複的車牌
                if(!isEmtpy(weight_edit, type_edit, plateNum_edit) && !isCarExist(new_plateNum)){
                    String[] row_data = {"-1", new_weight, new_carType, new_plateNum, "0"};
                    vehicles.add(row_data);
                }

                //呈現在list上
                c_adapter.notifyDataSetChanged();
                showCarList();

                if(isEmtpy(weight_edit, type_edit, plateNum_edit)){
                    Toast.makeText(context, "新增車輛失敗，有空白欄位", Toast.LENGTH_SHORT).show(); //有空白欄位，顯示錯誤訊息
                }
                else{
                    add_car(new_weight, new_carType, new_plateNum); //寫入資料庫
                    Log.i(TAG, new_weight+"噸"+new_carType+" "+new_plateNum);
                }
            });

            car_dialog.setNegativeButton("取消", null);

            car_dialog.create().show();
        });
    }

    private boolean isEmployeeExist(String name){
        for(int i = 0; i < employees.size(); i++){
            if(employees.get(i)[1].equals(name)) return true;
        }
        return false;
    }

    private boolean isCarExist(String plateNum){
        for(int i = 0; i < vehicles.size(); i++)
            if(vehicles.get(i)[3].equals(plateNum)) return true;
        return false;
    }

    private boolean isEmtpy(EditText weight, EditText type, EditText plateNum){
        boolean check = false;
        if(TextUtils.isEmpty(weight.getText().toString())) check = true;
        if(TextUtils.isEmpty(type.getText().toString())) check = true;
        if(TextUtils.isEmpty(plateNum.getText().toString())) check = true;
        return check;
    }

    private void add_staff(String staff_name){
        String function_name = "add_staff";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("staff_name", staff_name)
                .add("company_id", company_id)
                .build();
        Log.i(TAG, "staff_name: "+staff_name);

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
                Log.d(TAG,"responseData of add_staff: "+responseData); //顯示資料
            }
        });
    }

    private void add_car(String weight, String type, String plate_num){
        String function_name = "add_vehicle";
        String company_id = getCompany_id(this);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("vehicle_weight", weight)
                .add("vehicle_type", type)
                .add("plate_num", plate_num)
                .add("company_id", company_id)
                .build();
        Log.i(TAG, "vehicle_weight: "+weight+", vehicle_type"+type+", plate_num"+plate_num);

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
                Log.d(TAG,"responseData of add_vehicle: "+responseData); //顯示資料
            }
        });
    }

    private void globalNav(){
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(System_Data.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(System_Data.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(System_Data.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(System_Data.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(System_Data.this, Setting.class);
            startActivity(setting_intent);
        });
    }

    class Ani extends Animation {
        int shift = 0;
        public Ani(int shift){
            this.shift = shift;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 3-(2*interpolatedTime));
            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1+(2*interpolatedTime));
            if(shift == 0){
                employee_text.setLayoutParams(param1);
                car_text.setLayoutParams(param2);
            }
            else{
                employee_text.setLayoutParams(param2);
                car_text.setLayoutParams(param1);
            }
        }
    }
}
