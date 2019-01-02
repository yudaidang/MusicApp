package com.example.cpu11268.musicapp.Runnable;

import android.os.Handler;
import android.os.Message;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadApiRunnable implements Runnable {
    private HttpUtils httpUtils = new HttpUtils();
    private String userId = "25450224";
    private Handler mHandler;
    public LoadApiRunnable(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        URL myUrl = null;
        HttpURLConnection conn;
        BufferedReader reader;
        List<Track> tracks;
        String response = null;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            myUrl = new URL(httpUtils.getListTrackUserUrl(userId));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (myUrl != null) {
            try {
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                response = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        tracks = getListTrack(response);
        Message message = mHandler.obtainMessage(Constant.LOAD_API, tracks);
        message.sendToTarget();


    }

    private List<Track> getListTrack(String response) {
        ArrayList<Track> tracks = new ArrayList<>();
        try {
            JSONArray dataJson = new JSONArray(response);


            for (int i = 0; i < dataJson.length(); i++) {
                JSONObject jsonObject = dataJson.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("title");
                String streamUrl = jsonObject.getString("stream_url") + httpUtils.getClientId();
                String duration = jsonObject.getString("duration");
                String image = jsonObject.getString("artwork_url");
                JSONObject user = jsonObject.getJSONObject("user");
                String artist = user.getString("username");
                tracks.add(new Track(id, name, artist, duration, streamUrl, image));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tracks;
    }
}
