package com.example.imageloader.Ultils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.imageloader.ImageWorker;

import java.lang.ref.WeakReference;

public class CallBackImageView implements ImageWorker.MyDownloadCallback {

    private WeakReference<ImageView> imageView;

    public CallBackImageView(ImageView imageView) {
        this.imageView = new WeakReference<>(imageView);
    }

    //remove which
    @Override
    public void onLoad(Bitmap bitmap, Object which, int resultCode) {
        if (imageView.get() != null) {
            imageView.get().setImageBitmap(bitmap);
        }
    }

    @Override
    public int hashCode() {
        return imageView.get().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.hashCode() == this.hashCode();
    }
}
