package com.christophelai.idp_colistraking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuAppointment extends Activity implements View.OnClickListener  {
    Button  btnAppointmentSMS;
    SharedPreferences prf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_appointment);
        prf = getSharedPreferences("user_details", MODE_PRIVATE);
        btnAppointmentSMS = findViewById(R.id.btnRdvSms);
        btnAppointmentSMS.setOnClickListener(this);
        btnAppointmentSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Tag On Click", "btnRdv");
                startActivity(new Intent(MenuAppointment.this, AppointmentBySMS.class));
                Constant.SaveLog("{\n" +
                        "    \"btn\":\"btnRdv\",\n" +
                        "    \"msg\":\"Open Menu RDV\"\n" +
                        "}", prf.getString("idCarrier", null), MenuAppointment.this);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}