package com.aig.science.damagedetection.models;

import java.io.Serializable;
import java.util.Date;

public class MyClaim implements Serializable  {

    private String cost;
    private String comments;
    private String make;
    private String model;
    private String color;
    private String date;
    private String time;
    private String licenseNumber;
    private String vinNumber;
    private String userId, policyId, claimId,vehicleId;
    private String policyNumber;
    private boolean isCompleted;
    private String status;
    private Date submittedTime;
    private double longitude;
    private double latitude;

    public java.lang.String getMake() {
        return make;
    }

    public void setMake(java.lang.String make) {
        this.make = make;
    }

    public java.lang.String getModel() {
        return model;
    }

    public void setModel(java.lang.String model) {
        this.model = model;
    }

    public java.lang.String getColor() {
        return color;
    }

    public void setColor(java.lang.String color) {
        this.color = color;
    }

    public java.lang.String getDate() {
        return date;
    }

    public void setDate(java.lang.String date) {
        this.date = date;
    }

    public java.lang.String getTime() {
        return time;
    }

    public void setTime(java.lang.String time) {
        this.time = time;
    }

    public java.lang.String getCost() {
        return cost;
    }

    public void setCost(java.lang.String cost) {
        this.cost = cost;
    }

    public java.lang.String getComments() {
        return comments;
    }

    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }

    public java.lang.String getUserId() {
        return userId;
    }

    public void setUserId(java.lang.String userId) {
        this.userId = userId;
    }

    public java.lang.String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(java.lang.String policyId) {
        this.policyId = policyId;
    }

    public java.lang.String getClaimId() {
        return claimId;
    }

    public void setClaimId(java.lang.String claimId) {
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

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(Date submittedTime) {
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
}
