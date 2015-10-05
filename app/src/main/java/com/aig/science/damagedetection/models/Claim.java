package com.aig.science.damagedetection.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aig.science.damagedetection.helper.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Claim implements Parcelable,Comparable<Claim>  {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_NOW);

    private String cost;
    private String comments;
    private String make;
    private String model;
    private String color;
    private String licenseNumber;
    private String vinNumber;
    private String userId, policyId, claimId,vehicleId;
    private String policyNumber;
    private boolean isCompleted;
    private String status;
    private String submittedTime;
    private double longitude;
    private double latitude;


    public Claim(){}

    public Claim(Parcel source){
        readFromParcel(source);
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public JSONObject toInsertJSON(Claim claim){

        try{
            JSONObject object = new JSONObject();
            object.put("transaction_type", "MODIFICATION_PUSH");
            object.put("action", "INSERT");
            object.put("tablename", DatabaseHelper.TABLE_CLAIMS);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", claim.getUserId()));
            array.put(1, new JSONObject().put("column", DatabaseHelper.KEY_POLICY_ID)
                    .put("value",claim.getPolicyId()));
            array.put(2, new JSONObject().put("column", DatabaseHelper.KEY_CLAIM_ID)
                    .put("value", claim.getClaimId()));
            array.put(3, new JSONObject().put("column", DatabaseHelper.KEY_STATUS)
                    .put("value",claim.getStatus()));
            array.put(4, new JSONObject().put("column", DatabaseHelper.KEY_LATITUDE)
                    .put("value", claim.getLatitude()));
            array.put(5, new JSONObject().put("column", DatabaseHelper.KEY_LONGITUDE)
                    .put("value",claim.getLongitude()));
            array.put(6, new JSONObject().put("column", DatabaseHelper.KEY_SUBMITTED_TIME)
                    .put("value",claim.getSubmittedTime()));

            object.put("data", array);
            return object;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * query info by username
     * @param userInfo
     * @return
     */
    public static JSONObject toQueryJSON(UserInfo userInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("transaction_type", "MODIFICATION_PULL");

            object.put("action", "QUERY");
            object.put("tablename", DatabaseHelper.TABLE_CLAIMS);

            JSONArray array = new JSONArray();
            array.put(0, new JSONObject().put("column", DatabaseHelper.KEY_USER_ID)
                    .put("value", userInfo.getUserId()));

            object.put("data", array);
            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Date convertStringToDate(String dateString){

        try {
            return SIMPLE_DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(Claim claim) {
        return convertStringToDate(claim.getSubmittedTime()).compareTo(convertStringToDate(this.submittedTime));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel source){

        userId = source.readString();
        policyId = source.readString();
        claimId = source.readString();
        vehicleId = source.readString();
        cost = source.readString();
        comments = source.readString();
        make = source.readString();
        model = source.readString();
        color = source.readString();
        licenseNumber = source.readString();
        vinNumber = source.readString();
        policyNumber = source.readString();
        //isCompleted = source.readBoolean();
        status = source.readString();
        submittedTime = source.readString();
        longitude = source.readDouble();
        latitude = source.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(policyId);
        dest.writeString(claimId);
        dest.writeString(vehicleId);
        dest.writeString(cost);
        dest.writeString(comments);
        dest.writeString(make);
        dest.writeString(model);
        dest.writeString(color);
        dest.writeString(licenseNumber);
        dest.writeString(vinNumber);
        dest.writeString(policyNumber);
        //dest.writeBoolean(isCompleted);
        dest.writeString(status);
        dest.writeString(submittedTime);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public PolicyInfo createFromParcel(Parcel source) {
            return new PolicyInfo(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }
    };
}
