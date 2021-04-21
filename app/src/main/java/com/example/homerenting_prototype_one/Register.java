package com.example.homerenting_prototype_one;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

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
            if(!pwd_check_edit.getText().toString().equals(pwd_edit.getText().toString())){
                pwd_check_edit.setError("確認密碼與密碼不符");
            }
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
            if(!checkID(id_edit.getText().toString())){
                id_edit.setError("身份證字號有誤");
            }
        }
        if (TextUtils.isEmpty( phone_edit.getText().toString() )){
            phone_edit.setError( "請輸入手機號碼" );
            if(!checkPhone(phone_edit.getText().toString())){
                phone_edit.setError("手機號碼格式有誤");
            }
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
    private boolean checkID(String id){
        if(!id.matches("[a-zA-Z][1-2][0-9]{8}")){
            return false;
        }

        String newID = id.toUpperCase();
        int[] headNum = new int[]{
                1, 10, 19, 28, 37,
                46, 55, 64, 39, 73,
                82, 2, 11, 20, 48,
                29, 38, 47, 56, 65,
                74, 83, 21, 3, 12, 30
        };
        char[] headCharUpper = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z'
        };
        int index = Arrays.binarySearch(headCharUpper, newID.charAt(0));
        int base = 8;
        int total = 0;
        for (int i = 1; i < 10; i++) {
            int tmp = Integer.parseInt(Character.toString(newID.charAt(i))) * base;
            total += tmp;
            base--;
        }

        total += headNum[index];
        int remain = total % 10;
        int checkNum = (10 - remain) % 10;
        if (Integer.parseInt(Character.toString(newID.charAt(9))) != checkNum) {
            return false;
        }
        return true;
    }
    private boolean checkPhone(String phone){
        if(!phone.matches("[0-9]{10}")){
            return false;
        }
        else return true;
    }
}
