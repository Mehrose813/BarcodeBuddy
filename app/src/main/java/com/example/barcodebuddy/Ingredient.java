package com.example.barcodebuddy;

public class Ingredient {
    String nameingre , quantity;

    public Ingredient(String nameingre , String quantity){
        this.nameingre = nameingre;
        this.quantity = quantity;
    }



    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNameingre() {
        return nameingre;
    }

    public void setNameingre(String nameingre) {
        this.nameingre = nameingre;
    }

    public Ingredient(){

    }
}
