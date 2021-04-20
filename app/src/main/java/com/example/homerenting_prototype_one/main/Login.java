package com.example.homerenting_prototype_one.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.order.Order;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    String account;
    String passowrd;

    OkHttpClient okHttpClient = new OkHttpClient();
    String TAG = "Valuation_Booking";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText account_edit = findViewById(R.id.actEdit_L);
        final EditText password_edit = findViewById(R.id.pwdEdit_L);
//        final EditText check_Edit = findViewById(R.id.checkEdit);
        //TextView forget_password = findViewById(R.id.forgetText);
        Button login_btn = findViewById(R.id.login_btn_L);
        Button edit_btn = findViewById(R.id.edit_pwd_btn);

        context = Login.this;

        login_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = account_edit.getText().toString();
                passowrd = password_edit.getText().toString();
                boolean check = false;
                if (TextUtils.isEmpty( account_edit.getText().toString() )){
                    account_edit.setError( "請輸入帳號" );
                    check = true;
                }
                if (TextUtils.isEmpty( password_edit.getText().toString() )){
                    password_edit.setError( "請輸入密碼" );
                    check = true;
                }
//                if (TextUtils.isEmpty( check_Edit.getText().toString() )){
//                    check_Edit.setError( "請確認密碼" );
//                    check = true;
//                }
                if(check) return;
                else logining();
//                final String check_text = check_Edit.getText().toString().trim();
//                if (check_text.equals( passowrd_text )&&!(TextUtils.isEmpty( passowrd_text ))&&!(TextUtils.isEmpty( check_text ))){
//                    startActivity(new Intent(context, Calendar.class));
//                }
//                else if (!(check_text.equals( passowrd_text ))&&!(TextUtils.isEmpty( passowrd_text ))&&TextUtils.isEmpty( check_text )){
//                    startActivity(new Intent(context, Calendar.class));
//                }
//                else {
//                    new AlertDialog.Builder( Login.this )
//                            .setTitle( "密碼不一致" )
//                            .setMessage( "請輸入一致的密碼" )
//                            .setPositiveButton( "確認", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            } )
//                            .setNegativeButton( "取消",null ).create()
//                            .show();
//                }
            }
        } );
//        edit_btn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                check_Edit.setVisibility( View.VISIBLE );
//            }
//        } );
    }

    public void login(View view) {
        return;
    }

    public void logining(){
        RequestBody body = new FormBody.Builder()
                .add("function_name", "check_companyID")
                .add("company_id", account)
                .build();
        Log.d(TAG, "login: company_id: "+account);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"functional.php")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseData.equals("success"))
                            Toast.makeText(context, "登入成功", Toast.LENGTH_LONG).show();
                        else Toast.makeText(context, "登入失敗", Toast.LENGTH_LONG).show();
                    }
                });
                Log.d(TAG, "submit responseData: " + responseData);
            }
        });

        //startActivity(new Intent(Login.this, Calendar.class));
        startActivity(new Intent(context, Order.class));
    }
//    public void register(View view){
//        startActivity(new Intent(Login.this, Register.class));
//    }
}
