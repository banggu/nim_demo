package com.netease.nim.demo.redenvelope.model;

import java.io.Serializable;

/**
 * Created by GeiLe on 2016/8/4.
 */
public class Opener implements Serializable{
    private int id;
    private int packet_id;
    private String user_id;
    private float money;
    private String created_at;

    public Opener() {
    }

    public Opener(int id, int packet_id, String user_id, float money, String created_at) {
        this.id = id;
        this.packet_id = packet_id;
        this.user_id = user_id;
        this.money = money;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPacket_id() {
        return packet_id;
    }

    public void setPacket_id(int packet_id) {
        this.packet_id = packet_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
