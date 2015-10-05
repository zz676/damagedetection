package com.aig.science.damagedetection.models;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhizhou on 10/27/2014.
 */
public class Viehcle {
    private String brand;
    private String model;
    private String year;
    private Color color;

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getYear() {
        return year;
    }

    private Date producedDate;
    private Date purchasedDate;
    private List<String> photosPathList = new ArrayList<String>();

    public Viehcle(String brand, String model, String year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Date getProducedDate() {
        return producedDate;
    }

    public void setProducedDate(Date producedDate) {
        this.producedDate = producedDate;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(Date purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public List<String> getPhotosPathList() {
        return photosPathList;
    }

    public void setPhotosPathList(List<String> photosPathList) {
        this.photosPathList = photosPathList;
    }
}
