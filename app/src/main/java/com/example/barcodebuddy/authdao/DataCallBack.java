package com.example.barcodebuddy.authdao;

public interface DataCallBack<T> {
    public void onSuccess(T data);
    public void onError(String msg);

}


