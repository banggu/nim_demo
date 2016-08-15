package com.netease.nim.demo.redenvelope.model;

/**
 * Created by bangzhu on 2016/8/8.
 */
public class PacketJson {
    private int type;
    private int quantity;
    private float total;
    private String message;
    private String group_id;
    private String sender_id;

    public PacketJson() {
    }

    public PacketJson(int type, int quantity, float total, String message, String group_id, String sender_id) {
        this.type = type;
        this.quantity = quantity;
        this.total = total;
        this.message = message;
        this.group_id = group_id;
        this.sender_id = sender_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }
}
