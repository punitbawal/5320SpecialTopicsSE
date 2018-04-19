package com.example.ivan.crib2castle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

public class FavoritesActivity extends BaseActivity {

    private String uId;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        uId = getIntent().getStringExtra("uId");
        loadActionBar(uId);

        loadHomesFromDb();
        context = FavoritesActivity.this;

        ListView lvHomes = (ListView) findViewById(R.id.lvHomes);

        lvHomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Home home = (Home) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, HomeDetailsActivity.class);
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
        DatabaseReference myRef = database.getReference("favorites").child(uId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> homeIds = new ArrayList<>();
                final ArrayList<Home> homeArrayList = new ArrayList<>();

                for(DataSnapshot childrenSnapshot:dataSnapshot.getChildren()) {
                    homeIds.add(childrenSnapshot.getKey());
                }

                DatabaseReference hmRef = FirebaseDatabase.getInstance().getReference("homes");
                hmRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        for(String hId:homeIds) {
                            DataSnapshot childSnapshot = dataSnapshot1.child(hId);
                            Home h=childSnapshot.getValue(Home.class);
                            homeArrayList.add(h);
                            FavoritesActivity.this.populateListview(homeArrayList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



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
