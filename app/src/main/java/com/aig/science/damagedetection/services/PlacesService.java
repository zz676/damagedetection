package com.aig.science.damagedetection.services;

import android.util.Log;

import com.aig.science.damagedetection.models.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create request for Places API.
 *
 * @author Zhisheng Zhou
 * @Date 11/4/2014
 */
public class PlacesService {

    private final static double SEARCHRADIUS = 40233.6;
    private String API_KEY;

    public PlacesService(String apikey) {
        this.API_KEY = apikey;
    }

    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }

    public ArrayList<Place> findPlaces(LatLng location,
                                       String placeSpacification) {

        String urlString = makeUrl(location.latitude, location.longitude, placeSpacification);

        try {
            String json = getJSON(urlString);

            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToPlaceObject(location, (JSONObject) array.get(i));
                    place.setsPhoneNumber(getPhoneNumber(place.getsID()));
                    Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    public String getPhoneNumber(String placeID) {

        StringBuilder urlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?placeid=");
        urlBuilder.append(placeID);
        urlBuilder.append("&key=" +API_KEY);;

        try {
            String json = getJSON(urlBuilder.toString());
            System.out.println(json);
            JSONObject object = new JSONObject(json);

            JSONObject result = object.getJSONObject("result");
            String phoneNumber = result.getString("formatted_phone_number");
            Log.v("Places Services ", "Phone Number: " + phoneNumber);
            return phoneNumber;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    // https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String makeUrl(double latitude, double longitude, String place) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");
        if (place.equals("")) {
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=" + String.valueOf(SEARCHRADIUS));
            // urlString.append("&types="+place);
            //urlString.append("&sensor=false&key=" + API_KEY);
            urlString.append("&key=" + API_KEY);
        } else {
            urlString.append("keyword=" + place);
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=" + String.valueOf(SEARCHRADIUS));
            // urlString.append("rankby=distance");
            urlString.append("&sensor=true&key=" + API_KEY);
        }
        return urlString.toString();
    }

    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(theUrl);
            //URLConnection urlConection = url.openConnection();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}