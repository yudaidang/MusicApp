package com.example.cpu11268.musicapp.Utils;

public class HttpUtils {
    private static final String BASE_URL = "http://api.soundcloud.com/";
    private static final String CLIENT_ID = "95f22ed54a5c297b1c41f72d713623ef";

    public String getListTrackUserUrl(String userId) {
        return BASE_URL + "users/" + userId + "/tracks" + getClientId();
    }

    public String getClientId(){
        return "?client_id="+CLIENT_ID;
    }
}
