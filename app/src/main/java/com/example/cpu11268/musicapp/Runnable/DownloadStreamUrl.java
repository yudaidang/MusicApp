package com.example.cpu11268.musicapp.Runnable;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.example.cpu11268.musicapp.Constant;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadStreamUrl implements Runnable {
    private String Url;
    private Handler mHandler;
    public DownloadStreamUrl(String url, Handler mHandler) {
        this.Url = url;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        byte[] bytes = downloadStream();
        Message message = mHandler.obtainMessage(Constant.DOWN_LOAD_STREAM_AUDIO, bytes);
        message.sendToTarget();
    }

    private byte[] downloadStream() {
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        byte[] bytes = new byte[0];
        try {
            if (TextUtils.isEmpty(Url)) {
                return null;
            }
            URL url = new URL(Url);
            //connection
            connection = (HttpURLConnection) url.openConnection();
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                String newUrl = connection.getHeaderField("Location");
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
            }

            inputStream = connection.getInputStream();

            bytes = IOUtils.toByteArray(inputStream);
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
