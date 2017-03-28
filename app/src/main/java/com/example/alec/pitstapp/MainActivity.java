package com.example.alec.pitstapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

        moveToMapFragment();
    }

    public void moveToMapFragment() {
        MapFragment mapFragment = new MapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(mapFragment, "mapFragment")
                .replace(R.id.fragment_container, mapFragment)
                .commit();
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