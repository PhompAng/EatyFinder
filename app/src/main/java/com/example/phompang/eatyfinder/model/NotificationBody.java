package com.example.phompang.eatyfinder.model;

import java.io.Serializable;

/**
 * Created by phompang on 12/5/2016 AD.
 */
public class NotificationBody implements Serializable {
    private String body;
    private String title;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
