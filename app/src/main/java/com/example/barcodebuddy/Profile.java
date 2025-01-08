package com.example.barcodebuddy;

public class Profile {
    String name ,email,password;
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Profile(String name, String email, String type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public Profile(String name , String email){
        this.name=name;
        this.email=email;
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
    public Profile(){

    }
}
