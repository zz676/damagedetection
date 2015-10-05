package com.aig.science.damagedetection.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 * Created by zhizhou on 10/30/2014.
 */
public class Place implements Comparable<Place> {
    private String sID;
    private String sName;
    private String sPhoneNumber;
    private String sVicinity;
    private String sReference;
    private LatLng latLng;
    private String sIcon;
    // the distance between the current location and the shop
    private double distance = 0;

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getsPhoneNumber() {
        return sPhoneNumber;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public static Place jsonToPlaceObject(LatLng currentLocation, JSONObject placeObject) {
        DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");

        try {
            Place result = new Place();
            JSONObject geometry = (JSONObject) placeObject.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatLng(new LatLng((Double) location.get("lat"), (Double) location.get("lng")));
            result.setDistance(Double.valueOf(DECIMALFORMAT.format(computeDistanceBetween(currentLocation, result.getLatLng()) * 0.00062137)));
            result.setsName(placeObject.getString("name"));
            result.setsVicinity(placeObject.getString("vicinity"));

            //Notice: not id
            result.setsID(placeObject.getString("place_id"));
            result.setsReference("reference");
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * get details of a place object
     * @param placeObject
     * @return
     */
    public static Place jsonToPlaceDetailObject(JSONObject placeObject) {
        try {
            Place result = new Place();
            result.setsPhoneNumber(placeObject.getString("international_phone_number"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + sID + ", icon=" + sIcon + ", name=" + sName + ", latitude=" + latLng.latitude + ", longitude=" + latLng.longitude + '}';
    }

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public String getsReference() {
        return sReference;
    }

    public void setsReference(String sReference) {
        this.sReference = sReference;
    }

    public String getsVicinity() {
        return sVicinity;
    }

    public void setsVicinity(String sVicinity) {
        this.sVicinity = sVicinity;
    }

    public String getsIcon() {
        return sIcon;
    }

    public void setsIcon(String sIcon) {
        this.sIcon = sIcon;
    }

    @Override
    public int compareTo(Place place) {
        return Double.compare(this.distance, place.getDistance());
    }

    public void setsPhoneNumber(String sPhoneNumber) {
        this.sPhoneNumber = sPhoneNumber;
    }
}