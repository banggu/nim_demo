package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by bangzhu on 2016/7/25.
 */
public class RedEnvelopeAttachment extends CustomAttachment {

    private final String KEY_LUCKY_MONEY = "luckyMoney";
    private final String KEY_LEFT_MESSAGE = "leftMessage";

    private String luckyMoney;
    private String leftMessage;
    protected int type;

    public RedEnvelopeAttachment() {
        super(CustomAttachmentType.RedEnvelope);
    }

    public RedEnvelopeAttachment(String luckyMoney, String leftMessage) {
        this();
        this.luckyMoney = luckyMoney;
        this.leftMessage = leftMessage;
    }

    @Override
    protected void parseData(JSONObject data) {
        this.luckyMoney = data.getString(KEY_LUCKY_MONEY);
        this.leftMessage = data.getString(KEY_LEFT_MESSAGE);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_LUCKY_MONEY, luckyMoney);
        data.put(KEY_LEFT_MESSAGE, leftMessage);
        return data;
    }

    public String getLuckyMoney() {
        return luckyMoney;
    }

    public String getLeftMessage() {
        return leftMessage;
    }
}
