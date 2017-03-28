package com.example.alec.pitstapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alec.pitstapp.Fragments.AboutFragment;
import com.example.alec.pitstapp.Fragments.MapFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        checkPermission();

        moveToMapFragment();
    }

    public void moveToMapFragment() {
        MapFragment mapFragment = new MapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(mapFragment, "mapFragment")
                .replace(R.id.fragment_container, mapFragment)
                .commit();
    }

    public void checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 200);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            default:
                checkPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click

        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.action_about){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            AboutFragment aboutFragment = new AboutFragment();
            fragmentTransaction.add(aboutFragment, "about")
                    .replace(R.id.fragment_container, aboutFragment)
                    .addToBackStack("about")
                    .commit();
        }
        return true;

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle("");
        getSupportActionBar().setTitle(title);
    }
}