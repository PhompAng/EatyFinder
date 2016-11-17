package com.example.phompang.eatyfinder.model;

import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteSpecial;
import fi.foyt.foursquare.api.entities.Contact;
import fi.foyt.foursquare.api.entities.HereNow;
import fi.foyt.foursquare.api.entities.Location;
import fi.foyt.foursquare.api.entities.Stats;
import fi.foyt.foursquare.api.entities.TodoGroup;
import fi.foyt.foursquare.api.entities.venue.Hours;
import fi.foyt.foursquare.api.entities.venue.Menu;
import fi.foyt.foursquare.api.entities.venue.Price;

/**
 * Created by phompang on 11/17/2016 AD.
 */

public class MyCompactVenue extends CompactVenue {
    private CompactVenue mCompactVenue;

    public MyCompactVenue(CompactVenue compactVenue) {
        this.mCompactVenue = compactVenue;
    }

    @Override
    public String getId() {
        return mCompactVenue.getId();
    }

    @Override
    public String getName() {
        return mCompactVenue.getName();
    }

    @Override
    public Contact getContact() {
        return mCompactVenue.getContact();
    }

    @Override
    public Location getLocation() {
        return mCompactVenue.getLocation();
    }

    @Override
    public Category[] getCategories() {
        return mCompactVenue.getCategories();
    }

    @Override
    public Boolean getVerified() {
        return mCompactVenue.getVerified();
    }

    @Override
    public Stats getStats() {
        return mCompactVenue.getStats();
    }

    @Override
    public String getUrl() {
        return mCompactVenue.getUrl();
    }

    @Override
    public Hours getHours() {
        return mCompactVenue.getHours();
    }

    @Override
    public Hours getPopular() {
        return mCompactVenue.getPopular();
    }

    @Override
    public Menu getMenu() {
        return mCompactVenue.getMenu();
    }

    @Override
    public Price getPrice() {
        return mCompactVenue.getPrice();
    }

    @Override
    public Integer getRating() {
        return mCompactVenue.getRating();
    }

    @Override
    public CompleteSpecial[] getSpecials() {
        return mCompactVenue.getSpecials();
    }

    @Override
    public Boolean isClosed() {
        return mCompactVenue.isClosed();
    }

    @Override
    public HereNow getHereNow() {
        return mCompactVenue.getHereNow();
    }

    @Override
    public TodoGroup getTodos() {
        return mCompactVenue.getTodos();
    }

    @Override
    public String toString() {
        return mCompactVenue.getName();
    }
}
