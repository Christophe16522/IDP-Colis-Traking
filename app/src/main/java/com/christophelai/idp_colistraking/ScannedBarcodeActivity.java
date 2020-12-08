package com.christophelai.idp_colistraking;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ScannedBarcodeActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String baseUrl = Constant.SERVER + "/";
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    Button btnAction, btnScan, btnSaveID;
    String baseUrlTrackId = "";
    String statusById = "";
    String intentData = "";
    ArrayList<String> spinnerStatusList = new ArrayList<String>();
    JSONArray status;
    String newStatusId = "0";
    ArrayAdapter<String> adapter;
    int spinnerPosition;
    Spinner spin;
    String newId;
    Boolean camIsActive = true;
    String[] stockArr;
    ArrayList<String> trackingStatus = new ArrayList<>();
    String urlListTrackingStatus = baseUrl + "api-delivery/getstatus";
    SharedPreferences prf;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        initViews();
        spin = findViewById(R.id.spinner1);
        spin.setVisibility(View.GONE);
        btnAction.setVisibility(View.GONE);
        txtBarcodeValue.setVisibility(View.GONE);
        btnSaveID.setClickable(false);
        loadSpinnerData(urlListTrackingStatus);
       /* adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerStatusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);*/

        spin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        String item = arg0.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(arg0.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        getidStatus(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);
        btnScan = findViewById(R.id.btnScan);
        btnSaveID = findViewById(R.id.btnSaveID);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (camIsActive) {
                    camIsActive = false;
                    onPause();
                    getStatusById(newId);
                    spin.setVisibility(View.VISIBLE);
                    btnAction.setVisibility(View.VISIBLE);
                    btnSaveID.setVisibility(View.VISIBLE);
                    btnScan.setText("Re-scanner");
                } else {
                    camIsActive = true;
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ScannedBarcodeActivity.this);
                builder1.setMessage("Toutes les informations sont correctes ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                trackProduct(newStatusId);
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
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
        btnSaveID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ScannedBarcodeActivity.this);
                builder1.setMessage("Toutes les informations sont correctes ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                trackProduct(newStatusId);
                                Intent i = new Intent(ScannedBarcodeActivity.this, SaveIdentityActivity.class);
                                i.putExtra("nCommande", newId);
                                startActivity(i);
                                finish();
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

    }

    public void getStatusById(String idDelivery) {
        String urlgetStatusById = baseUrl + "api-delivery/getStatusById/" + idDelivery;
        Log.i("getStatusById", "url : " + urlgetStatusById + " args : " + idDelivery);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlgetStatusById, (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        statusById = response.getString("trackingDesc");
                        Log.i("getStatusById", "response : " + statusById);
                        spinnerPosition = adapter.getPosition(statusById);
                        Log.i("getStatusById", "spinnerPosition : " + spinnerPosition);
                        spin.setSelection(spinnerPosition);
                        //Toast.makeText(getApplicationContext(), response.getString("trackingDesc"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "[getStatusById] Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(getApplicationContext(), "Error : " + message, Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getidStatus(String status) {

        String urlgetidStatus = baseUrl + "api-delivery/getIdStatus/" + status;
        Log.i("getidStatus", "url : " + urlgetidStatus + " args : " + status);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlgetidStatus, (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        newStatusId = response.getString("id");

                        //Toast.makeText(getApplicationContext(), response.getString("id"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //txtBarcodeValue.setText("Resposne : " + response.toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "[getidStatus] Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(getApplicationContext(), "Error : " + message, Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void trackProduct(String newstatus) {
        String todayDate = Constant.getToday("yyyy-MM-dd");
        String[] result = baseUrlTrackId.split("/");
        String url = baseUrl + "trackingdelivery/updatetracking/" + result[0] + "/" + newstatus + "/" + todayDate;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //txtBarcodeValue.setText("Resposne : " + response.toString());
                    Toast.makeText(getApplicationContext(), "Tracking updated", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(getApplicationContext(), "Error : " + message, Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void loadSpinnerData(String url) {
        Log.i("loadSpinnerData", " args : " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("loadSpinnerData", " args : " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("success") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("trackingstatus");
                        Log.i("in condition success", " args : " + jsonArray);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String country = jsonObject1.getString("trackingDesc");
                            Log.i("in loop", " args : " + country);
                            trackingStatus.add(country);
                        }
                    }
                    //  spin.setAdapter(new ArrayAdapter<String>(ScannedBarcodeActivity.this, android.R.layout.simple_spinner_dropdown_item, trackingStatus));
                    adapter = new ArrayAdapter<String>(ScannedBarcodeActivity.this, android.R.layout.simple_spinner_item, trackingStatus);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("loadSpinnerData Error", " args : " + e.getMessage());
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {

                            baseUrlTrackId = barcodes.valueAt(0).displayValue;
                            String[] result = baseUrlTrackId.split("/");
                            Log.i("receiveDetections", " args : " + result);

                            //Result[0] is the position of the argument
                            //need check
                            try {
                                newId = result[0];
                                btnSaveID.setClickable(true);
                            } catch (Exception e) {
                                btnSaveID.setClickable(false);
                                Toast.makeText(getApplicationContext(), "Code Qr invalide", Toast.LENGTH_SHORT).show();
                            }
                            intentData = barcodes.valueAt(0).displayValue;
                            txtBarcodeValue.setText(intentData);

                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1);
                            txtBarcodeValue.setText(barcodes.valueAt(0).displayValue);
                            txtBarcodeValue.setVisibility(View.VISIBLE);
                            //change btn text
                            btnScan.setText("SUIVANT");
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}