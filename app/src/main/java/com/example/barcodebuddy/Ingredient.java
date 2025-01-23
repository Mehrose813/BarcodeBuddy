package com.example.barcodebuddy;

public class Ingredient {
    String name;
    String id;
    String qty;
    String healthy;
    String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Ingredient() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getHealthy() {
        return healthy;
    }

    public void setHealthy(String healthy) {
        this.healthy = healthy;
    }
}
