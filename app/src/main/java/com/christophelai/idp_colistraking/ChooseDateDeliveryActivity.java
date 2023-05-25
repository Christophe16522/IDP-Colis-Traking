package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import androidx.annotation.NonNull;

import java.util.Date;

public class ChooseDateDeliveryActivity extends Activity {
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_date_delivery);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setMinDate(new Date().getTime());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //le mois doit etres +1 car janv = 0 et dec = 11
                int mois = month + 1;
                String date = year + "-" + mois + "-" + dayOfMonth;
                Log.e("date selected", date);
                Intent i = new Intent(ChooseDateDeliveryActivity.this, ListDelivery.class);
                i.putExtra("dateChoosed", date);
                startActivity(i);
                finish();
            }
        });
    }
}