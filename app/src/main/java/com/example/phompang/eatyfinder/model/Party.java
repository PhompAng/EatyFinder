package com.example.phompang.eatyfinder.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by phompang on 10/25/2016 AD.
 */
@IgnoreExtraProperties
public class Party {
    private String title;
    private double price;
    private int Photo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPhoto() {
        return Photo;
    }

    public void setPhoto(int photo) {
        Photo = photo;
    }
}
