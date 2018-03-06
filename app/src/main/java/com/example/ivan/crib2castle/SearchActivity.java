package com.example.ivan.crib2castle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Ivan on 2/17/18.
 */

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String uId = getIntent().getStringExtra("UserId");
        loadActionBar(uId.equals("-1"));
        loadHomesFromDB();





    }




    /*
     * loads the listview with homes from the database
     */
    public void loadHomesFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("homes");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Home> homeArrayList = new ArrayList<>();
                for(DataSnapshot childrenSnapshot:dataSnapshot.getChildren()) {
                    Home home = childrenSnapshot.getValue(Home.class);
                    homeArrayList.add(home);
                }

                SearchActivity.this.populateListview(homeArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("C2C", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void populateListview(ArrayList<Home> homeArrayList) {

        ListView lvHomes = (ListView) findViewById(R.id.lvHomes);

        HomeListAdapter homeListAdapter = new HomeListAdapter(this, R.layout.home_item, homeArrayList);
        lvHomes.setAdapter(homeListAdapter);
    }

}
