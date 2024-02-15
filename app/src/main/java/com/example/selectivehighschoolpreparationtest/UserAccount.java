package com.example.selectivehighschoolpreparationtest;

import java.io.Serializable;

// UserAccount class used to store information on a user
// currently only used to store information of the currently logged in user
public class UserAccount implements Serializable {

    private String userName, password, emailAddress;

    UserAccount(String uName, String pWord, String email){
        userName = uName;
        password = pWord;
        emailAddress = email;
    }

    // default constructor
    UserAccount(){
        userName = "";
        password = "";
        emailAddress = "";
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
