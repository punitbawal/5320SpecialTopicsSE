package com.example.ivan.crib2castle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HomeDetailsActivity extends BaseActivity implements DownloadImageResponse {

    String uId;
    Home home;
    ArrayList<Bitmap> imageBitmaps;
    int loadedImages;
    ImageSwitcher iswPhotos;
    int imageIndex;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_details);
        uId = getIntent().getStringExtra("uId");
        home = (Home) getIntent().getSerializableExtra("home");
        loadedImages=0;
        imageIndex = 0;
        imageBitmaps = new ArrayList<>();
        context = HomeDetailsActivity.this;
        for(int i=0;i<home.getNumImages();i++) {
            imageBitmaps.add(null);
        }

        loadActionBar(uId);
        loadWidgets();
        loadImages();

    }

    public void loadWidgets() {
        iswPhotos = (ImageSwitcher) findViewById(R.id.iswPhotos);
        TextView tvPrev = (TextView) findViewById(R.id.tvPrev);
        TextView tvNext = (TextView) findViewById(R.id.tvNext);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        TextView tvYear = (TextView) findViewById(R.id.tvYear);
        TextView tvSqft = (TextView) findViewById(R.id.tvSqft);
        TextView tvBedBaths = (TextView) findViewById(R.id.tvBedBaths);
        TextView tvDetails = (TextView) findViewById(R.id.tvDetails);
        final ImageView ivFullscreen = (ImageView) findViewById(R.id.ivFullscreen);

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
    }

    public void loadImages() {

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
    }

}
