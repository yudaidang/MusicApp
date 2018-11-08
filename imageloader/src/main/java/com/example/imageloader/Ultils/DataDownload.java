package com.example.imageloader.Ultils;

public class DataDownload {
    private String mUrl;
    private byte[] bytes;

    public DataDownload(String mUrl, byte[] bytes) {
        this.mUrl = mUrl;
        this.bytes = bytes;
    }

    public String getmUrl() {
        return mUrl;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
