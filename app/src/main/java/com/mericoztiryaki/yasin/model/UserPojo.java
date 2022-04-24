package com.mericoztiryaki.yasin.model;

import com.google.gson.annotations.SerializedName;

public class UserPojo {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("userType")
    private String userType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
