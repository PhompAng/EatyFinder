package com.example.phompang.eatyfinder.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fi.foyt.foursquare.api.entities.Category;

/**
 * Created by phompang on 10/25/2016 AD.
 */
@IgnoreExtraProperties
public class Party implements Serializable {
    private String title;
    private String desc;
    private String date;
    private String time;
    private int currentPeople;
    private int requiredPeople;
    private double price;
    private double pricePerPerson;
    private String location;
    private String photo;
    private String owner;
    private Map<String, User> attendees;
    private Category category;

    public Party() {
        this.attendees = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCurrentPeople() {
        return currentPeople;
    }

    public void setCurrentPeople(int currentPeople) {
        this.currentPeople = currentPeople;
    }

    public int getRequiredPeople() {
        return requiredPeople;
    }

    public void setRequiredPeople(int requiredPeople) {
        this.requiredPeople = requiredPeople;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(double pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void addAttendees(String key, User u) {
        this.getAttendees().put(key, u);
    }

    public Map<String, User> getAttendees() {
        return attendees;
    }

    public void setAttendees(Map<String, User> attendees) {
        this.attendees = attendees;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
