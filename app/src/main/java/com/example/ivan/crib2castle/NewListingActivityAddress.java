package com.example.ivan.crib2castle;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ivan on 3/12/18.
 */

public class NewListingActivityAddress extends BaseActivity implements OnMapReadyCallback, LocationApiResponse {

    private MapView mapView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing_address);

        String uId = getIntent().getStringExtra("uId");
        loadActionBar(uId);
        mapView = (MapView) findViewById(R.id.map);

        if(mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }



    }

    public void updateMap() {

    }



    @Override
    public void locationApiFinish(Double[] result) {
        if(Math.abs(result[0]) >= 0.00001 && Math.abs(result[1]) >= 0.00001) {
            updateMap();
        } else {
            Toast.makeText(NewListingActivityAddress.this, "Address not found, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng usa = new LatLng(37, -92);
        // mMap.addMarker(new MarkerOptions().position(usa).title("Marker in USA"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(usa));
    }
}
