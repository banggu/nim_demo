package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by bangzhu on 2016/7/25.
 */
public class RedEnvelopeAttachment extends CustomAttachment implements Serializable {

    private final String KEY_LUCKY_MONEY = "luckyMoney";
    private final String KEY_LEFT_MESSAGE = "leftMessage";
    private final String KEY_PACKET_ID = "packetId";

    private String luckyMoney;
    private String leftMessage;
    private String packetId;
    protected int type;

    public RedEnvelopeAttachment() {
        super(CustomAttachmentType.RedEnvelope);
    }

//    public RedEnvelopeAttachment(String luckyMoney, String leftMessage) {
//        this();
//        this.luckyMoney = luckyMoney;
//        this.leftMessage = leftMessage;
//    }

    public RedEnvelopeAttachment(String luckyMoney, String leftMessage, String packetId) {
        this();
        this.luckyMoney = luckyMoney;
        this.leftMessage = leftMessage;
        this.packetId = packetId;
    }

    @Override
    protected void parseData(JSONObject data) {
        this.luckyMoney = data.getString(KEY_LUCKY_MONEY);
        this.leftMessage = data.getString(KEY_LEFT_MESSAGE);

        this.packetId = data.getString(KEY_PACKET_ID);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_LUCKY_MONEY, luckyMoney);
        data.put(KEY_LEFT_MESSAGE, leftMessage);

        data.put(KEY_PACKET_ID, packetId);
        return data;
    }

    public String getLuckyMoney() {
        return luckyMoney;
    }

    public String getLeftMessage() {
        return leftMessage;
    }

    public String getPacketId() {
        return packetId;
    }
}
