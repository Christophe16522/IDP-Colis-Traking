package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DetailDelivery extends Activity implements AdapterView.OnItemSelectedListener {
    Button btnCall, btnDetailDeliveryWaze, btnFinishDetailDelivery, btnDetailDeliveryMaj;
    ArrayList<String> trackingStatus = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String statusById = "1";
    int spinnerPosition;
    String comeFrom = "listDelivery";
    Spinner spin;
    String baseUrl = Constant.SERVER + "/";
    String urlListTrackingStatus = baseUrl + "api-delivery/getstatus";
    String urlGetAdress = baseUrl + "api-get-location/";
    String longitude, latitude, adresseComplet;
    String newStatusId = "0";
    int idTracking = 0;
    String trackingDesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_delivery);
        spin = findViewById(R.id.spinnerDetailDeliveryStatus);
        btnFinishDetailDelivery = findViewById(R.id.btnFinishDetailDelivery);
        btnDetailDeliveryMaj = findViewById(R.id.btnDetailDeliveryMaj);

        Intent i = getIntent();
        idTracking = i.getIntExtra("id", 0);
        comeFrom = i.getStringExtra("comeFrom");
        //  getStatusById(Integer.toString(idTracking));
        String nomComplet = i.getStringExtra("nomComplet");
        String adresse = i.getStringExtra("adresse");
        final String telephone = i.getStringExtra("telephone");
        final String nComande = i.getStringExtra("nComande");
        String ville = i.getStringExtra("ville");
        String nSuivi = i.getStringExtra("nSuivi");
        trackingDesc = i.getStringExtra("trackingDesc");
        adresseComplet = adresse + " " + ville;
        loadSpinnerData(urlListTrackingStatus);
        TextView detailDeliveryNcommande = (TextView) findViewById(R.id.detailDeliveryNcommande);
        detailDeliveryNcommande.setText(nComande);

        TextView detailDeliveryNsuivi = (TextView) findViewById(R.id.detailDeliveryNsuivi);
        detailDeliveryNsuivi.setText(nSuivi);

        TextView detailDeliveryNomComplet = (TextView) findViewById(R.id.detailDeliveryNomComplet);
        detailDeliveryNomComplet.setText(nomComplet);

        TextView detailDeliveryAdresse = (TextView) findViewById(R.id.detailDeliveryAdresse);
        detailDeliveryAdresse.setText(adresseComplet);



       /* TextView detailDeliveryNcommande = (TextView) findViewById(R.id.detailDeliveryNcommande);
        detailDeliveryNcommande.setText(nComande);

        TextView detailDeliveryNcommande = (TextView) findViewById(R.id.detailDeliveryNcommande);
        detailDeliveryNcommande.setText(nComande);*/
        String url_str = null;
        try {
            URL url = new URL(urlGetAdress + idTracking);
            url_str = url.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setLongLatWaze(url_str);

        btnCall = findViewById(R.id.btnDetailCall);
        btnDetailDeliveryWaze = findViewById(R.id.btnDetailDeliveryWaze);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("telephne a appeler", telephone);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + telephone));
                startActivity(callIntent);
            }
        });

        btnDetailDeliveryWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude != null && longitude != null) {
                    try {
                        String url = "https://waze.com/ul?ll=" + latitude + "," + longitude + "&z=10";
                        Log.e("Url Waze", " url : " + url);

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        // If Waze is not installed, open it in Google Play:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                        startActivity(intent);
                    }
                } else {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", adresseComplet);
                    clipboard.setPrimaryClip(clip);

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailDelivery.this);
                    builder1.setMessage("Geolocation introuvable, merci de tapez l'adresse dans waze.");
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });

        btnFinishDetailDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailDelivery.this);
                builder1.setMessage("Toutes les informations sont correctes ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                trackProduct(newStatusId, idTracking);
                                Intent intent = new Intent(DetailDelivery.this, SaveIdentityActivity.class);
                                intent.putExtra("nCommande", "" + idTracking);
                                startActivity(intent);
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

        btnDetailDeliveryMaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailDelivery.this);
                builder1.setMessage("Toutes les informations sont correctes ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                trackProduct(newStatusId, idTracking);
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
        spin.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        getidStatus(item);
    }

    @Override
    public void onBackPressed() {
        if (comeFrom.equals("listDelivery")) {
            Intent i = new Intent(DetailDelivery.this, ListDelivery.class);
            i.putExtra("dateChoosed", Constant.getToday("yyyy-MM-dd"));
            startActivity(i);
            finish();
        } else if (comeFrom.equals("saisieData")) {
            Intent i = new Intent(DetailDelivery.this, SaisieData.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                        if (adapter != null) {
                            synchronized (adapter) {
                                spinnerPosition = adapter.getPosition(statusById);
                                spin.setSelection(spinnerPosition);
                            }
                        }
                        spin.setVisibility(View.VISIBLE);
                        Log.i("getStatusById", "spinnerPosition : " + spinnerPosition);
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
            e.printStackTrace();
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
                    adapter = new ArrayAdapter<String>(DetailDelivery.this, android.R.layout.simple_spinner_item, trackingStatus);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin.setAdapter(adapter);

                    spinnerPosition = adapter.getPosition(trackingDesc);
                    spin.setSelection(spinnerPosition);
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

    private void setLongLatWaze(String urlGetGeoAdress) {
        Log.e("Url get geolocation", " url : " + urlGetGeoAdress);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlGetGeoAdress, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("message");

                    if (msg.equals("waze_ok")) {
                        JSONObject jsonObject = obj.getJSONObject("response");
                        longitude = jsonObject.getString("longitude");
                        latitude = jsonObject.getString("latitude");
                    } else if (msg.equals("Adresse introuvable")) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", adresseComplet);
                        clipboard.setPrimaryClip(clip);

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailDelivery.this);
                        builder1.setMessage("Geolocation introuvable, merci de tapez l'adresse dans waze.");
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public void trackProduct(String newStatus, int idTracking) {
        String todayDate = Constant.getToday("yyyy-MM-dd");
        String url = baseUrl + "trackingdelivery/updatetracking/" + idTracking + "/" + newStatus + "/" + todayDate;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //txtBarcodeValue.setText("Resposne : " + response.toString());
                    Toast.makeText(getApplicationContext(), "Mise a jour terminé", Toast.LENGTH_LONG).show();
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


}