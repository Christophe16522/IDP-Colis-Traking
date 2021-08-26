package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.christophelai.idp_colistraking.Adapter.DeliveryAdapter;
import com.christophelai.idp_colistraking.model.Delivery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListDelivery extends Activity {

    String baseUrl = Constant.SERVER + "/";
    String urlListDailyDelivery = baseUrl + "api-list-daily-delivery";
    List<Delivery> deliveryList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_delivery);

        listView = (ListView) findViewById(R.id.list_view_delivery);
        deliveryList = new ArrayList<>();

        loadDeliveryList();
        //getDailyDelivery();
        //add listener in list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Delivery selectedItem = (Delivery) parent.getItemAtPosition(position);
                Intent i = new Intent(ListDelivery.this, DetailDelivery.class);
                i.putExtra("id",  selectedItem.getId());
                i.putExtra("nomComplet", selectedItem.getNomComplet());
                i.putExtra("adresse", selectedItem.getAdresse());
                i.putExtra("telephone", selectedItem.getTelephone());
                i.putExtra("nComande", selectedItem.getnComande());
                i.putExtra("ville", selectedItem.getVille());
                i.putExtra("nSuivi", selectedItem.getnSuivi());
                startActivity(i);
            }
        });
    }

    private void loadDeliveryList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlListDailyDelivery,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONArray heroArray = obj.getJSONArray("response");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < heroArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject deliveryObject = heroArray.getJSONObject(i);

                                //creating a hero object and giving them the values from json object
                                //String nomComplet, String adresse, String telephone, String nComande, String ville, String nSuivi
                                Delivery delivery = new Delivery(
                                        deliveryObject.getInt("id"),
                                        deliveryObject.getString("nomComplet"),
                                        deliveryObject.getString("adresse"),
                                        deliveryObject.getString("telephone"),
                                        deliveryObject.getString("nComande"),
                                        deliveryObject.getString("ville"),
                                        deliveryObject.getString("nSuivi"));
                                //adding the hero to deliverylist
                                deliveryList.add(delivery);
                            }

                            //creating custom adapter object
                            DeliveryAdapter adapter = new DeliveryAdapter(deliveryList, getApplicationContext());

                            //adding the adapter to listview
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    private void getDailyDelivery() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlListDailyDelivery, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("commande_count") >= 1) {
                        Log.i("loadSpinnerData", " args : " + response);
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ListDelivery.this);
                        builder1.setMessage("Pas de livraison aujourd'hui.");
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
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }
}