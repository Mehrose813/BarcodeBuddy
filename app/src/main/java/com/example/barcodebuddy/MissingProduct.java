package com.example.barcodebuddy;

public class MissingProduct {
    private String barcode;
    private String userEmail;
    private String userId;
    String date,time;


    public  MissingProduct(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public MissingProduct(String barcode, String userEmail , String userId , String date , String time) {
        this.barcode = barcode;
        this.userEmail = userEmail;
        this.userId = userId;
        this.date = date;
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
