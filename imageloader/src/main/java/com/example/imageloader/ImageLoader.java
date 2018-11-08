package com.example.imageloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.imageloader.Ultils.CallBackImageView;
import com.example.imageloader.Ultils.MessageBitmap;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageLoader implements Handler.Callback {
    public static final int LOAD_INTERNET = 101;
    public static final int LOAD_DISK = 102;
    public static final int INTERNET_NOT_CONNECT = 105;
    private static final int DEFAULT_MAX_SIZE = 0;
    private static final int LOAD_MEM = 103;
    private static final int URL_NULL = 104;
    protected static Executor executorInternet;
    private static ImageLoader sInstance = new ImageLoader();
    protected final Handler mHandler;
    private int mWidth = DEFAULT_MAX_SIZE;
    private int mHeight = DEFAULT_MAX_SIZE;
    private Executor executor;
    private WeakReference<View> view;
    // 1 imageworker co nhieu callback.
    private HashMap<ImageWorker.MyDownloadCallback, ImageWorker> mCallbacksImageWorker = new HashMap<>();
    //Integer view.hashcode();

    private HashMap<Integer, Runnable> listTaskQueue = new HashMap<>();
    private BlockingQueue queueDownload;

    public ImageLoader() {

        mHandler = new Handler(this);
        if (executor == null) {
            BlockingQueue queueDisk = new LinkedBlockingDeque() {

                @Override
                public boolean add(Object o) {
                    if (contains(o)) {
                        remove(o);
                    }
                    addLast(o);
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }
                    super.putLast(o);
                }

                @Override
                public boolean offer(Object o) {
                    return offerLast(o);
                }

                @Override
                public Object poll() {
                    return pollLast();
                }

                @Override
                public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
                    return super.pollLast(timeout, unit);
                }

                @Override
                public Object take() throws InterruptedException {
                    return takeLast();
                }

                @Override
                public Object peek() {
                    return super.peekLast();
                }
            };
            executor = new ThreadPoolExecutor(
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    queueDisk);
        }

        if (executorInternet == null) {
            queueDownload = new LinkedBlockingDeque() {

                @Override
                public boolean add(Object o) {
                    if (contains(o)) {
                        remove(o);
                    }
                    addLast(o);
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }
                    super.putLast(o);
                }

                @Override
                public boolean offer(Object o) {
                    return offerLast(o);
                }

                @Override
                public Object poll() {
                    return pollLast();
                }

                @Override
                public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
                    return super.pollLast(timeout, unit);
                }

                @Override
                public Object take() throws InterruptedException {
                    return takeLast();
                }

                @Override
                public Object peek() {
                    return super.peekLast();
                }
            };
            executorInternet = new ThreadPoolExecutor(
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    queueDownload);
        }
    }

    public static ImageLoader getInstance() {
        return sInstance;
    }

    public void loadImageWorker(Context context, String mUrl, View view, String diskPath) {
        this.mWidth = (int) (view.getLayoutParams().width / (Resources.getSystem().getDisplayMetrics().density));
        this.mHeight = (int) (view.getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        this.view = new WeakReference<>(view);
        this.loadImageWorker(context, mUrl, new CallBackImageView((ImageView) view), diskPath);
    }

    public void setWidthHeight(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public void loadImageWorker(Context context, String mUrl, ImageWorker.MyDownloadCallback mCallback, String diskPath) {
        ImageKey imageKey = new ImageKey(mUrl, mWidth, mHeight);
        ImageWorker imageWorker;
        imageWorker = getImageWorker(imageKey);
        Log.d("TEST", mUrl);

        if (imageWorker == null) {
            imageWorker = new ImageWorker(imageKey);
            mCallbacksImageWorker.put(mCallback, imageWorker);
        } else {
            clearCallback(mCallback);
        }

        imageWorker.listCallback.add(mCallback);

        Bitmap bitmap;
        if (TextUtils.isEmpty(mUrl) || mUrl == "null") {
            imageWorker.onDownloadComplete(null, URL_NULL);
        } else {
            bitmap = ImageCache.getInstance().findBitmapCache(mWidth, mHeight, mUrl);
            if (bitmap == null) {
                DiskBitmapRunnable diskBitmapRunnable = new DiskBitmapRunnable(context,
                        imageWorker, diskPath);
                if (view != null) {
                    listTaskQueue.put(view.get().hashCode(), diskBitmapRunnable);
                }
                executor.execute(diskBitmapRunnable);
            } else {
                Log.d("IMAGELOADERLOG", "LOAD BITMAP WITH MEMORY");

                imageWorker.onDownloadComplete(bitmap, LOAD_MEM);
            }
        }
    }

    public void clearTaskQueue(View view) {
        if (listTaskQueue.containsKey(view.hashCode())) {
            queueDownload.remove(listTaskQueue.get(view.hashCode()));
            listTaskQueue.remove(view.hashCode());
        }
    }

    public void clearCallback(ImageWorker.MyDownloadCallback callback, String mUrl) {
        if (callback != null) {
            ImageWorker imageWorker = mCallbacksImageWorker.get(callback);
            if (imageWorker != null && (imageWorker.imageKey.getmUrl() != mUrl) && imageWorker.listCallback.contains(callback)) {
                mCallbacksImageWorker.remove(callback);
                imageWorker.listCallback.remove(callback);
            }
        }
    }

    public void clearCallback(ImageWorker.MyDownloadCallback callback) {
        if (callback != null) {
            ImageWorker imageWorker = mCallbacksImageWorker.get(callback);
            if (imageWorker != null && imageWorker.listCallback.contains(callback)) {
                mCallbacksImageWorker.remove(callback);
                imageWorker.listCallback.remove(callback);
            }
        }
    }

    public void clearView(View view) {
        ImageWorker.MyDownloadCallback callback = new CallBackImageView((ImageView) view);
        clearCallback(callback);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == ImageLoader.LOAD_DISK || msg.what == ImageLoader.INTERNET_NOT_CONNECT) {
            MessageBitmap messageBitmap = (MessageBitmap) msg.obj;

            ImageWorker imageWorker = getImageWorker(messageBitmap.getImageKey());
            if (imageWorker != null) {
                imageWorker.onDownloadComplete(messageBitmap.getmBitmap(), msg.what);
            }
            removeCallback(imageWorker);
        }
        return true;
    }

    private void removeCallback(ImageWorker im) {
        for (Iterator<Map.Entry<ImageWorker.MyDownloadCallback, ImageWorker>> it = mCallbacksImageWorker.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<ImageWorker.MyDownloadCallback, ImageWorker> entry = it.next();
            if (entry.getValue().equals(im)) {
                it.remove();
            }
        }
    }

    private ImageWorker getImageWorker(ImageKey imageKey) {
        for (ImageWorker ik : mCallbacksImageWorker.values()) {
            if (ik.imageKey.equals(imageKey)) {
                return ik;
            }
        }
        return null;
    }

}
