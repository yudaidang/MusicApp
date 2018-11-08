package com.example.imageloader;

import android.graphics.BitmapFactory;

import com.example.imageloader.Ultils.BitmapPolicy;
import com.example.imageloader.Ultils.ValueBitmap;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class DiskCacheSimple {
    private static DiskCacheSimple sInstance = new DiskCacheSimple();
    private BitmapPolicy mBitmapPolicy;
    private File diskCacheDir = null;

    private DiskCacheSimple() {
        mBitmapPolicy = new BitmapPolicy();
    }

    public static DiskCacheSimple getInstance() {
        return sInstance;
    }

    public void setDiskCacheDir(File diskCacheDir) {
        this.diskCacheDir = diskCacheDir;
    }

    public synchronized void clearAll() {
        if (diskCacheDir != null) {
            diskCacheDir.delete();
        }
    }

    public synchronized void clearDiskLastDate(Date mDate) {
        clearDiskLastDate(diskCacheDir, mDate);
    }

    public synchronized void clearDiskLastDate(File mPath, Date mDate) {
        for (File f : mPath.listFiles()) {
            long time = mDate.getTime();
            long modify = f.lastModified();
            if (modify <= time) {
                f.delete();
            }

        }
    }

    public boolean isExistFile(String mPath) {
        File f = new File(mPath);
        return f.exists() && f.length() != 0;
    }

    public synchronized ValueBitmap get(String mPath, String mUrl) {
        return isExistFile(mPath) ? mBitmapPolicy.read(new File(mPath), mUrl) : null;
    }

    public synchronized ValueBitmap get(String mPath, int width, int height, BitmapFactory.Options options, String mUrl) {
        return isExistFile(mPath) ? mBitmapPolicy.read(new File(mPath), width, height, options, mUrl) : null;
    }

    //editting
    public synchronized boolean put(String key, byte[] value, String diskPath) {
        if (value.length != 0) {
            File file = new File(diskPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            try {
                mBitmapPolicy.write(new File(file, key.hashCode() + ""), value);
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }


}
