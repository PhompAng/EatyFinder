package com.example.phompang.eatyfinder.model;

import java.io.Serializable;

/**
 * Created by phompang on 12/5/2016 AD.
 */

public class Comment implements Serializable {
    private User user;
    private String comment;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
