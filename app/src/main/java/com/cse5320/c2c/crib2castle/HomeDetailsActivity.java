package com.cse5320.c2c.crib2castle;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeDetailsActivity extends BaseActivity implements DownloadImageResponse {

    String uId;
    Home home;
    String[] userInfo;                  // {userName, userEmail}
    boolean isFavorite;
    ArrayList<Bitmap> imageBitmaps;
    int loadedImages;
    ImageSwitcher iswPhotos;
    int imageIndex;
    Context context;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cse5320.c2c.crib2castle.R.layout.activity_home_details);
        userInfo = new String[2];
        uId = getIntent().getStringExtra("uId");
        home = (Home) getIntent().getSerializableExtra("home");
        loadedImages=0;
        imageIndex = 0;
        imageBitmaps = new ArrayList<>();
        context = HomeDetailsActivity.this;
        for(int i=0;i<home.getNumImages();i++) {
            imageBitmaps.add(null);
        }
        progressBar = (ProgressBar) findViewById(com.cse5320.c2c.crib2castle.R.id.pbHomeDetailsImg);
        progressBar.setVisibility(View.GONE);
        setFavorites();
        loadActionBar(uId);
        loadUserNameEmail(home.getuId());
        loadWidgets();
        loadImages();

    }

    public void loadUserNameEmail(String uId) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("c2cusers").child(uId);

        Log.d("C2C", uId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    C2CUser u=dataSnapshot.getValue(C2CUser.class);
                    userInfo[0]=u.getFname()+" "+u.getLname();
                    userInfo[1]=u.getEmail();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("C2C", "Failed to read value.", databaseError.toException());
            }
        });
    }


    public void setFavorites() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("favorites").child(uId);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView ivFavorite = (ImageView) findViewById(com.cse5320.c2c.crib2castle.R.id.ivFavorite);
                ivFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                isFavorite=false;

                for(DataSnapshot childrenSnapshot:dataSnapshot.getChildren()) {
                    String hId = childrenSnapshot.getKey();

                    if(hId.equals(home.gethId())) {
                        ivFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                        isFavorite=true;
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("C2C", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void loadWidgets() {
        TextView tvFavorite = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvFavorite);
        ImageView ivFavorite = (ImageView) findViewById(com.cse5320.c2c.crib2castle.R.id.ivFavorite);
        iswPhotos = (ImageSwitcher) findViewById(com.cse5320.c2c.crib2castle.R.id.iswPhotos);
        TextView tvPrev = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvPrev);
        TextView tvNext = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvNext);
        TextView tvAddress = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvAddress);
        TextView tvPrice = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvPrice);
        TextView tvYear = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvYear);
        TextView tvSqft = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvSqft);
        TextView tvBedBaths = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvBedBaths);
        TextView tvDetails = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvDetails);
        TextView tvContactSeller = (TextView) findViewById(com.cse5320.c2c.crib2castle.R.id.tvContact);
        final ImageView ivFullscreen = (ImageView) findViewById(com.cse5320.c2c.crib2castle.R.id.ivFullscreen);


        if(uId.equals("-1")) {
            tvFavorite.setVisibility(View.GONE);
            ivFavorite.setVisibility(View.GONE);
        }

        tvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = db.getReference().child("favorites");
                if(isFavorite) {
                    dbRef.child(uId).child(home.gethId()).removeValue();
                } else {
                    dbRef.child(uId).child(home.gethId()).setValue(true);
                }
                isFavorite=!isFavorite;
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = db.getReference().child("favorites");
                if(isFavorite) {
                    dbRef.child(uId).child(home.gethId()).removeValue();
                } else {
                    dbRef.child(uId).child(home.gethId()).setValue(true);
                }
                isFavorite=!isFavorite;
            }
        });



        tvAddress.setText(home.getAddress().toSingleLineString());
        tvPrice.setText("$"+(new Utils().numberToCurrency(home.getPrice())));
        tvYear.setText("Built In: "+String.valueOf(home.getYear()));
        tvSqft.setText(String.valueOf(home.getSqft())+" sqft");
        tvBedBaths.setText(String.valueOf(home.getBedrooms())+" bds | "+String.valueOf(home.getBathrooms())+" bths");
        tvDetails.setText(home.getDetails());



        iswPhotos.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                final ImageView iv = new ImageView(HomeDetailsActivity.this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                iv.setLayoutParams(layoutParams);
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageBitmaps.size() <= 0) return;
                        ivFullscreen.setImageBitmap(imageBitmaps.get(imageIndex));
                        ivFullscreen.bringToFront();
                        ivFullscreen.setVisibility(View.VISIBLE);


                    }
                });

                return iv;
            }
        });
        Animation animIn = AnimationUtils.loadAnimation(HomeDetailsActivity.this, android.R.anim.fade_in);
        Animation animOut = AnimationUtils.loadAnimation(HomeDetailsActivity.this, android.R.anim.fade_out);
        iswPhotos.setInAnimation(animIn);
        iswPhotos.setOutAnimation(animOut);

        ivFullscreen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivFullscreen.setImageBitmap(null);
                ivFullscreen.setVisibility(View.GONE);
            }
        });

        tvPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex = (imageIndex > 0) ? imageIndex-1 : imageBitmaps.size()-1;
                setImageSwitcher();
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIndex = (imageIndex+1) % imageBitmaps.size();
                setImageSwitcher();
            }
        });

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Email", userInfo[1]);
                        clipboard.setPrimaryClip(clip);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        tvContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Name: "+userInfo[0]+ "\nEmail: "+userInfo[1]).setPositiveButton("Copy Email", dialogClickListener)
                        .setNegativeButton("OK", dialogClickListener).show();
            }
        });
    }

    public void loadImages() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        for(int i=0;i<home.getNumImages(); i++) {
            StorageReference homesRef = storage.getReference().child("images/"+home.gethId()+"-"+String.valueOf(i));
            final int index=i;
            homesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    DownloadImage di = new DownloadImage();
                    di.delegate = (DownloadImageResponse) context;
                    di.execute(uri.toString(), String.valueOf(index));
                }
            });

        }
    }

    public void setImageSwitcher() {
        if(loadedImages == home.getNumImages() && home.getNumImages() > 0) {
            iswPhotos.setImageDrawable(new BitmapDrawable(imageBitmaps.get(imageIndex)));
        }
    }

    @Override
    public void downloadImageFinish(Bitmap bitmap, int index) {
        imageBitmaps.set(index, bitmap);
        loadedImages++;
        setImageSwitcher();
        if(loadedImages==home.getNumImages())
            progressBar.setVisibility(View.GONE);
    }

}
