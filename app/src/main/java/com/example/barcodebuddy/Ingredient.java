package com.example.barcodebuddy;

public class Ingredient {
    String name , quantity;

    public Ingredient(String name , String quantity){
        this.name = name;
        this.quantity = quantity;
    }



    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String nameingre) {
        this.name = name;
    }

    public Ingredient(){

    }
}
