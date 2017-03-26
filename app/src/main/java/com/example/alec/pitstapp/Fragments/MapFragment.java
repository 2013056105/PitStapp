package com.example.alec.pitstapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.alec.pitstapp.Adapters.GasStation;
import com.example.alec.pitstapp.Adapters.GasStationAdapter;
import com.example.alec.pitstapp.R;
import com.example.alec.pitstapp.SearchModules.GetNearbyPlacesData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ViewGroup rootView;
    MapView mapView;
    double latitude;
    double longitude;
    private RecyclerView recyclerViewNearby;
    private GasStationAdapter adapter;
    private Spinner stationFilter;
    private ImageView ivAnchor;
    private GoogleMap mMap;
    private Button searchButton;
    Marker gasStationMarker[];

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String bufferCatcher = "";

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        checkVersionAndGooglePlayServices();
        initializeViews(rootView);
        configureMap(savedInstanceState);
        searchGasStation();
        //configureSlidingPanel();
        return rootView;
    }

    public void searchGasStation() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gasStationSearch();
            }
        });
    }

    public void checkVersionAndGooglePlayServices() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            getActivity().finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result, 0).show();
            }
            return false;
        }
        return true;
    }

    public void initializeViews(ViewGroup rootView) {
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        recyclerViewNearby = (RecyclerView)rootView.findViewById(R.id.recyclerView_results);
        searchButton = (Button) rootView.findViewById(R.id.searchButton);
        stationFilter = (Spinner) rootView.findViewById(R.id.stationFilter);
        //ivAnchor = (ImageView) rootView.findViewById(R.id.nearby_anchor);
        //mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
    }

    public void configureMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isLocationEnabled = false;
        try {
            if (isConnectedToNetwork(getContext()) == true) {
                if (isLocationServiceEnabled() == false) {
                    Toast.makeText(getActivity().getApplicationContext(),"No location", Toast.LENGTH_LONG).show();
                }
                else {
                    isLocationEnabled = true;
                }
            }
            if (isConnectedToNetwork(getContext()) == false && isLocationServiceEnabled() == false) {
                Toast.makeText(getActivity().getApplicationContext(),"No internet connection and location service.", Toast.LENGTH_LONG).show();
            }
        }
        catch (NullPointerException e){
            Toast.makeText(getActivity().getApplicationContext(),"No location211", Toast.LENGTH_LONG).show();
        }

    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isLocationServiceEnabled() {
        try {
            LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (NullPointerException e) {

        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.custom_map)));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        float zoomLevel = (float) 16.0;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        if (isLocationServiceEnabled()){
            if (isConnectedToNetwork(getContext()) == false){
                Toast.makeText(getActivity().getApplicationContext(),"No internet connection", Toast.LENGTH_LONG).show();
            }
        }

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void gasStationSearch() {
        String gasStation = "gas_station";
        try {
            mMap.clear();
        }
        catch (NullPointerException e){

        }
        ArrayList<String> url = new ArrayList<String>();
        String urlHolder = getUrl(latitude, longitude, gasStation);
        url.add(urlHolder);
        new JSONTask().execute(url);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&rankby=distance");
        googlePlacesUrl.append("&type=" + nearbyPlace);
        //googlePlacesUrl.append("&keyword=shell");
        googlePlacesUrl.append("&key=" + "AIzaSyCX9NxVldFGpRqa4Vh7vSSRacMRpgxAhVs");
        //AIzaSyCpxm09Wptnl10RwXdzV9FYxuiwXwCoO5E -- RYAN
        //AIzaSyCX9NxVldFGpRqa4Vh7vSSRacMRpgxAhVs -- NAS
        return (googlePlacesUrl.toString());
    }

    public String determineStationChosen() {
        String chosenStation = stationFilter.getSelectedItem().toString();
        String finalChosenStation = "";

        if(chosenStation.toLowerCase().equals("all")) {
            finalChosenStation = "all";
        } else if(chosenStation.toLowerCase().equals("shell")) {
            finalChosenStation = "shell";
        } else if(chosenStation.toLowerCase().equals("petron")) {
            finalChosenStation = "petron";
        } else if(chosenStation.toLowerCase().equals("caltex")) {
            finalChosenStation = "caltex";
        }

        return finalChosenStation;
    }

    public class JSONTask extends AsyncTask<ArrayList<String>, String, ArrayList<String>>{
        int counter = 0;
        int secondCounter = 0;
        String gasStationName = "";
        String gasStationVicinity = "";
        String gasStationPlaceID = "";
        String gasStationLatitude = "";
        String gasStationLongitude = "";
        GasStation gasStationClass = new GasStation(gasStationName, gasStationVicinity, gasStationPlaceID, gasStationLatitude, gasStationLongitude);
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {
            HttpURLConnection connection = null;
            JSONObject json;
            BufferedReader reader = null;

            try{
                URL url2 = new URL(params[0].get(0));
                connection = (HttpURLConnection) url2.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();
                ArrayList<String> JSONList = new ArrayList<>();
                JSONList.add(finalJSON);
                ArrayList<String> gasStationNamesList = new ArrayList<>();
                ArrayList<String> gasStationVicinitiesList = new ArrayList<>();
                ArrayList<String> gasStationPlaceIDList = new ArrayList<>();
                ArrayList<String> gasStationLatitudeList = new ArrayList<>();
                ArrayList<String> gasStationLongitudeList = new ArrayList<>();


                JSONObject parentObject = new JSONObject(finalJSON);
                JSONArray parentArray = parentObject.getJSONArray("results");

                int i = 0;
                String finalChosenStation = determineStationChosen();

                counter = parentArray.length();

                while (i != counter){
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    String gasStationName = finalObject.getString("name");

                    if(finalChosenStation.equals("all")) {
                        gasStationNamesList.add(gasStationName);

                        String gasStationVicinity = finalObject.getString("vicinity");
                        gasStationVicinitiesList.add(gasStationVicinity);

                        String gasStationPlaceID = finalObject.getString("place_id");
                        gasStationPlaceIDList.add(gasStationPlaceID);

                        JSONObject gasStationLatitude1 = finalObject.getJSONObject("geometry");
                        JSONObject gasStationLatitude2 = gasStationLatitude1.getJSONObject("location");
                        gasStationLatitude = gasStationLatitude2.getString("lat");
                        gasStationLatitudeList.add(gasStationLatitude);

                        JSONObject gasStationLongitude1 = finalObject.getJSONObject("geometry");
                        JSONObject gasStationLongitude2 = gasStationLongitude1.getJSONObject("location");
                        gasStationLongitude = gasStationLatitude2.getString("lng");
                        gasStationLongitudeList.add(gasStationLongitude);

                        i++;
                    } else if(finalChosenStation.equals("shell")) {
                        if(gasStationName.toLowerCase().contains("shell")) {
                            gasStationNamesList.add(gasStationName);

                            String gasStationVicinity = finalObject.getString("vicinity");
                            gasStationVicinitiesList.add(gasStationVicinity);

                            String gasStationPlaceID = finalObject.getString("place_id");
                            gasStationPlaceIDList.add(gasStationPlaceID);

                            JSONObject gasStationLatitude1 = finalObject.getJSONObject("geometry");
                            JSONObject gasStationLatitude2 = gasStationLatitude1.getJSONObject("location");
                            gasStationLatitude = gasStationLatitude2.getString("lat");
                            gasStationLatitudeList.add(gasStationLatitude);

                            JSONObject gasStationLongitude1 = finalObject.getJSONObject("geometry");
                            JSONObject gasStationLongitude2 = gasStationLongitude1.getJSONObject("location");
                            gasStationLongitude = gasStationLatitude2.getString("lng");
                            gasStationLongitudeList.add(gasStationLongitude);

                            i++;
                            secondCounter++;
                        } else {
                            i++;
                        }
                    } else if(finalChosenStation.equals("petron")) {
                        if(gasStationName.toLowerCase().contains("petron")) {
                            gasStationNamesList.add(gasStationName);

                            String gasStationVicinity = finalObject.getString("vicinity");
                            gasStationVicinitiesList.add(gasStationVicinity);

                            String gasStationPlaceID = finalObject.getString("place_id");
                            gasStationPlaceIDList.add(gasStationPlaceID);

                            JSONObject gasStationLatitude1 = finalObject.getJSONObject("geometry");
                            JSONObject gasStationLatitude2 = gasStationLatitude1.getJSONObject("location");
                            gasStationLatitude = gasStationLatitude2.getString("lat");
                            gasStationLatitudeList.add(gasStationLatitude);

                            JSONObject gasStationLongitude1 = finalObject.getJSONObject("geometry");
                            JSONObject gasStationLongitude2 = gasStationLongitude1.getJSONObject("location");
                            gasStationLongitude = gasStationLatitude2.getString("lng");
                            gasStationLongitudeList.add(gasStationLongitude);

                            i++;
                            secondCounter++;
                        } else {
                            i++;
                        }
                    } else if(finalChosenStation.equals("caltex")) {
                        if(gasStationName.toLowerCase().contains("caltex")) {
                            gasStationNamesList.add(gasStationName);

                            String gasStationVicinity = finalObject.getString("vicinity");
                            gasStationVicinitiesList.add(gasStationVicinity);

                            String gasStationPlaceID = finalObject.getString("place_id");
                            gasStationPlaceIDList.add(gasStationPlaceID);

                            JSONObject gasStationLatitude1 = finalObject.getJSONObject("geometry");
                            JSONObject gasStationLatitude2 = gasStationLatitude1.getJSONObject("location");
                            gasStationLatitude = gasStationLatitude2.getString("lat");
                            gasStationLatitudeList.add(gasStationLatitude);

                            JSONObject gasStationLongitude1 = finalObject.getJSONObject("geometry");
                            JSONObject gasStationLongitude2 = gasStationLongitude1.getJSONObject("location");
                            gasStationLongitude = gasStationLatitude2.getString("lng");
                            gasStationLongitudeList.add(gasStationLongitude);

                            i++;
                            secondCounter++;
                        } else {
                            i++;
                        }
                    }
                }

                gasStationClass.putGasStationInformationList(gasStationNamesList, gasStationVicinitiesList, gasStationPlaceIDList,
                        gasStationLatitudeList, gasStationLongitudeList);
                return JSONList;
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            finally{
                if (connection != null){
                    connection.disconnect();
                }
                try {
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //return null;
            return null;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected void onPostExecute(ArrayList<String> testingArray) {
            //super.onPostExecute();
            //Log.d("DALIRI", "DALIRI MO");
            Object[] DataTransfer = new Object[2];
            DataTransfer[0] = mMap;
            DataTransfer[1] = testingArray.get(0).toString();
            //Log.d("onClick", url);
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);

            List<GasStation> data = new ArrayList<>();
            int i = 0;
            ArrayList<String> gasStationNameList = gasStationClass.getGasStationNameList();
            ArrayList<String> gasStationVicinityList = gasStationClass.getGasStationVicinityList();
            ArrayList<String> gasStationPlaceIDList = gasStationClass.getGasStationPlaceIDList();
            ArrayList<String> gasStationLatitudeList = gasStationClass.getGasStationLatitudeList();
            ArrayList<String> gasStationLongitudeList = gasStationClass.getGasStationLongitudeList();

//            while (i != counter){
//
//                String gasStationName = gasStationNameList.get(i);
//                String gasStationVicinity = gasStationVicinityList.get(i);
//                String gasStationPlaceID = gasStationPlaceIDList.get(i);
//                String latitude = gasStationLatitudeList.get(i);
//                String longitude = gasStationLongitudeList.get(i);
//
//                //Toast.makeText(getActivity().getApplicationContext(), latitude, Toast.LENGTH_LONG).show();
//
//                GasStation nGasStation = new GasStation(gasStationName, gasStationVicinity, gasStationPlaceID, latitude, longitude);
//                data.add(nGasStation);
//
//                i++;
//            }

            if(determineStationChosen().equals("all")) {
                for(int ctr = 0; ctr < counter; ctr++) {
                    String gasStationName = gasStationNameList.get(ctr);
                    String gasStationVicinity = gasStationVicinityList.get(ctr);
                    String gasStationPlaceID = gasStationPlaceIDList.get(ctr);
                    String latitude = gasStationLatitudeList.get(ctr);
                    String longitude = gasStationLongitudeList.get(ctr);

                    //Toast.makeText(getActivity().getApplicationContext(), latitude, Toast.LENGTH_LONG).show();

                    GasStation nGasStation = new GasStation(gasStationName, gasStationVicinity, gasStationPlaceID, latitude, longitude);
                    data.add(nGasStation);
                }
            } else {
                for(int ctr = 0; ctr < secondCounter; ctr++) {
                    String gasStationName = gasStationNameList.get(ctr);
                    String gasStationVicinity = gasStationVicinityList.get(ctr);
                    String gasStationPlaceID = gasStationPlaceIDList.get(ctr);
                    String latitude = gasStationLatitudeList.get(ctr);
                    String longitude = gasStationLongitudeList.get(ctr);

                    //Toast.makeText(getActivity().getApplicationContext(), latitude, Toast.LENGTH_LONG).show();

                    GasStation nGasStation = new GasStation(gasStationName, gasStationVicinity, gasStationPlaceID, latitude, longitude);
                    data.add(nGasStation);
                }
            }


            adapter = new GasStationAdapter(getActivity(), data);
            recyclerViewNearby.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            recyclerViewNearby.setItemAnimator(new DefaultItemAnimator());
            recyclerViewNearby.setAdapter(adapter);
            //tvData.setText(result);
            progressDialog.dismiss();
        }
    }

    public void catchBufferString(String holder){
        bufferCatcher = holder;
        //Log.e("HELLO", bufferCatcher);
    }
}
