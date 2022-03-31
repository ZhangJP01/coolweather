package com.example.coolweather.utils;

import android.app.DownloadManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * 网络请求 工具类
 */
public class HttpUtil {

    //发送网络请求   address为请求地址 callback为回调函数(接口)
    public static void sendOhHttpRequst(String address,okhttp3.Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}
