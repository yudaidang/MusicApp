package com.example.imageloader;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageKey {
    private String mUrl;
    private int mSize;


    public ImageKey(String url, int width, int height) {
        mUrl = url;
        int sampleSize = sampleSize(width, height);
        if (sampleSize <= 0) {
            mSize = 0;
        } else if (sampleSize <= 64) {
            mSize = 64;
        } else if (sampleSize <= 128) {
            mSize = 128;
        } else if (sampleSize <= 256) {
            mSize = 256;
        } else if (sampleSize <= 512) {
            mSize = 512;
        } else {
            mSize = width;
        }
    }


    private int sampleSize(int width, int height) {
        return width > height ? width : height;
    }

    public String getmUrl() {
        return mUrl;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int mSize) {
        this.mSize = mSize;
    }

    @Override
    public int hashCode() {
        return (mUrl.hashCode() + mSize);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.hashCode() == this.hashCode();
    }

}
