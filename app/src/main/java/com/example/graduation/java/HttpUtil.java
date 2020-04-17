package com.example.graduation.java;


import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static void sendJsonOkhttpRequest(String url, RequestBody requestBody, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
