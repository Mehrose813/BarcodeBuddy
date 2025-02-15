package com.example.barcodebuddy;

public class MissingProduct {
    private String barcode;
    private String userEmail;
    private String userId;


    public  MissingProduct(){

    }
    public MissingProduct(String barcode, String userEmail , String userId) {
        this.barcode = barcode;
        this.userEmail = userEmail;
        this.userId = userId;
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
