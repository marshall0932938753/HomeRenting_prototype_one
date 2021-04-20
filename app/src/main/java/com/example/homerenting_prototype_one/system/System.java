package com.example.homerenting_prototype_one.system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.bouns.System_Bonus;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.valuation.Valuation;

public class System extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        ImageButton data_btn = findViewById(R.id.data_imgBtn);
        ImageButton schedule_btn = findViewById(R.id.schedule_imgBtn);
        ImageButton vacation_btn = findViewById(R.id.vacation_imgBtn);
        ImageButton bonus_btn = findViewById(R.id.bonus_imgBtn);
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);

        data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data_intent = new Intent(System.this, System_Data.class);
                startActivity(data_intent);
            }
        });
        schedule_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent schedule_intent = new Intent(System.this, System_Schedule.class);
                startActivity(schedule_intent);
            }
        });
        vacation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vacation_intent = new Intent(System.this, System_Vacation.class);
                startActivity(vacation_intent);
            }
        });
        bonus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bonus_intent = new Intent(System.this, System_Bonus.class);
                startActivity(bonus_intent);
            }
        });

        //底下nav
        valuation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent valuation_intent = new Intent(System.this, Valuation.class);
                startActivity(valuation_intent);
            }
        });
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent order_intent = new Intent(System.this, Order.class);
                startActivity(order_intent);
            }
        });
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calender_intent = new Intent(System.this, Calendar.class);
                startActivity(calender_intent);
            }
        });
//        system_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent system_intent = new Intent(System.this, System.class);
//                startActivity(system_intent);
//            }
//        });
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_intent = new Intent(System.this, Setting.class);
                startActivity(setting_intent);
            }
        });
    }
}
