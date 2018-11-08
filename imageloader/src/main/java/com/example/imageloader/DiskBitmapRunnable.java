package com.example.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.example.imageloader.Ultils.DataDownload;
import com.example.imageloader.Ultils.MessageBitmap;
import com.example.imageloader.Ultils.NetworkChecker;
import com.example.imageloader.Ultils.ValueBitmap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DiskBitmapRunnable implements Runnable, Handler.Callback {
    private final WeakReference<Context> mContext;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private final Handler mHandlerDownload;
    private final int PRIORITY_THREAD = 1;
    //    private ImageKey imageKey;
    private ImageWorker imageWorker;
    //List url downloading
    private HashMap<String, HashSet<ImageWorker>> listDownloading = new HashMap<>();
    private String diskCachePath;


    public DiskBitmapRunnable(Context context, ImageWorker imageWorker, String diskCachePath) {
        this.mContext = new WeakReference<>(context);
        this.imageWorker = imageWorker;
        mHandlerDownload = new Handler(this);
        this.diskCachePath = diskCachePath;
    }

    @Override
    public void run() {
        Process.setThreadPriority(PRIORITY_THREAD);

        if (!TextUtils.isEmpty(diskCachePath)) {
            DiskCacheSimple.getInstance().setDiskCacheDir(new File(diskCachePath));
        } else {
            diskCachePath = getDiskPath(mContext.get(), "IMAGE");
            DiskCacheSimple.getInstance().setDiskCacheDir(new File(diskCachePath));
        }

        ValueBitmap bitmap;

        if (ImageCache.getInstance().isBitmapFromDiskCache(diskCachePath + File.separator + imageWorker.imageKey.getmUrl().hashCode())) {
            if (imageWorker.imageKey.getSize() == ImageWorker.DEFAULT_MAX_SIZE) {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(diskCachePath + File.separator + imageWorker.imageKey.getmUrl().hashCode(), imageWorker.imageKey.getmUrl());
            } else {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(diskCachePath + File.separator + imageWorker.imageKey.getmUrl().hashCode(), imageWorker.imageKey.getSize(), imageWorker.imageKey.getSize(), options, imageWorker.imageKey.getmUrl());
            }
            ImageCache.getInstance().addBitmapToMemoryCache(bitmap); //?
            Log.d("IMAGELOADERLOG", "LOAD BITMAP WITH DISK");

            handleResult(imageWorker.imageKey, bitmap.getmBitmap(), ImageLoader.LOAD_DISK);
        } else {
            if (mContext.get() != null && NetworkChecker.isOnline(mContext.get())) {
                HashSet<ImageWorker> list;
                if (!listDownloading.containsKey(imageWorker.imageKey.getmUrl())) {
                    list = new HashSet<>();
                    DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(imageWorker.imageKey.getmUrl(), mHandlerDownload, diskCachePath);
                    ImageLoader.executorInternet.execute(downloadImageRunnable);

                } else {
                    list = listDownloading.get(imageWorker.imageKey.getmUrl());
                }
                list.add(imageWorker);
                listDownloading.put(imageWorker.imageKey.getmUrl(), list);

            } else {
                handleResult(imageWorker.imageKey, null, ImageLoader.INTERNET_NOT_CONNECT);
            }
        }
    }

    private void handleResult(ImageKey imageKey, Bitmap bitmap, int resultCode) {
        MessageBitmap messageBitmap = new MessageBitmap(imageKey, bitmap);
        Message message = ImageLoader.getInstance().mHandler.obtainMessage(resultCode, messageBitmap);
        message.sendToTarget();
    }


    // NOT FIX
    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == ImageLoader.LOAD_INTERNET) {
            DataDownload data = (DataDownload) msg.obj;
            if (listDownloading.containsKey(data.getmUrl())) {
                Set<ImageWorker> list = listDownloading.get(data.getmUrl());
                if (list != null) {
                    for (ImageWorker ik : list) {
                        ik.decodeDataBitmap(data.getBytes(), options, msg.what);
                    }
                }
                listDownloading.remove(data.getmUrl());
            }
        }
        return false;
    }

    private String getDiskPath(Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                        Objects.requireNonNull(context.getExternalCacheDir()).getPath() :
                        context.getCacheDir().getPath();
        return cachePath + File.separator + uniqueName;
    }
}
