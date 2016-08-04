package com.netease.nim.demo.session.viewholder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.RedEnvelope.RedEnvelopeDetailActivity;
import com.netease.nim.demo.session.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bangzhu on 2016/7/25.
 */
public class MsgViewHolderRedEnvelope extends MsgViewHolderBase {
    private int isMsgOpen;
    private Map<String, Object> remoteExtension;
    private Map<String, Object> localExtension;
    private IMMessage msgToFriend;
    private AlertDialog alertDialog;
    private AlertDialog.Builder build;
    private TextView friend_name, friend_message;
    private ImageView closeDialog, openRedEnvelope;
    private AnimationDrawable anim;

    @Override
    protected int getContentResId() {
        return R.layout.red_envelope_item;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

    }

    @Override
    protected int leftBackground() {
        return R.drawable.red_envelope_left_bg;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.red_envelope_right_bg;
    }

    @Override
    protected void onItemClick() {
        String sender = message.getFromAccount();
        remoteExtension = message.getRemoteExtension();
        localExtension = message.getLocalExtension();
        if (localExtension == null) {
            isMsgOpen = Integer.parseInt(remoteExtension.get("isOpen").toString());
        } else {
            isMsgOpen = Integer.parseInt(localExtension.get("isOpen").toString());
        }
        Log.i("HZWING", isMsgOpen+"");
        if (!sender.equals(DemoCache.getAccount())) {
            if(isMsgOpen == 0){
                //打开红包
                build = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.red_envelope_open, null);
                initView(dialogView);
                build.setView(dialogView);
                build.setCancelable(true);
                alertDialog = build.create();
                setListener();
                alertDialog.show();
            }else{
                toRedEnveloptDetail("receiver");
            }
        }else{
            Log.i("HZWING", message.getRemoteExtension().get("messageId").toString());
            toRedEnveloptDetail("sender");
        }
    }

    private void initView(View view) {
        friend_name = (TextView) view.findViewById(R.id.friend_name);
        friend_message = (TextView) view.findViewById(R.id.friend_message);
        closeDialog = (ImageView) view.findViewById(R.id.iv_cancle);
        openRedEnvelope = (ImageView) view.findViewById(R.id.open_red_envelope);

        friend_name.setText(message.getFromNick());
        String text = ((RedEnvelopeAttachment) message.getAttachment()).getLeftMessage();
        if (text == "") {
            text = "恭喜发财，大吉大利";
        }
        friend_message.setText(text);
        anim = (AnimationDrawable) openRedEnvelope.getBackground();
    }

    private void setListener() {
        openRedEnvelope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开红包动画
                anim.start();
                //执行延迟
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        receiveRedEnvelope();
                        toRedEnveloptDetail("receiver");
                    }
                }, 500);
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //领取红包
    private void receiveRedEnvelope() {
        //创建 Tip 消息
        msgToFriend = MessageBuilder.createTipMessage(message.getFromAccount(), message.getSessionType());

        //设置Tip内容
        msgToFriend.setContent("你领取了"+message.getFromNick()+"的红包");

        CustomMessageConfig config = new CustomMessageConfig();
        config.enablePush = false; // 不推送
        msgToFriend.setConfig(config);

        //设置拓展字段
        Map<String, Object> data = new HashMap<>();
        data.put("isOpen", 1);
        data.put("messageId", message.getFromAccount() + "_" + message.getSessionType()+"_"+new Date());
        data.put("targetId", message.getRemoteExtension().get("messageId").toString());
        msgToFriend.setRemoteExtension(data);
        msgToFriend.setLocalExtension(data);
        message.setLocalExtension(data);
        NIMClient.getService(MsgService.class).updateIMMessage(message);

        //发送消息
        NIMClient.getService(MsgService.class).sendMessage(msgToFriend, false);

    }

    private void toRedEnveloptDetail(String role){
        Intent intent = new Intent();
        intent.setClass(context, RedEnvelopeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("message", message);
        bundle.putString("role", role);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
