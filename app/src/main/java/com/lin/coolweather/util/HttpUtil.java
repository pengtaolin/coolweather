package com.lin.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * http操作工具类
 */
public class HttpUtil {
    /**
     * 用于发送服务器请求
     * @param address 请求地址
     * @param callback 请求回调函数
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        //客户端
        OkHttpClient client = new OkHttpClient();
        //构建数据请求
        Request request = new Request.Builder().url(address).build();
        //发送请求获得响应
        client.newCall(request).enqueue(callback);
    }
}
