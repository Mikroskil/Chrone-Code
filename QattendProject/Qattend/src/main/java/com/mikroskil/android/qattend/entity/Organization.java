package com.mikroskil.android.qattend.entity;

public class Organization {

    private String email;
    private String password;

    public Organization(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
