package com.example.ivan.crib2castle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        final EditText etYear = (EditText) findViewById(R.id.etYear);
        final EditText etSqft = (EditText) findViewById(R.id.etSqft);
        final EditText etBeds = (EditText) findViewById(R.id.etBeds);
        final EditText etBaths = (EditText) findViewById(R.id.etBaths);
        final EditText etPrice = (EditText) findViewById(R.id.etPrice);
        final EditText etDetails = (EditText) findViewById(R.id.etDetails);




        final TextView tvSubmit = (TextView) findViewById(R.id.tvSubmit);


        etSqft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(etSqft.getText().toString().length() != 0) {
                    int sqft = Integer.parseInt(etSqft.getText().toString());
                    Utils utils = new Utils();
                    tvEstimate.setText("Quandl Estimate\n$"+utils.numberToCurrency(Math.round(ppsqft*sqft)));
                } else {
                    tvEstimate.setText("Quandl Estimate");
                }
            }
        });

        tvAddress.setText(home.getAddress().toSingleLineString());

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etYear.getText().toString().length()==0 || etSqft.getText().toString().length()==0
                        || etBeds.getText().toString().length()==0 || etBaths.getText().toString().length()==0
                        || etPrice.getText().toString().length()==0 || etDetails.getText().toString().length()==0) {
                    Toast.makeText(NewListingDetailsActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                home.sethId(new Utils().randString(10));
                home.setYear(Integer.parseInt(etYear.getText().toString()));
                home.setSqft(Integer.parseInt(etSqft.getText().toString()));
                home.setBedrooms(Double.parseDouble(etBeds.getText().toString()));
                home.setBathrooms(Double.parseDouble(etBaths.getText().toString()));
                home.setPrice(Double.parseDouble(etPrice.getText().toString()));
                home.setDetails(etDetails.getText().toString());
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("homes").child(home.gethId()).setValue(home);
                Toast.makeText(NewListingDetailsActivity.this, "New listing added", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void quandlApiFinish(Double ppsqft) {
        this.ppsqft = ppsqft;
    }
}
