package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignActivity extends AppCompatActivity {
    PaintView paintView;
    private String UploadUrl = "http://192.168.100.19:8000/api-delivery/upload";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paintView = new PaintView(this);
        setContentView(paintView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                uploadImage();
                return true;
            case R.id.clear:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String captureScreen() {
        paintView.setDrawingCacheEnabled(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        paintView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void uploadImage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response", " args : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Response = jsonObject.getString("response");
                    Toast.makeText(SignActivity.this, Response, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Log.e("Response", " args : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response", " args : " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", UUID.randomUUID().toString());
                params.put("image", captureScreen());
                return params;
            }
        };
        MySingleton.getInstance(SignActivity.this).addToRequestQue(stringRequest);
    }
}