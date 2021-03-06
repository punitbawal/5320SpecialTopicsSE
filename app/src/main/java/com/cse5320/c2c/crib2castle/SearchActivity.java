package com.cse5320.c2c.crib2castle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Ivan on 2/17/18.
 */

public class SearchActivity extends BaseActivity implements LocationApiResponse {

    String uId;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cse5320.c2c.crib2castle.R.layout.activity_search);
        progressBar = (ProgressBar) findViewById(com.cse5320.c2c.crib2castle.R.id.pbSearch);
        progressBar.setVisibility(View.GONE);
        uId = getIntent().getStringExtra("uId");
        loadActionBar(uId);
        loadWidgets();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void loadWidgets() {
        final EditText etSearch = (EditText) findViewById(com.cse5320.c2c.crib2castle.R.id.etSearch);
        ImageButton ibSearch = (ImageButton) findViewById(com.cse5320.c2c.crib2castle.R.id.ibSearch);
        ListView lvHomes = (ListView) findViewById(com.cse5320.c2c.crib2castle.R.id.lvHomes);
        final Context context = SearchActivity.this;

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                LocationApi locApi = new LocationApi();
                locApi.delegate = SearchActivity.this;
                locApi.execute(etSearch.getText().toString());
            }
        });

        lvHomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar.setVisibility(View.VISIBLE);
                Home home = (Home) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, HomeDetailsActivity.class);
                intent.putExtra("uId", uId);
                intent.putExtra("home", home);
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
            }
        });

    }




    /*
     * loads the listview with homes from the database
     */
    public void loadHomesFromDb(Double latitude, Double longitude, Double radius) {

        final double lat1 = latitude;
        final double lng1 = longitude;
        final double dist = radius;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("homes");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Home> homeArrayList = new ArrayList<>();
                for(DataSnapshot childrenSnapshot:dataSnapshot.getChildren()) {
                    Home home = childrenSnapshot.getValue(Home.class);
                    double lat2 = home.getAddress().getLatitude();
                    double lng2 = home.getAddress().getLongitude();
                    Utils u = new Utils();
                    if(u.getDistanceFromLatLng(lat1, lng1, lat2, lng2) <= dist) {
                        homeArrayList.add(home);
                    }
                }

                SearchActivity.this.populateListview(homeArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("C2C", "Failed to read value.", databaseError.toException());
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    public void populateListview(ArrayList<Home> homeArrayList) {

        TextView tvNoResults = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvNoResults);

        if(homeArrayList.size() == 0){
            tvNoResults.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No homes found. Try being more specific in your search.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

        }

        ListView lvHomes = (ListView) findViewById(com.cse5320.c2c.crib2castle.R.id.lvHomes);

        HomeListAdapter homeListAdapter = new HomeListAdapter(this, com.cse5320.c2c.crib2castle.R.layout.home_item, homeArrayList);
        lvHomes.setAdapter(homeListAdapter);

    }

    @Override
    public void locationApiFinish(Double[] result) {
        loadHomesFromDb(result[0], result[1], 10.0);
    }

}
