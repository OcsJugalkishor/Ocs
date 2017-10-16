package com.dharmshala.marulohar.model;

/**
 * Created by user on 10/5/2017.
 */

public class newUser {
    public String uid;
    public String name;
    public String email;
    public String firebaseToken;

    public newUser(){

    }

    public newUser(String uid, String email, String firebaseToken){
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
