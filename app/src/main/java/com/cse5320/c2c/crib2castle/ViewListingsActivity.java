package com.cse5320.c2c.crib2castle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewListingsActivity extends BaseActivity {

    String uId;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cse5320.c2c.crib2castle.R.layout.activity_view_listings);
        uId = getIntent().getStringExtra("uId");
        loadActionBar(uId);
        loadHomesFromDb();
        context = ViewListingsActivity.this;

        ListView lvHomes = (ListView) findViewById(com.cse5320.c2c.crib2castle.R.id.lvHomes);

        lvHomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Home home = (Home) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, EditListingActivity.class);
                intent.putExtra("uId", uId);
                intent.putExtra("home", home);
                startActivity(intent);
            }
        });
    }

    /*
 * loads the listview with homes from the database
 */
    public void loadHomesFromDb() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("homes");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Home> homeArrayList = new ArrayList<>();
                for(DataSnapshot childrenSnapshot:dataSnapshot.getChildren()) {
                    Home home = childrenSnapshot.getValue(Home.class);
                    if(home.getuId().equals(uId)) {
                        homeArrayList.add(home);
                    }
                }

                ViewListingsActivity.this.populateListview(homeArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("C2C", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void populateListview(ArrayList<Home> homeArrayList) {

        ListView lvHomes = (ListView) findViewById(com.cse5320.c2c.crib2castle.R.id.lvHomes);

        HomeListAdapter homeListAdapter = new HomeListAdapter(this, com.cse5320.c2c.crib2castle.R.layout.home_item, homeArrayList);
        lvHomes.setAdapter(homeListAdapter);

    }
}
