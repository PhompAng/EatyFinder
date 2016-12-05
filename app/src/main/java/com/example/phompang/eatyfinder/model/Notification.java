package com.example.phompang.eatyfinder.model;

import java.io.Serializable;

/**
 * Created by phompang on 12/5/2016 AD.
 */

public class Notification implements Serializable {
    private String to;
    private NotificationBody notification;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationBody getNotification() {
        return notification;
    }

    public void setNotification(NotificationBody notification) {
        this.notification = notification;
    }
}
