package com.example.ivan.crib2castle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ivan on 2/24/18.
 */

public class HomeListAdapter extends ArrayAdapter<Home> {
    private ArrayList<Home> homeArrayList;
    private Context context;
    private LayoutInflater inflater;
    private int resource;

    public HomeListAdapter(Context context, int resource, ArrayList<Home> homeArrayList) {
        super(context, R.layout.home_item, homeArrayList);
        this.homeArrayList = homeArrayList;
        this.context = context;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if(convertView == null) convertView = inflater.inflate(resource, null);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        TextView tvBedBaths = (TextView) convertView.findViewById(R.id.tvBedBaths);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);

        Utils u = new Utils();
        Home home = homeArrayList.get(i);
        tvAddress.setText(home.getAddress().toString());
        tvBedBaths.setText(home.getBedrooms()+"bds | "+home.getBathrooms()+"bths\n"+home.getSqft()+" sqft");
        tvPrice.setText("$"+u.numberToCurrency((long) home.getPrice()));

        return convertView;

    }

}
