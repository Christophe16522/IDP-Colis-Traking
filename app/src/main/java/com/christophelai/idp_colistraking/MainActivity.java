package com.christophelai.idp_colistraking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements View.OnClickListener {

    Button  btnScanBarecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
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
                startActivity(new Intent(MainActivity.this,ScannedBarcodeActivity.class));
        }
    }
}