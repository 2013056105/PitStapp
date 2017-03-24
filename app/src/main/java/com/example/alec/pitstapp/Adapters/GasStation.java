package com.example.alec.pitstapp.Adapters;

import java.util.ArrayList;

public class GasStation {
    private String gasStationName;
    private String gasStationVicinity;
    private String gasStationPlaceID;
    private String gasStationLatitude;
    private String gasStationLongitude;

    private ArrayList<String> gasStationNameList;
    private ArrayList<String> gasStationVicinityList;
    private ArrayList<String> gasStationPlaceIDList;
    private ArrayList<String> gasStationLatitudeList;
    private ArrayList<String> gasStationLongitudeList;



    public GasStation(String gasStationName, String gasStationVicinity, String gasStationPlaceID, String gasStationLatitude, String gasStationLongitude){
        this.setGasStationName(gasStationName);
        this.setGasStationVicinity(gasStationVicinity);
        this.setGasStationPlaceID(gasStationPlaceID);
        this.setGasStationLatitude(gasStationLatitude);
        this.setGasStationLongitude(gasStationLongitude);
    }

    public void putGasStationInformationList(ArrayList<String> gasStationNameList, ArrayList<String> gasStationVicinityList,
                                           ArrayList<String> gasStationPlaceIDList, ArrayList<String> gasStationLatitudeList,
                                           ArrayList<String> gasStationLongitudeList){
        this.setGasStationNameList(gasStationNameList);
        this.setGasStationVicinityList(gasStationVicinityList);
        this.setGasStationPlaceIDList(gasStationPlaceIDList);
        this.setGasStationLatitudeList(gasStationLatitudeList);
        this.setGasStationLongitudeList(gasStationLongitudeList);
    }

    public void setGasStationName(String gasStationName) {
        this.gasStationName = gasStationName;
    }

    public void setGasStationVicinity(String gasStationVicinity) {
        this.gasStationVicinity = gasStationVicinity;
    }

    public void setGasStationPlaceID(String gasStationPlaceID) {
        this.gasStationPlaceID = gasStationPlaceID;
    }

    public void setGasStationLatitude(String gasStationLatitude) {
        this.gasStationLatitude = gasStationLatitude;
    }

    public void setGasStationLongitude(String gasStationLongitude) {
        this.gasStationLongitude = gasStationLongitude;
    }

    public void setGasStationNameList(ArrayList<String> gasStationNameList) {
        this.gasStationNameList = gasStationNameList;
    }

    public void setGasStationVicinityList(ArrayList<String> gasStationVicinityList) {
        this.gasStationVicinityList = gasStationVicinityList;
    }

    public void setGasStationPlaceIDList(ArrayList<String> gasStationPlaceID) {
        this.gasStationPlaceIDList = gasStationPlaceID;
    }

    public void setGasStationLatitudeList(ArrayList<String> gasStationLatitudeList) {
        this.gasStationLatitudeList = gasStationLatitudeList;
    }

    public void setGasStationLongitudeList(ArrayList<String> gasStationLongitudeList) {
        this.gasStationLongitudeList = gasStationLongitudeList;
    }

    public String getGasStationName() {
        return gasStationName;
    }

    public String getGasStationVicinity() {
        return gasStationVicinity;
    }

    public String getGasStationPlaceID() {
        return gasStationPlaceID;
    }

    public String getGasStationLatitude() {
        return gasStationLatitude;
    }

    public String getGasStationLongitude() {
        return gasStationLongitude;
    }

    public ArrayList<String> getGasStationNameList() {
        return gasStationNameList;
    }

    public ArrayList<String> getGasStationVicinityList() {
        return gasStationVicinityList;
    }

    public ArrayList<String> getGasStationPlaceIDList() {
        return gasStationPlaceIDList;
    }

    public ArrayList<String> getGasStationLatitudeList() {
        return gasStationLatitudeList;
    }

    public ArrayList<String> getGasStationLongitudeList() {
        return gasStationLongitudeList;
    }
}
