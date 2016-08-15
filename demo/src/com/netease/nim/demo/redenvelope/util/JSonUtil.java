package com.netease.nim.demo.redenvelope.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bangzhu on 2016/8/5.
 */
public class JSonUtil {

    public static <T> List<T> getListFromJson(String jsonData, Class<T> cls){
        Gson gson = new Gson();
        List<T> objectList = gson.fromJson(jsonData, new TypeToken<List<T>>(){}.getType());
        if(objectList == null){
            return null;
        }
        List<T> list = new ArrayList<T>(objectList.size());
        for(T map : objectList){
            String tmpJson = gson.toJson(map);
            list.add(gson.fromJson(tmpJson, cls));
        }
        return list;
    }

    public static <T> T getObjFromJson(String jsonData, Class<T> cls){
        return new Gson().fromJson(jsonData, cls);
    }

    public static String toJSON(Object obj){
        return new Gson().toJson(obj);
    }

    public static <T> String toJSONArry(List<T> list){
        Gson gson = new Gson();
        StringBuilder json = new StringBuilder("[");
        for(T t : list){
            json.append(gson.toJson(t));
            json.append(",");
        }
        if(json.charAt(json.length()-1) == ','){
            json.deleteCharAt(json.length()-1);
        }
        json.append("]");
        return json.toString();
    }
}
