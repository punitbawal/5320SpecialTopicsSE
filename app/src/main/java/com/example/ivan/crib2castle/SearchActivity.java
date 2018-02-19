package com.example.ivan.crib2castle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Ivan on 2/17/18.
 */

public class SearchActivity extends AppCompatActivity implements QuandlResponse {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        QuandlAPI q = new QuandlAPI();
        q.delegate=this;
        q.execute("76006");



    }



    @Override
    public void processFinish(Double ppsqft) {
        TextView tvTest = (TextView) findViewById(R.id.tvTest);
        tvTest.setText(String.valueOf(ppsqft));
    }
}
