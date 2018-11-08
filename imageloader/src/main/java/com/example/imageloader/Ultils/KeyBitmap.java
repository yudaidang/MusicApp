package com.example.imageloader.Ultils;

public class KeyBitmap {
    private int mSampleSize;
    private String mUrl;

    public KeyBitmap(int mSampleSize, String mUrl) {
        this.mSampleSize = mSampleSize;
        this.mUrl = mUrl;
    }

    public int getmSampleSize() {
        return mSampleSize;
    }

    public String getmUrl() {
        return mUrl;
    }

    @Override
    public int hashCode() {
        return (mUrl.hashCode() * 31 + mSampleSize);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.hashCode() == this.hashCode();
    }

}
