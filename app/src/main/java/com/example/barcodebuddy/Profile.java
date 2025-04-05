package com.example.barcodebuddy;

import com.google.firebase.auth.FirebaseUser;

public class Profile {
    String name ,email,password,profileimageid,type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfileimageid() {
        return profileimageid;
    }

    public void setProfileimageid(String profileimageid) {
        this.profileimageid = profileimageid;
    }



    public Profile(String name , String email,String type){
        this.name=name;
        this.email=email;
        this.type=type;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Profile(FirebaseUser currentUser){

    }
}
