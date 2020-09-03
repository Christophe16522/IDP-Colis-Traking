package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    Button btnScanBarecode;
    SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prf = getSharedPreferences("user_details", MODE_PRIVATE);
        initViews();
        Constant.SaveLog("{\n" +
                "    \"msg\":\"Open Main Activity\",\n" +
                "}", prf.getString("idCarrier", null), MainActivity.this);
    }

    private void initViews() {
        btnScanBarecode = findViewById(R.id.btnOpenScan);
        btnScanBarecode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOpenScan:
                Log.d("Tag On Click", "in the btn scan code bare");
                startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));
                Constant.SaveLog("{\n" +
                        "    \"btn\":\"btnOpenScan\",\n" +
                        "    \"msg\":\"Open ScannedBarcodeActivity\"\n" +
                        "}", prf.getString("idCarrier", null), MainActivity.this);
        }
    }
}