package com.example.barcodebuddy;

public class Reply {
    private String userEmail;
    private String barcode;
    private String replyMessage;

    // Empty constructor for Firebase
    public Reply() {
    }

    public Reply(String userEmail, String barcode, String replyMessage) {
        this.userEmail = userEmail;
        this.barcode = barcode;
        this.replyMessage = replyMessage;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getReplyMessage() {
        return replyMessage;
    }
}
