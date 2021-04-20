package com.example.homerenting_prototype_one;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarView extends LinearLayout {
    LinearLayout header_layout;
    ImageButton left_btn, right_btn,
                valuation_btn, order_btn, calendar_btn, system_btn,setting_btn;
    TextView date_text;
    GridView calendar_grid;

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }
    private void assignUiElements(){
        header_layout = findViewById( R.id.header_layout );
        left_btn = findViewById( R.id.previous_btn );
        right_btn = findViewById( R.id.next_btn );
        valuation_btn = findViewById( R.id.valuationBlue_Btn);
        order_btn = findViewById(R.id.order_imgBtn);
        calendar_btn = findViewById(R.id.calendar_imgBtn);
        system_btn = findViewById(R.id.system_imgBtn);
        setting_btn = findViewById(R.id.setting_imgBtn);
        date_text = findViewById( R.id.year_month_text );
        calendar_grid = findViewById( R.id.calendar_grid );
    }
    private void initControl(Context context, AttributeSet attrs){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate( R.layout.calendar_layout,this );
        assignUiElements();
    }
    public void updateCalendar(HashSet<Date>events){
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = null;
//        Calendar calendar = (Calendar)currentDate.clone();


    }
}
