package com.example.ivan.crib2castle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NewListingDetailsActivity extends BaseActivity implements QuandlApiResponse {

    final static int RESULT_LOAD_IMAGE = 1;
    private Home home;
    private double ppsqft;
    private TextView tvEstimate;
    private String uId;
    private ImageSwitcher iswPhotos;
    private ArrayList<Bitmap> imageBitmaps;
    private int imgIndex;
    private int imagesUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing_details);

        home = (Home) getIntent().getSerializableExtra("home");
        uId = getIntent().getStringExtra("uId");
        loadWidgets();
        loadActionBar(uId);
        tvEstimate = (TextView) findViewById(R.id.tvEstimate);

        imageBitmaps = new ArrayList<>();
        imagesUploaded=0;

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
        final TextView tvNext = (TextView) findViewById(R.id.tvNext);
        final TextView tvPrev = (TextView) findViewById(R.id.tvPrev);
        final Button btnDelete = (Button) findViewById(R.id.btnDelete);
        final Button btnUpload = (Button) findViewById(R.id.btnUpload);
        iswPhotos = (ImageSwitcher) findViewById(R.id.iswPhotos);
        final ImageView ivFullscreen = (ImageView) findViewById(R.id.ivFullscreen);



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


        ivFullscreen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivFullscreen.setImageBitmap(null);
                ivFullscreen.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
                btnUpload.setVisibility(View.VISIBLE);
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgIndex = (imgIndex+1) % imageBitmaps.size();
                setImageSwitcher();
            }
        });

        tvPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgIndex = (imgIndex > 0) ? imgIndex-1 : imageBitmaps.size()-1;
                setImageSwitcher();
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
               if(imageBitmaps.size() == 0) return;
               imageBitmaps.remove(imgIndex);
               if(imageBitmaps.size()==0) {
                   iswPhotos.setImageResource(0);
               } else {
                   if(imgIndex >= imageBitmaps.size()) imgIndex = 0;
                   setImageSwitcher();
               }
           }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etYear.getText().toString().length()==0 || etSqft.getText().toString().length()==0
                        || etBeds.getText().toString().length()==0 || etBaths.getText().toString().length()==0
                        || etPrice.getText().toString().length()==0 || etDetails.getText().toString().length()==0) {
                    Toast.makeText(NewListingDetailsActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(imageBitmaps.size() <= 0) {
                    Toast.makeText(NewListingDetailsActivity.this, "Please upload at least one photo", Toast.LENGTH_SHORT).show();
                    return;
                }
                home.sethId(new Utils().randString(10));
                home.setuId(uId);
                home.setYear(Integer.parseInt(etYear.getText().toString()));
                home.setSqft(Integer.parseInt(etSqft.getText().toString()));
                home.setBedrooms(Integer.parseInt(etBeds.getText().toString()));
                home.setBathrooms(Integer.parseInt(etBaths.getText().toString()));
                home.setPrice(Long.parseLong(etPrice.getText().toString()));
                home.setDetails(etDetails.getText().toString());
                home.setNumImages(imageBitmaps.size());
                Toast.makeText(NewListingDetailsActivity.this, "Adding listing... Please be patient", Toast.LENGTH_LONG).show();

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("homes").child(home.gethId()).setValue(home);

                for(int i=0;i<imageBitmaps.size(); i++) {
                    uploadPhotoToDb(imageBitmaps.get(i), home.gethId()+"-"+String.valueOf(i));
                }
            }
        });

        iswPhotos.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView iv = new ImageView(NewListingDetailsActivity.this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                iv.setLayoutParams(layoutParams);
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageBitmaps.size() <= 0) return;
                        ivFullscreen.setImageBitmap(imageBitmaps.get(imgIndex));
                        ivFullscreen.bringToFront();
                        ivFullscreen.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.INVISIBLE);
                        btnUpload.setVisibility(View.INVISIBLE);
                    }
                });
                return iv;
            }
        });
        Animation animIn = AnimationUtils.loadAnimation(NewListingDetailsActivity.this, android.R.anim.fade_in);
        Animation animOut = AnimationUtils.loadAnimation(NewListingDetailsActivity.this, android.R.anim.fade_out);
        iswPhotos.setInAnimation(animIn);
        iswPhotos.setOutAnimation(animOut);
    }


    @Override
    public void quandlApiFinish(Double ppsqft) {
        this.ppsqft = ppsqft;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageBitmaps.add(bitmap);
            imgIndex = imageBitmaps.size()-1;
            setImageSwitcher();
        }
    }

    public void setImageSwitcher() {
        Bitmap bmp = imageBitmaps.get(imgIndex);
        double scaleFactor=1.0;
        Double scaledWidth;
        Double scaledHeight;
        if(Math.max(bmp.getWidth(), bmp.getHeight())>4096) {
            scaleFactor=4096.0/Math.max(bmp.getHeight(), bmp.getWidth());
            scaledWidth=bmp.getWidth()*scaleFactor;
            scaledHeight=bmp.getHeight()*scaleFactor;
            bmp=Bitmap.createScaledBitmap(bmp, scaledWidth.intValue(), scaledHeight.intValue(), false);
        }
        iswPhotos.setImageDrawable(new BitmapDrawable(bmp));
    }

    public void uploadPhotoToDb(Bitmap bitmap, String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imgStorageRef = storage.getReference().child("images/"+name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        UploadTask uploadTask = imgStorageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewListingDetailsActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesUploaded++;
                if(imagesUploaded==imageBitmaps.size()) {
                    Intent i = new Intent(NewListingDetailsActivity.this, SearchActivity.class);
                    i.putExtra("uId", uId);
                    startActivity(i);
                }
            }
        });

    }
}
