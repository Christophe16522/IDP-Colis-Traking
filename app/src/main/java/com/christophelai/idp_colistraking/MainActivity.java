package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    Button btnScanBarecode, btnLogout, btnSaisiData, btnListLivraison;
    SharedPreferences prf;
    TextView welcomeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prf = getSharedPreferences("user_details", MODE_PRIVATE);
        welcomeTxt = (TextView) findViewById(R.id.txtBienvenu);
        welcomeTxt.setText("Bienvenu , " + prf.getString("fullname", null));
        initViews();
        Constant.SaveLog("{\n" +
                "    \"msg\":\"Open Main Activity\",\n" +
                "}", prf.getString("idCarrier", null), MainActivity.this);
    }

    private void initViews() {
        btnScanBarecode = findViewById(R.id.btnOpenScan);
        btnLogout = findViewById(R.id.btnLogout);
        btnSaisiData = findViewById(R.id.btnOpenSaisi);
        btnListLivraison = findViewById(R.id.btn_list_livraison);
        btnScanBarecode.setOnClickListener(this);
        btnSaisiData.setOnClickListener(this);
        btnListLivraison.setOnClickListener(this);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Voulez-vous vraiment vous déconnecter ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Constant.SaveLog("{\n" +
                                        "    \"btn\":\"yes - Log out\",\n" +
                                        "    \"msg\":\"Log Out\"\n" +
                                        "}", prf.getString("idCarrier", null), MainActivity.this);
                                SharedPreferences.Editor editor = prf.edit();
                                editor.clear();
                                editor.commit();
                                startActivity(new Intent(MainActivity.this, Login.class));
                            }
                        });

                builder1.setNegativeButton(
                        "non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        btnSaisiData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Tag On Click", "saisi des data");
                startActivity(new Intent(MainActivity.this, SaisieData.class));
            }
        });
        btnListLivraison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Tag On Click", "saisi des data");
                startActivity(new Intent(MainActivity.this, ListDelivery.class));
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOpenScan:
                Log.w("Tag On Click", "in the btn scan code bare");
                startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));
                Constant.SaveLog("{\n" +
                        "    \"btn\":\"btnOpenScan\",\n" +
                        "    \"msg\":\"Open ScannedBarcodeActivity\"\n" +
                        "}", prf.getString("idCarrier", null), MainActivity.this);
        }
    }


}