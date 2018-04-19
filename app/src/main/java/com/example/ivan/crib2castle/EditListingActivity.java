package com.example.ivan.crib2castle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditListingActivity extends BaseActivity {

    private String uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);
        uId = getIntent().getStringExtra("uId");
        loadActionBar(uId);
    }
}
