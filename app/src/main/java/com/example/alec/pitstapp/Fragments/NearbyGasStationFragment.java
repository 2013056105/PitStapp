package com.example.alec.pitstapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alec.pitstapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyGasStationFragment extends Fragment {


    public NearbyGasStationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_routes, container, false);

        return rootView;
    }

}
