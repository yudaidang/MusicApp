package com.example.imageloader;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.example.imageloader.Ultils.AddImageRunnable;
import com.example.imageloader.Ultils.DataDownload;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadImageRunnable implements Runnable {

    private static Executor mExecutor = new ThreadPoolExecutor(2,
            3, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private Handler mHandler;
    private String mUrl;
    private String diskPath;

    public DownloadImageRunnable(String mUrl, Handler mHandler, String diskPath) {
        this.mUrl = mUrl;
        this.mHandler = mHandler;
        this.diskPath = diskPath;
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        byte[] bytes = downloadImage(mUrl);
        DataDownload data = new DataDownload(mUrl, bytes);
        Message message = mHandler.obtainMessage(ImageLoader.LOAD_INTERNET, data);
        message.sendToTarget();
    }

    private byte[] downloadImage(String imgUrl) {
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        byte[] bytes = new byte[0];
        try {
            if(TextUtils.isEmpty(imgUrl)){
                return null;
            }
            URL url = new URL(imgUrl);
            //connection
            connection = (HttpURLConnection) url.openConnection();
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                String newUrl = connection.getHeaderField("Location");
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
            }

            Log.d("IMAGELOADERLOG", "LOAD BITMAP WITH INTERNET");
            inputStream = connection.getInputStream();

            bytes = IOUtils.toByteArray(inputStream);

            AddImageRunnable addImageRunnable = new AddImageRunnable(imgUrl, bytes, diskPath);
            mExecutor.execute(addImageRunnable);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}
