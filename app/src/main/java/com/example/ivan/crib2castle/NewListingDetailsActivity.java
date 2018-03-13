package com.example.ivan.crib2castle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class NewListingDetailsActivity extends BaseActivity implements QuandlApiResponse {

    private Home home;
    private double ppsqft;
    private TextView tvEstimate;
    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing_details);

        home = (Home) getIntent().getSerializableExtra("home");
        uId = getIntent().getStringExtra("uId");
        loadWidgets();
        loadActionBar(uId);
        tvEstimate = (TextView) findViewById(R.id.tvEstimate);

        Log.d("Address", home.getAddress().toSingleLineString());

        QuandlApi quandlApi = new QuandlApi();
        quandlApi.delegate = NewListingDetailsActivity.this;
        quandlApi.execute(home.getAddress().getZip());
    }

    public void loadWidgets() {
        final TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        final EditText etSqft = (EditText) findViewById(R.id.etSqft);

        etSqft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(etSqft.getText().toString().length() != 0) {
                    int sqft = Integer.parseInt(etSqft.getText().toString());
                    tvEstimate.setText("Quandl Estimate\n"+String.valueOf(Math.round(ppsqft*sqft)));
                }
            }
        });

        tvAddress.setText(home.getAddress().toSingleLineString());
    }


    @Override
    public void quandlApiFinish(Double ppsqft) {
        this.ppsqft = ppsqft;
    }
}
