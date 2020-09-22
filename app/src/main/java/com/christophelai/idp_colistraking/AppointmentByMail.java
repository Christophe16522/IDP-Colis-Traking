package com.christophelai.idp_colistraking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class AppointmentByMail extends Activity {
    Button sendBtn;
    EditText txtphoneNo;
    EditText txtMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_by_mail);

        sendBtn = (Button) findViewById(R.id.btnSendSMS);
        txtphoneNo = (EditText) findViewById(R.id.telephonNo);
        txtMessage = (EditText) findViewById(R.id.txtMsg);

    }
}