package com.netease.nim.demo.redenvelope.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by bangzhu on 2016/8/15.
 */
public class AsyncHttpUtil {
    private static final String BASE_URL = "http://192.168.1.124:8080/packet/servlet/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl){
        return BASE_URL+relativeUrl;
    }
}
