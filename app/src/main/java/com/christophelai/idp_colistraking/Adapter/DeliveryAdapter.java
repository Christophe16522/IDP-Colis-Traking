package com.christophelai.idp_colistraking.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.christophelai.idp_colistraking.R;
import com.christophelai.idp_colistraking.model.Delivery;

import java.util.List;

public class DeliveryAdapter extends ArrayAdapter<Delivery> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Delivery> deliveryItems;
    private Context mCtx;


    public DeliveryAdapter(List<Delivery> deliveryItems, Context mCtx) {
        super(mCtx, R.layout.list_delivery, deliveryItems);
        this.deliveryItems = deliveryItems;
        this.mCtx = mCtx;
    }

    @Override
    public int getCount() {
        return deliveryItems.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View listViewItem = inflater.inflate(R.layout.list_delivery, null, true);
        TextView textViewNcommande = listViewItem.findViewById(R.id.textViewNcommande);
        TextView textViewNsuivi = listViewItem.findViewById(R.id.textViewNSuivi);
        Delivery delivery = deliveryItems.get(position);
        textViewNcommande.setText(delivery.getnComande());
        textViewNsuivi.setText(delivery.getnSuivi());
        return listViewItem;
    }
}
