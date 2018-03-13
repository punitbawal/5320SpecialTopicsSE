package com.example.ivan.crib2castle;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Ivan on 3/12/18.
 */

public class NewListingAddressActivity extends BaseActivity implements OnMapReadyCallback, LocationApiResponse {

    private boolean verify;
    private MapView mapView;
    private GoogleMap mMap;
    private Marker addressMarker;
    private TextView tvVerifySubmit;
    private Double[] latLng;
    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing_address);

        latLng = new Double[2];
        verify = true;
        uId = getIntent().getStringExtra("uId");
        loadActionBar(uId);
        loadWidgets();
        mapView = (MapView) findViewById(R.id.map);

        if(mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }



    }

    public void loadWidgets() {
        final EditText etAddress1 = (EditText) findViewById(R.id.etAddress1);
        final EditText etAddress2 = (EditText) findViewById(R.id.etAddress2);
        final EditText etCity = (EditText) findViewById(R.id.etCity);
        final EditText etState = (EditText) findViewById(R.id.etState);
        final EditText etZip = (EditText) findViewById(R.id.etZip);
        tvVerifySubmit = (TextView) findViewById(R.id.tvVerifySubmit);

        etAddress1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvVerifySubmit.setText("Verify Address");
                verify=true;
            }
        });

        etAddress2.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvVerifySubmit.setText("Verify Address");
                verify=true;
            }
        });

        etCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvVerifySubmit.setText("Verify Address");
                verify=true;
            }
        });

        etState.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvVerifySubmit.setText("Verify Address");
                verify=true;
            }
        });

        etZip.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvVerifySubmit.setText("Verify Address");
                verify=true;
            }
        });

        tvVerifySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etAddress1.getText().toString().length()==0 || etCity.getText().toString().length()==0
                        || etState.getText().toString().length()==0 || etZip.getText().toString().length()==0) {
                    Toast.makeText(NewListingAddressActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Address address = new Address();
                address.setAddress1(etAddress1.getText().toString());
                if(etAddress2.getText().toString().length() != 0) address.setAddress2(etAddress2.getText().toString());
                address.setCity(etCity.getText().toString());
                address.setState(etState.getText().toString());
                address.setZip(etZip.getText().toString());
                address.setLatitude(latLng[0]);
                address.setLongitude(latLng[1]);

                if(verify) {

                    final boolean[] duplicateAddress = new boolean[] {false};

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("homes");

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot childrenSnapshot:dataSnapshot.getChildren()) {
                                Home home = childrenSnapshot.getValue(Home.class);
                                Utils u = new Utils();
                                if(u.compareAddresses(home.getAddress(), address)) {
                                    duplicateAddress[0] = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Failed to read value
                            Log.w("C2C", "Failed to read value.", databaseError.toException());
                        }
                    });

                    if(!duplicateAddress[0]) {
                        LocationApi locApi = new LocationApi();
                        locApi.delegate = NewListingAddressActivity.this;
                        locApi.execute(address.toSingleLineString());
                    } else {
                        Toast.makeText(NewListingAddressActivity.this, "Address already listed", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Home home = new Home();
                    home.setAddress(address);
                    Intent i = new Intent(NewListingAddressActivity.this, NewListingDetailsActivity.class);
                    i.putExtra("home", home);
                    i.putExtra("uId", uId);
                    startActivity(i);
                }
            }
        });

    }

    public void updateMap(Double lat, Double lng) {

        LatLng loc = new LatLng(lat, lng);

        if(addressMarker != null) addressMarker.remove();
        addressMarker = mMap.addMarker(new MarkerOptions().position(loc).title("Address Marker"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        tvVerifySubmit.setText("Add House Details");
        verify = false;

    }



    @Override
    public void locationApiFinish(Double[] result) {
        if(Math.abs(result[0]) >= 0.00001 && Math.abs(result[1]) >= 0.00001) {
            latLng = result;
            updateMap(result[0], result[1]);
        } else {
            Toast.makeText(NewListingAddressActivity.this, "Address not found, please try again.", Toast.LENGTH_SHORT).show();
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
