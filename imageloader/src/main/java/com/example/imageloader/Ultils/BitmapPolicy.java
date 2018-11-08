package com.example.imageloader.Ultils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapPolicy {

    public void write(File outputFile, byte[] value) throws IOException {
        BufferedOutputStream buf = null;
        try {
            FileOutputStream fl = new FileOutputStream(outputFile);
            buf = new BufferedOutputStream(fl);
            buf.write(value);
        } catch (Exception ex) {
            Log.e("LOGLOG", ex.getMessage());
        } finally {
            if (buf != null) {

                buf.flush();
                buf.close();
            }
        }
    }


    public ValueBitmap read(File inputFile, int width, int height, BitmapFactory.Options options, String mUrl) {
        int mSampleSize, mOutWidth, mOutHeight;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);
        mSampleSize = caculateInSampleSize(options, width, height);
        mOutWidth = options.outWidth;
        mOutHeight = options.outHeight;
        options.inSampleSize = mSampleSize;
        options.inMutable = true;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);
        return new ValueBitmap(bitmap, mSampleSize, mUrl, mOutWidth, mOutHeight);
    }

    public ValueBitmap read(File inputFile, String mUrl) {
        Bitmap bitmap = BitmapFactory.decodeFile(inputFile.getAbsolutePath());
        return new ValueBitmap(bitmap, 1, mUrl, bitmap.getWidth(), bitmap.getHeight());
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int widthReq, int heightReq) {
        int inSampleSize = 1;
        while (((options.outHeight / 2) / inSampleSize) >= heightReq && ((options.outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public long size(byte[] value) {
        return value.length;
    }
}
