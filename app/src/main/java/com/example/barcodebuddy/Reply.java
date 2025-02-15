package com.example.barcodebuddy;

public class Reply {
    private String reply;
    private long timestamp;

    // No-arg constructor required for Firebase
    public Reply() { }

    public Reply(String reply, long timestamp) {
        this.reply = reply;
        this.timestamp = timestamp;
    }

    public String getReply() {
        return reply;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
