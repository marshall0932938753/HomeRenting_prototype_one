package com.example.homerenting_prototype_one;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homerenting_prototype_one.calendar.Calendar;

public class Register_Company extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register__company );

        EditText company_name_edit = findViewById(R.id.company_name_edit);
        EditText district_edit = findViewById(R.id.district_edit);
        EditText city_edit = findViewById(R.id.city_edit);
        EditText address_edit = findViewById(R.id.address_edit);
        EditText number_edit = findViewById(R.id.number_edit);
        EditText money_edit = findViewById(R.id.money_edit);
        EditText establish_day_edit = findViewById(R.id.establish_day_edit);
        EditText establish_month_edit = findViewById(R.id.establish_month_edit);
        EditText establish_year_edit = findViewById(R.id.establish_year_edit);
        EditText link_edit = findViewById(R.id.link_edit);
        EditText line_edit = findViewById(R.id.line_id_edit);
        Button photo_btn = findViewById(R.id.choose_photo_btn);
        Button finish_btn = findViewById(R.id.finish_btn);

        if (TextUtils.isEmpty( company_name_edit.getText().toString() )){
            company_name_edit.setError( "請輸入公司名稱" );
        }
        if (TextUtils.isEmpty( district_edit.getText().toString() )){
            district_edit.setError( "請輸入公司縣市名稱" );
        }
        if (TextUtils.isEmpty( city_edit.getText().toString() )){
            city_edit.setError( "請輸入公司鄉鎮市名稱" );
        }
        if (TextUtils.isEmpty( address_edit.getText().toString() )){
            address_edit.setError( "請輸入公司地址" );
        }
        if (TextUtils.isEmpty( number_edit.getText().toString() )){
            number_edit.setError( "請輸入員工人數" );
        }
        if (TextUtils.isEmpty( money_edit.getText().toString() )){
            money_edit.setError( "請輸入資本額" );
        }
        if (TextUtils.isEmpty( establish_day_edit.getText().toString() )){
            establish_day_edit.setError( "請輸入成立日期" );
        }
        if (TextUtils.isEmpty( establish_month_edit.getText().toString() )){
            establish_month_edit.setError( "請輸入成立月份" );
        }
        if (TextUtils.isEmpty( establish_year_edit.getText().toString() )){
            establish_year_edit.setError( "請輸入成立年份" );
        }
        if (TextUtils.isEmpty( link_edit.getText().toString() )){
            link_edit.setError( "請輸入公司網址" );
        }
        if (TextUtils.isEmpty( line_edit.getText().toString() )){
            line_edit.setError( "請輸入LINE的ID" );
        }
        photo_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );
        finish_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent finish_intent = new Intent();
                finish_intent.setClass( Register_Company.this,
                        Calendar.class);
                startActivity( finish_intent );
            }
        } );

    }
}
