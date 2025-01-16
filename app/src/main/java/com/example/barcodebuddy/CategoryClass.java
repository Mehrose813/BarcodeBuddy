package com.example.barcodebuddy;

public class CategoryClass {
    String id,catname;

    public CategoryClass(String id, String catname) {
        this.id = id;
        this.catname = catname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }
}
