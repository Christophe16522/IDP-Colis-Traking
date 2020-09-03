package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class Login extends Activity implements View.OnClickListener {
    Button btnLogin;
    ProgressBar progressBar;
    String email, pass, url, res, status;
    SharedPreferences pref;
    EditText editEmail, editPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressLogin);
        editEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editPass = (EditText) findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        pref = getSharedPreferences("user_details", MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                progressBar.setVisibility(View.VISIBLE);
                email = editEmail.getText().toString();
                pass = editPass.getText().toString();
                url = Constant.SERVER + "/api-login/" + email + "/" + pass;
                login();
        }
    }

    public void login() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        status = response.getString("status");
                        res = response.getString("response");
                        Log.i("Login", " args : " + res);

                        if (status.equals("200")) {
                            JSONObject user = new JSONObject(res);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("idCarrier", user.getString("id"));
                            editor.putString("fullname", user.getString("fullname"));
                            editor.putString("email", user.getString("email"));
                            editor.commit();
                            startActivity(new Intent(Login.this, MainActivity.class));
                        } else if (status.equals("201")) {
                            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
}