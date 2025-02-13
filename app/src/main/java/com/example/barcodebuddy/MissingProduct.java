package com.example.barcodebuddy;

public class MissingProduct {
    private String barcode;
    private String userEmail;


    public  MissingProduct(){

    }
    public MissingProduct(String barcode, String userEmail) {
        this.barcode = barcode;
        this.userEmail = userEmail;
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
