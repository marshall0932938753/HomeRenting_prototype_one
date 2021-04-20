package com.example.homerenting_prototype_one;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText act_edit = findViewById(R.id.act_edit);
        final EditText pwd_edit = findViewById(R.id.pwd_edit);
        final EditText pwd_check_edit = findViewById(R.id.checkPwd_edit);
        final EditText name_edit = findViewById(R.id.memberName_edit);
        final EditText birth_day_edit = findViewById(R.id.birth_day_edit);
        final EditText birth_month_edit = findViewById(R.id.birth_month_edit);
        final EditText birth_year_edit = findViewById(R.id.birth_year_edit);
        RadioButton sir_btn = findViewById( R.id.sir_radioBtn );
        RadioButton lady_btn = findViewById( R.id.lady_radioBtn );
        RadioButton other_btn = findViewById( R.id.other_radioBtn );
        final EditText id_edit = findViewById(R.id.id_edit);
        final EditText phone_edit = findViewById(R.id.memberPhone_edit);
        Button next_btn = findViewById(R.id.next_btn);

        if (TextUtils.isEmpty( act_edit.getText().toString() )){
            act_edit.setError("請輸入帳號"  );
        }
        if (TextUtils.isEmpty( pwd_edit.getText().toString() )){
            pwd_edit.setError( "請輸入密碼" );
        }
        if (TextUtils.isEmpty( pwd_check_edit.getText().toString() )){
            pwd_check_edit.setError( "請再次輸入密碼" );
        }
        if (TextUtils.isEmpty( name_edit.getText().toString() )){
            name_edit.setError( "請輸入姓名" );
        }
        if (TextUtils.isEmpty( birth_day_edit.getText().toString() )){
            birth_day_edit.setError( "請輸入生日日期" );
        }
        if (TextUtils.isEmpty( birth_month_edit.getText().toString() )){
            birth_month_edit.setError( "請輸入生日月份" );
        }
        if (TextUtils.isEmpty( birth_year_edit.getText().toString() )){
            birth_year_edit.setError( "請輸入生日年份" );
        }
        if (TextUtils.isEmpty( id_edit.getText().toString() )){
            id_edit.setError("請輸入身分證字號");
        }
        if (TextUtils.isEmpty( phone_edit.getText().toString() )){
            phone_edit.setError( "請輸入手機號碼" );
        }
        next_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next_intent = new Intent();
                next_intent.setClass( Register.this,Register_Company.class );
                startActivity( next_intent );
            }
        } );
    }
}
