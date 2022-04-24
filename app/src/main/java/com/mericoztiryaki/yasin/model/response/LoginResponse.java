package com.mericoztiryaki.yasin.model.response;

import com.mericoztiryaki.yasin.UserType;

/**
 * Created by as on 2019-05-07.
 */

public class LoginResponse {

    private int id;
    private String username;
    private UserType userType;

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

}
