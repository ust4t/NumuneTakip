package com.mericoztiryaki.yasin.model;

import com.mericoztiryaki.yasin.UserType;

public class LocalUser {
    private int id;
    private String username;
    private UserType userType;

    public LocalUser(int id, String username, UserType userType) {
        this.username = username;
        this.userType = userType;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserType getUserType() {
        return userType;
    }
}
