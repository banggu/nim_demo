package com.netease.nim.demo.redenvelope.util;

/**
 * Created by bangzhu on 2016/8/8.
 */
public class UrlUtil {
    private static final String BASE_REDENVELOPE_URL = "http://192.168.1.124:8080/packet/servlet/";

    public static String getUrl(String url){
        return BASE_REDENVELOPE_URL+url;
    }
}
