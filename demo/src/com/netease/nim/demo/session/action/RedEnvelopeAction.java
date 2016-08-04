package com.netease.nim.demo.session.action;

import android.content.Intent;
import android.util.Log;

import com.netease.nim.demo.R;
import com.netease.nim.demo.redenvelope.activity.CreateRedEnvelopeActivity;
import com.netease.nim.demo.session.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bangzhu on 2016/7/22.
 */
public class RedEnvelopeAction extends BaseAction {

    public RedEnvelopeAction() {
        super(R.drawable.apw, R.string.red_envelope);
    }

    private void createRedEnvelope(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), CreateRedEnvelopeActivity.class);
        Log.i("HZWING", getActivity() + "" + "====================");
        getActivity().startActivityForResult(intent, makeRequestCode(125));
    }

    @Override
    public void onClick() {
        createRedEnvelope();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 125:
                if(resultCode == getActivity().RESULT_OK){
                    String lucky_money = data.getExtras().getString("lucky_money");
                    String left_message = data.getExtras().getString("left_message");
                    RedEnvelopeAttachment attachment = new RedEnvelopeAttachment(lucky_money, left_message);
                    CustomMessageConfig config = new CustomMessageConfig();
                    IMMessage message;
                    if(getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom){
                        message = ChatRoomMessageBuilder.createChatRoomCustomMessage(getAccount(), attachment);
                    }else{
                        message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), "红包", attachment, config);
                    }
                    //设置拓展字段
                    if(message.getRemoteExtension() == null){
                        Map<String, Object> extendData = new HashMap<>();
                        extendData.put("isOpen", 0);
                        extendData.put("messageId", getAccount()+"_"+getSessionType()+"_"+new Date());
                        message.setRemoteExtension(extendData);
                    }

                    sendMessage(message);
                }
                break;
        }
    }

}
