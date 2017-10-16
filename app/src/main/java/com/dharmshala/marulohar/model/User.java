package com.dharmshala.marulohar.model;

/**
 * Created by user on 10/5/2017.
 */

public class User {
    public String firstName;
    public String lastName;
    public String email;
    public String passWord;
    public String confirmPassword;
    public String address;
    public String imageURL;

    public User() {
    }

    public User(String firstName, String email, String imageURL) {
        this.firstName = firstName;
        this.email = email;
        this.imageURL = imageURL;
    }

    public User(String firstName, String lastName, String email, String address, String passWord, String confirmPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.passWord = passWord;
        this.confirmPassword = confirmPassword;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
