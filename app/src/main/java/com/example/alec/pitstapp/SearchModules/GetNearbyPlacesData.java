package com.example.alec.pitstapp.SearchModules;

import android.os.AsyncTask;
import android.util.Log;

import com.example.alec.pitstapp.Fragments.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    StringBuffer holder;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
//            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = (String)params[1];
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();


        MapFragment nearby22 = new MapFragment();
        nearby22.catchBufferString(result);
//        nearby22.runMyJSONTask(result);
        nearbyPlacesList =  dataParser.parse(result);
        try {
            ShowNearbyPlaces(nearbyPlacesList);
        }
        catch (NullPointerException e){
            Log.d("GooglePlacesReadTask", "GetNearbyPlacesData-PostExecute NULL");
//            NearbyFragment newF = new NearbyFragment();
//            newF.automaticHospitalSearch();
            //Toast.makeText(getActivity().getApplicationContext(), latitude, Toast.LENGTH_LONG).show();

        }
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
//            Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.snippet(vicinity);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //move map camera
//            float zoomLevel = (float) 16.0; //This goes up to 21
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }
}
