package com.example.day_10_action_app.utility;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.day_10_action_app.R;

public class ServerUtility {

    private static String ip = "10.0.130.144:8080";
    private static String server_url = "http://"+ip+"/";
    private static String ws_url = "ws://"+ip+"/";

    public static String getServerUrl() {
        return server_url;
    }
    public static String getSocketUrl() { return ws_url; }
}
