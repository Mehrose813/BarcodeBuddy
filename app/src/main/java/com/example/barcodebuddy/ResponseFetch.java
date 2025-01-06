package com.example.barcodebuddy;

public interface ResponseFetch {
    public void onSuccess(Profile profile);
    public void onError(String msg);
}
