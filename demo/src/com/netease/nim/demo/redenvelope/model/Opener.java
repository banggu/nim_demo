package com.netease.nim.demo.redenvelope.model;

/**
 * Created by GeiLe on 2016/8/4.
 */
public class Opener {
    private int packet_id;
    private int user_id;
    private String user_name;
    private String user_img;
    private String time;
    private float money;

    public Opener(int packet_id, int user_id, String user_name, String user_img, String time, float money) {
        this.packet_id = packet_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_img = user_img;
        this.time = time;
        this.money = money;
    }

    public int getPacket_id() {
        return packet_id;
    }

    public void setPacket_id(int packet_id) {
        this.packet_id = packet_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }
}
