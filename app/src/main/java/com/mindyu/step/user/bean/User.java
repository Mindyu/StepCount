package com.mindyu.step.user.bean;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class User extends LitePalSupport implements Serializable{

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
