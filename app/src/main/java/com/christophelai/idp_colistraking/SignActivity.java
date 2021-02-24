package com.christophelai.idp_colistraking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
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

public class SignActivity extends AppCompatActivity {
    PaintView paintView;
    String nCommande, today, stringImage;
    private Bitmap bitmapImage;
    private byte[] byteArray;
    private String UploadUrl = Constant.SERVER + "/api-delivery/upload";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent saveIdentityIntent = getIntent();
        today = Constant.getToday("yyyy_MM_dd_HH_mm_ss");
        nCommande = saveIdentityIntent.getStringExtra("nCommande");
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
    public void onBackPressed() {
      /*  Intent i = new Intent(SignActivity.this, SaveIdentityActivity.class);
        startActivity(i);
        finish();*/
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent i = new Intent(SignActivity.this, SaveIdentityActivity.class);
                        i.putExtra("nCommande", nCommande);
                        startActivity(i);
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(SignActivity.this, SaveIdentityActivity.class);
        i.putExtra("nCommande", nCommande);
         switch (item.getItemId()) {
            case R.id.back:
                startActivity(i);
                finish();
                return true;
            case R.id.save:
                 uploadImage();
                startActivity(i);
                finish();
                return true;
            case R.id.clear:
                Intent intent = getIntent();
                finish();
                intent.putExtra("nCommande", nCommande);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String captureScreen() throws Exception {
        paintView.setDrawingCacheEnabled(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        paintView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        Log.e("Test bytearray ", " Taille : " + byteArray.length);
        stringImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //sauvegarde de la signature
        try {
            bitmapImage = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            Log.e("Bitmap image", String.valueOf(bitmapImage));
            if (bitmapImage != null) {
                Log.e("uploadSignatureImage ", " condition : " + bitmapImage);
                MediaStore.Images.Media.insertImage(getContentResolver(),bitmapImage,"singature-" + nCommande + "-" + today,"Signature Description");
            }
        }catch (Exception e){
            throw new Exception("Erreur dans la sauvegarde de la signature");
        }
        return stringImage;
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
                params.put("id", nCommande);
                params.put("name", "singature-" + nCommande + "-" + today);
                try {
                    params.put("image", captureScreen());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        MySingleton.getInstance(SignActivity.this).addToRequestQue(stringRequest);

    }


}