package com.example.homerenting_prototype_one.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.order.Order_Detail;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.valuation.Valuation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;

public class Evaluation_Detail extends AppCompatActivity {
    TextView nameText, nameTitleText, phoneText, fromAdressText, toAddressText, commentText, replyText, reply_text;
    LinearLayout serviceStars, workStars, priceStars;
    EditText reply_edit;
    Button replyBtn, finishBtn;
    ImageButton back_btn;

    Bundle bundle;
    String comment_id, replyStr;

    Context context = this;
    String TAG = "Evaluation_Detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation__detail);
        back_btn = findViewById(R.id.back_imgBtn);

        bundle = getIntent().getExtras();
        comment_id = bundle.getString("comment_id");

        linking();

        getData();

        reply_edit.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()!=0){
                    reply_text.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        replyBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyText.setGravity(Gravity.CENTER_VERTICAL);
                replyText.setVisibility(View.VISIBLE);
                reply_edit.setVisibility(View.VISIBLE);
                reply_text.setVisibility(View.GONE);
                replyBtn.setVisibility(View.GONE);
                finishBtn.setVisibility(View.VISIBLE);
            }
        } );
        finishBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyText.setGravity(Gravity.TOP);
                if(reply_edit.getText().toString().isEmpty()) replyText.setVisibility(View.GONE);
                reply_edit.setVisibility(View.GONE);
                reply_text.setText(reply_edit.getText().toString());
                reply_text.setVisibility(View.VISIBLE);
                finishBtn.setVisibility(View.GONE);
                replyBtn.setVisibility(View.VISIBLE);
                replyBtn.setText("修改");
            }
        } );

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        globalNav();
    }

    private void linking(){
        nameText = findViewById(R.id.name_ED);
        nameTitleText = findViewById(R.id.nameTitle_ED);
        phoneText = findViewById(R.id.phone_ED);
        fromAdressText = findViewById(R.id.fromAddress_ED);
        toAddressText = findViewById(R.id.toAddress_ED);
        commentText = findViewById(R.id.comment_ED);
        serviceStars = findViewById(R.id.serviceStar_LL_ED);
        workStars = findViewById(R.id.workStar_LL_ED);
        priceStars = findViewById(R.id.priceStar_LL_ED);
        replyText = findViewById(R.id.replyText_ED);
        reply_edit = findViewById(R.id.reply_edit_ED);
        reply_text = findViewById( R.id.reply_text_ED);
        replyBtn = findViewById(R.id.reply_btn_ED);
        finishBtn = findViewById(R.id.finish_btn_ED);
    }

    private void getData(){
        String function_name = "comment_detail";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("comment_id", comment_id)
                .build();

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //在app畫面上呈現錯誤訊息
                        Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.i(TAG,"responseData: "+responseData); //顯示資料

                try {
                    JSONArray responseArr = new JSONArray(responseData);
                    JSONObject comment = responseArr.getJSONObject(0);

                    final String name = comment.getString("member_name");
                    final String nameTitle;
                    if(comment.getString("gender").equals("女")) nameTitle = "小姐";
                    else nameTitle = "先生";
                    final String fromAddress = comment.getString("from_address");
                    final String toAddress = comment.getString("to_address");
                    final String commentStr = comment.getString("comment");
                    replyStr = comment.getString("reply");
                    final int service_star = comment.getInt("service_quality");
                    final int work_star = comment.getInt("work_attitude");
                    final int price_star = comment.getInt("price_grade");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameText.setText(name);
                            nameTitleText.setText(nameTitle);
                            fromAdressText.setText(fromAddress);
                            toAddressText.setText(toAddress);
                            commentText.setText(commentStr);
                            setStars(serviceStars, service_star);
                            setStars(workStars, work_star);
                            setStars(priceStars, price_star);

                            if(!replyStr.equals("null")){
                                replyText.setVisibility(View.VISIBLE);
                                reply_text.setText(replyStr);
                                reply_text.setVisibility(View.VISIBLE);
                                reply_edit.setText(replyStr);
                                replyBtn.setText("修改");
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setStars(LinearLayout ll, int stars){
        for(int i = 0; i < 5; i++){
            ImageView star = (ImageView) ll.getChildAt(i);
            if(i < stars) star.setVisibility(View.VISIBLE);
            else star.setVisibility(View.GONE);
        }
    }

    private void updateReply(){
        String function_name = "update_reply";
        String reply = reply_text.getText().toString();
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("comment_id", comment_id)
                .add("reply", reply)
                .build();
        Log.d(TAG, "comment_id: "+comment_id+", reply: "+reply);

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //在app畫面上呈現錯誤訊息
                        Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG,"responseData of update_reply: "+responseData); //顯示資料
            }
        });
    }

    private void globalNav(){
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        valuation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent valuation_intent = new Intent(Evaluation_Detail.this, Valuation.class);
                startActivity(valuation_intent);
            }
        });
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent order_intent = new Intent(Evaluation_Detail.this, Order.class);
                startActivity(order_intent);
            }
        });
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calender_intent = new Intent(Evaluation_Detail.this, Calendar.class);
                startActivity(calender_intent);
            }
        });
        system_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent system_intent = new Intent(Evaluation_Detail.this, System.class);
                startActivity(system_intent);
            }
        });
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_intent = new Intent(Evaluation_Detail.this, Setting.class);
                startActivity(setting_intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        String reply = reply_text.getText().toString();
        if(reply.isEmpty() || reply.equals(replyStr)) Log.d(TAG, "no edit");
        else updateReply();
        super.onBackPressed();
    }
}
