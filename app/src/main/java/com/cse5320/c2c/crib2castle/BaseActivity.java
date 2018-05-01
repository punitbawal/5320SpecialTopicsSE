package com.cse5320.c2c.crib2castle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ivan on 3/6/18.
 */

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cse5320.c2c.crib2castle.R.layout.activity_search);
    }

    public void loadActionBar(String uId) {

        final String userId = uId;
        boolean asGuest = uId.equals("-1");
        Toolbar toolbar = (Toolbar) findViewById(com.cse5320.c2c.crib2castle.R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(com.cse5320.c2c.crib2castle.R.drawable.ic_menu);

        mDrawerLayout = findViewById(com.cse5320.c2c.crib2castle.R.id.drawer_layout);
        NavigationView navigationView = findViewById(com.cse5320.c2c.crib2castle.R.id.nav_view);
        Menu menu = navigationView.getMenu();


        if(asGuest) {

            menu.findItem(com.cse5320.c2c.crib2castle.R.id.mItmLogout).setVisible(false);
            menu.findItem(com.cse5320.c2c.crib2castle.R.id.mItmFavorites).setVisible(false);
            menu.findItem(com.cse5320.c2c.crib2castle.R.id.mItmNewListing).setVisible(false);
            menu.findItem(com.cse5320.c2c.crib2castle.R.id.mItmEditListing).setVisible(false);


        } else {
            menu.findItem(com.cse5320.c2c.crib2castle.R.id.mItmLogin).setVisible(false);

        }


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Intent i;

                        switch(menuItem.getItemId()) {
                            case com.cse5320.c2c.crib2castle.R.id.mItmSearch:
                                i = new Intent(BaseActivity.this, SearchActivity.class);
                                i.putExtra("uId", userId);
                                startActivity(i);
                                break;
                            case com.cse5320.c2c.crib2castle.R.id.mItmLogin: case com.cse5320.c2c.crib2castle.R.id.mItmLogout:
                                FirebaseAuth.getInstance().signOut();
                                i = new Intent(BaseActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                break;
                            case com.cse5320.c2c.crib2castle.R.id.mItmFavorites:
                                i = new Intent(BaseActivity.this, FavoritesActivity.class);
                                i.putExtra("uId", userId);
                                startActivity(i);
                                break;
                            case com.cse5320.c2c.crib2castle.R.id.mItmNewListing:
                                i = new Intent(BaseActivity.this, NewListingAddressActivity.class);
                                i.putExtra("uId", userId);
                                startActivity(i);
                                break;
                            case com.cse5320.c2c.crib2castle.R.id.mItmEditListing:
                                i = new Intent(BaseActivity.this, ViewListingsActivity.class);
                                i.putExtra("uId", userId);
                                startActivity(i);
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
