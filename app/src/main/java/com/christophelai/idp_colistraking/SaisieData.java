package com.christophelai.idp_colistraking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.christophelai.idp_colistraking.Adapter.DeliveryAdapter;
import com.christophelai.idp_colistraking.model.Delivery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SaisieData extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnCheckData;
    String baseUrl = Constant.SERVER + "/";
    EditText idComande;
    TextView txtDelivery;
    Delivery delivery = null;
    Spinner spinChoiceSaisiData;
    String[] spinChoiceSaisiDataList = {"Nom", "Numero de Commande", "Numero de Suivis"};
    ArrayAdapter<String> adapter;
    String choice = null;
    List<Delivery> deliveryList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisie_data);
        txtDelivery = findViewById(R.id.txtDelivery);
        spinChoiceSaisiData = findViewById(R.id.choix_recherche_saisie_data);
        adapter = new ArrayAdapter<String>(SaisieData.this, android.R.layout.simple_spinner_item, spinChoiceSaisiDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinChoiceSaisiData.setAdapter(adapter);
        spinChoiceSaisiData.setOnItemSelectedListener(this);
        listView = (ListView) findViewById(R.id.list_view_search_delivery);
        deliveryList = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Delivery selectedItem = (Delivery) parent.getItemAtPosition(position);
                Intent i = new Intent(SaisieData.this, DetailDelivery.class);
                i.putExtra("id", selectedItem.getId());
                i.putExtra("nomComplet", selectedItem.getNomComplet());
                i.putExtra("adresse", selectedItem.getAdresse());
                i.putExtra("telephone", selectedItem.getTelephone());
                i.putExtra("nComande", selectedItem.getnComande());
                i.putExtra("ville", selectedItem.getVille());
                i.putExtra("nSuivi", selectedItem.getnSuivi());
                i.putExtra("trackingDesc", selectedItem.getStatus());
                i.putExtra("comeFrom", "saisieData");
                startActivity(i);
                finish();
            }
        });
        idComande = (EditText) findViewById(R.id.txtidCommande);
        btnCheckData = findViewById(R.id.btnCheckData);
        btnCheckData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeliveryById(idComande.getText().toString());
                //open details delivery

            }
        });
    }

    public void getDeliveryById(String nSuivi) {
        deliveryList.clear();
        String urlgetDeliveryById = baseUrl + "api-delivery/getDeliveryBy/" + nSuivi + "/" + choice;
        Log.i("getDeliveryById", "url : " + urlgetDeliveryById + " args : " + nSuivi);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, urlgetDeliveryById, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                      /*  if(response.getString("status").equals("201")){
                            txtDelivery.setText(response.getString("response"));
                        }*/

                        JSONObject obj = new JSONObject(response);
                        JSONArray heroArray = obj.getJSONArray("response");
                        for (int i = 0; i < heroArray.length(); i++) {
                            JSONObject deliveryObject = heroArray.getJSONObject(i);
                            Delivery delivery = new Delivery(
                                    deliveryObject.getInt("id"),
                                    deliveryObject.getString("nomComplet"),
                                    deliveryObject.getString("adresse"),
                                    deliveryObject.getString("telephone"),
                                    deliveryObject.getString("nComande"),
                                    deliveryObject.getString("ville"),
                                    deliveryObject.getString("nSuivi"),
                                    deliveryObject.getString("trackingDesc"));
                            deliveryList.add(delivery);
                        }
                        DeliveryAdapter adapter = new DeliveryAdapter(deliveryList, getApplicationContext());
                        listView.setAdapter(adapter);

                         /*   if (delivery != null) {
                            Intent i = new Intent(SaisieData.this, DetailDelivery.class);
                            i.putExtra("id", delivery.getId());
                            i.putExtra("nomComplet", delivery.getNomComplet());
                            i.putExtra("adresse", delivery.getAdresse());
                            i.putExtra("telephone", delivery.getTelephone());
                            i.putExtra("nComande", delivery.getnComande());
                            i.putExtra("ville", delivery.getVille());
                            i.putExtra("nSuivi", delivery.getnSuivi());
                            i.putExtra("comeFrom", "saisieData");
                            startActivity(i);
                            finish();
                        }*/
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).toString().equals("Nom")) {
            choice = "nom";
        } else if (parent.getItemAtPosition(position).toString().equals("Numero de Commande")) {
            choice = "ncommande";
        } else if (parent.getItemAtPosition(position).toString().equals("Numero de Suivis")) {
            choice = "nsuivi";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}