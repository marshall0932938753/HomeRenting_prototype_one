package com.example.homerenting_prototype_one.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.homerenting_prototype_one.R;

public class MainActivity extends AppCompatActivity {
    public ImageView btn;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout hone_page = findViewById(R.id.homePage_Linear_M);
        context = MainActivity.this;

        hone_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(context, New_Login.class);
                startActivity(login_intent);
            }
        });
//        btn = findViewById(R.id.logo_btn);
//        //btn.setImageResource(R.drawable.master_logo);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }
//    public void enter(View view){
////        Intent intent = new Intent(this,Activity_login.class);
////        startActivity(intent);
//    }
}
