package com.example.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;
import android.util.LruCache;

import com.example.imageloader.Ultils.KeyBitmap;
import com.example.imageloader.Ultils.ValueBitmap;

import java.io.File;
import java.util.HashMap;

public class ImageCache {
    private static final int DEFAULT_MAX_SIZE = 66000;
    private static LruCache<KeyBitmap, Bitmap> mMemoryCache;
    private static LruCache<KeyBitmap, Bitmap> mMemoryCacheLarge;
    private static ImageCache sInstance = new ImageCache();
    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int cacheSize = maxMemory / 8;

    private HashMap<String, Pair<Integer, Integer>> list = new HashMap<>();

    private ImageCache() {

        mMemoryCache = new LruCache<KeyBitmap, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(KeyBitmap key, Bitmap value) {
                return value.getByteCount();

            }
        };

        mMemoryCacheLarge = new LruCache<KeyBitmap, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(KeyBitmap key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }

    public static ImageCache getInstance() { //?
        return sInstance;
    }

    public void setSizeLargeMem(int mMaxSize) {
        mMemoryCacheLarge.resize(mMaxSize);
    }

    public void setSizeSmallMem(int mMaxSize) {
        mMemoryCache.resize(mMaxSize);
    }

    //*

    //MemoryCacheTotal
    public synchronized void addBitmapToMemoryCache(ValueBitmap bitmap) {
        if (bitmap == null)
            return;

        KeyBitmap keyBitmap = new KeyBitmap(bitmap.getmSampleSize(), bitmap.getmUrl());
        if ( bitmap.getmBitmap().getByteCount() > DEFAULT_MAX_SIZE) {
            addBitmapToMemoryLargeCache(keyBitmap, bitmap.getmBitmap());
        } else {
            addBitmapToMemorySmallCache(keyBitmap, bitmap.getmBitmap());
        }
        list.put(bitmap.getmUrl(), new Pair<>(bitmap.getmOutWidth(), bitmap.getmOutHeight()));
    }

    private int caculateInSampleSize(int outWidth, int outHeight, int widthReq, int heightReq) {
        int inSampleSize = 1;
        while (((outHeight / 2) / inSampleSize) >= heightReq && ((outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public Bitmap findBitmapCache(int mSampleSize, String mUrl) {
        KeyBitmap key = new KeyBitmap(mSampleSize, mUrl);
        if (isBitmapFromMemorySmallCache(key)) {
            return getBitmapFromCache(mMemoryCache, key);
        } else if (isBitmapFromMemoryLargeCache(key)) {
            return getBitmapFromCache(mMemoryCacheLarge, key);
        } else {
            return null;
        }
    }

    public Bitmap findBitmapCache(int inWidth, int inHeight, String mUrl) {
        int mSampleSize;
        if (list.containsKey(mUrl)) {
            if(inWidth == 0 || inHeight == 0){
                return findBitmapCache(1, mUrl);
            }
            mSampleSize = caculateInSampleSize(list.get(mUrl).first, list.get(mUrl).second, inWidth, inHeight);
        } else {
            return null;
        }
        return findBitmapCache(mSampleSize, mUrl);
    }

    private Bitmap getBitmapFromCache(LruCache<KeyBitmap, Bitmap> cache, KeyBitmap key) {
        if (key != null) {
            return cache.get(key);
        }
        return null;
    }

    private void addBitmapToMemorySmallCache(KeyBitmap key, Bitmap bitmap) { //?
        if (!isBitmapFromMemorySmallCache(key) && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private boolean isBitmapFromMemorySmallCache(KeyBitmap key) { //?
        return mMemoryCache.snapshot().containsKey(key);
    }

    private void addBitmapToMemoryLargeCache(KeyBitmap key, Bitmap bitmap) { //?
        if (!isBitmapFromMemoryLargeCache(key) && bitmap != null) {
            mMemoryCacheLarge.put(key, bitmap);
        }
    }

    private boolean isBitmapFromMemoryLargeCache(KeyBitmap key) { //?
        return mMemoryCacheLarge.snapshot().containsKey(key);
    }

    // Disk Cache
    public synchronized void addBitmapToDiskCache(String key, byte[] bytes, String diskPath) {
        if (!DiskCacheSimple.getInstance().isExistFile(diskPath + File.separator + key.hashCode())) {
            DiskCacheSimple.getInstance().put(key, bytes, diskPath);
        }
    }

    public boolean isBitmapFromDiskCache(String key) {
        return DiskCacheSimple.getInstance().isExistFile(key);
    }

    public ValueBitmap getBitmapFromDiskCache(String key, String mUrl) {
        return DiskCacheSimple.getInstance().get(key, mUrl);
    }


    public ValueBitmap getBitmapFromDiskCache(String key, int width, int height, BitmapFactory.Options options, String mUrl) {
        return DiskCacheSimple.getInstance().get(key, width, height, options, mUrl);
    }

    //*


}