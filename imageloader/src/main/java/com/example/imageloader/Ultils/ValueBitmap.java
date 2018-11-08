package com.example.imageloader.Ultils;

import android.graphics.Bitmap;

public class ValueBitmap {
    private Bitmap mBitmap;
    private int mSampleSize;
    private String mUrl;
    private int mOutWidth;
    private int mOutHeight;

    public ValueBitmap(Bitmap mBitmap, int mSampleSize, String mUrl, int mOutWidth, int mOutHeight) {
        this.mBitmap = mBitmap;
        this.mSampleSize = mSampleSize;
        this.mUrl = mUrl;
        this.mOutWidth = mOutWidth;
        this.mOutHeight = mOutHeight;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public int getmSampleSize() {
        return mSampleSize;
    }

    public String getmUrl() {
        return mUrl;
    }

    public int getmOutWidth() {
        return mOutWidth;
    }

    public int getmOutHeight() {
        return mOutHeight;
    }

}
