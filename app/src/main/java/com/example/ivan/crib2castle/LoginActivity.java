package com.example.ivan.crib2castle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setting widgets
        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);

        // setting listeners
        tvLogin.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Intent i = new Intent("com.example.ivan.crib2castle.SearchActivity");
               startActivity(i);
           }
        });
    }
}
