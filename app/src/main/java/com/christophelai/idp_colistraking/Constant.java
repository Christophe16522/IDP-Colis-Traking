package com.christophelai.idp_colistraking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Constant {
    public static final String SERVER = "http://192.168.100.19:8000";

    public static String getToday(String patern) {
        //yyyy-MM-dd
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(patern, Locale.getDefault());
        String todayDate = df.format(c);
        return todayDate;
    }

    public static void SaveLog(String msg, String idCarrier, final Context context) {
        String date = getToday("yyyy-MM-dd hh:mm:ss");
        String url = SERVER + "/api-log/" + msg + "/" + date + "/" + idCarrier;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //newStatusId = response.getString("id");
                    try {
                        Log.i("Save Log", "result : " + response.getString("response"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
                    Toast.makeText(context, "Error : " + message, Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(context, "Error in save logs : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
