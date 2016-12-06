package com.example.phompang.eatyfinder.model;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by phompang on 11/6/2016 AD.
 */
@IgnoreExtraProperties
public class User implements Serializable {
    private String uid;
    private String displayName;
    private String email;
    private String provide;
    private String photo;
    private String token;
    private int people;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvide() {
        return provide;
    }

    public void setProvide(String provide) {
        this.provide = provide;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
