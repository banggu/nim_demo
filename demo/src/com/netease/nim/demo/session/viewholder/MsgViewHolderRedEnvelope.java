package com.netease.nim.demo.session.viewholder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import com.android.volley.RequestQueue;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.redenvelope.activity.RedEnvelopeDetailActivity;
import com.netease.nim.demo.redenvelope.util.UrlUtil;
import com.netease.nim.demo.session.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.activity.TeamMessageActivity;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bangzhu on 2016/7/25.
 */
public class MsgViewHolderRedEnvelope extends MsgViewHolderBase implements Serializable{
    private int isMsgOpen;
    private int canOpen;
    private Map<String, Object> remoteExtension;
    private Map<String, Object> localExtension;
    private IMMessage msgToFriend;
    private AlertDialog alertDialog;
    private AlertDialog.Builder build;
    private HeadImageView friend_image;
    private TextView friend_name, friend_message;
    private ImageView closeDialog, openRedEnvelope;
    private AnimationDrawable anim;
    private RequestQueue requestQueue;

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
        //收到单聊红包
        if(message.getSessionType() == SessionTypeEnum.P2P){
            String sender = message.getFromAccount();
            remoteExtension = message.getRemoteExtension();
            localExtension = message.getLocalExtension();
            if (localExtension == null) {
                isMsgOpen = Integer.parseInt(remoteExtension.get("isOpen").toString());
            } else {
                isMsgOpen = Integer.parseInt(localExtension.get("isOpen").toString());
            }
            if (!sender.equals(DemoCache.getAccount())) {
                if(isMsgOpen == 0){
                    //打开红包
                    openRedenvelope(true);
                }else{
                    toRedEnveloptDetail("receiver");
                }
            }else{
                toRedEnveloptDetail("sender");
            }
            //收到群聊红包
        }else if(message.getSessionType() == SessionTypeEnum.Team){
            requestQueue = Volley.newRequestQueue(context);
            //访问服务器，确定红包是否打开过
            checkPacketIsOpen();
        }

    }

    private void initView(View view, boolean canOpen) {
        friend_image = (HeadImageView) view.findViewById(R.id.friend_image);
        friend_name = (TextView) view.findViewById(R.id.friend_name);
        friend_message = (TextView) view.findViewById(R.id.friend_message);
        closeDialog = (ImageView) view.findViewById(R.id.iv_cancle);
        openRedEnvelope = (ImageView) view.findViewById(R.id.open_red_envelope);

        friend_image.loadBuddyAvatar(message.getFromAccount());
        friend_name.setText(message.getFromNick());
        String text = ((RedEnvelopeAttachment) message.getAttachment()).getLeftMessage();
        if (!TextUtils.isEmpty(text)) {
            friend_message.setText(text);
        }
        anim = (AnimationDrawable) openRedEnvelope.getBackground();

        if(canOpen){
            openRedEnvelope.setVisibility(View.VISIBLE);
        }else{
            openRedEnvelope.setVisibility(View.GONE);
            friend_message.setText(context.getResources().getText(R.string.redenvelope_empty));
        }
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
                        if (message.getSessionType() == SessionTypeEnum.P2P)
                            toRedEnveloptDetail("receiver");
                        else if (message.getSessionType() == SessionTypeEnum.Team){
                            receiveTeamRedenvelope();
                            toRedEnveloptDetail("sender");
                        }
                            //访问服务器，领取红包
                            //toRedEnveloptDetail("sender");
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

    //弹出打开红包对话框
    private void openRedenvelope(boolean canOpen){
        build = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.red_envelope_open, null);
        initView(dialogView, canOpen);
        build.setView(dialogView);
        build.setCancelable(true);
        alertDialog = build.create();
        setListener();
        alertDialog.show();
    }

    //领取红包
    private void receiveRedEnvelope() {
        //创建 Tip 消息
        if(message.getSessionType() == SessionTypeEnum.P2P){
            msgToFriend = MessageBuilder.createTipMessage(message.getFromAccount(), message.getSessionType());
        }else if(message.getSessionType() == SessionTypeEnum.Team){
            msgToFriend = MessageBuilder.createTipMessage(message.getSessionId(), SessionTypeEnum.Team);
        }

        //设置Tip内容
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(DemoCache.getAccount());
        msgToFriend.setContent(userInfo.getName() + "领取了红包");

        CustomMessageConfig config = new CustomMessageConfig();
        config.enablePush = false; // 不推送
        msgToFriend.setConfig(config);

        //设置拓展字段
        Map<String, Object> data = new HashMap<>();
        data.put("isOpen", 1);
        data.put("messageId", message.getUuid());
        msgToFriend.setRemoteExtension(data);
        msgToFriend.setLocalExtension(data);
        message.setLocalExtension(data);
        NIMClient.getService(MsgService.class).updateIMMessage(message);

        //发送消息
        NIMClient.getService(MsgService.class).sendMessage(msgToFriend, false);

    }

    //领取群红包
    private void receiveTeamRedenvelope(){
        RedEnvelopeAttachment attachment = (RedEnvelopeAttachment) message.getAttachment();
        String packet_id = attachment.getPacketId();
        String user_id = DemoCache.getAccount();
        String relativeUrl = "PacketOpenServlet?id="+packet_id+"&user_id="+user_id;
        String url = UrlUtil.getUrl(relativeUrl);
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                           Log.i("HZWING", "领取红包返回结果"+response+"----------------");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("HZWING", "访问失败" + volleyError.toString() + "===============");
            }
        });
        requestQueue.add(request);
    }

    //显示红包详情
    private void toRedEnveloptDetail(String role){
        Intent intent = new Intent();
        intent.setClass(context, RedEnvelopeDetailActivity.class);
        Bundle bundle = new Bundle();
        IMMessage msg = message;
        bundle.putSerializable("message", msg);
        bundle.putString("openTime", new Date().toString());
        bundle.putString("userId", DemoCache.getAccount());
        bundle.putString("role", role);
        intent.putExtras(bundle);
//        IMMessage msg = message;
//        intent.putExtra("message", msg);
//        intent.putExtra("openTime", new Date().toString());
//        intent.putExtra("userId", DemoCache.getAccount());
//        intent.putExtra("role", role);
        context.startActivity(intent);
    }

    private void checkPacketIsOpen(){
        RedEnvelopeAttachment attachment = (RedEnvelopeAttachment) message.getAttachment();
        String packet_id = attachment.getPacketId();
        String user_id = DemoCache.getAccount();
        String relativeUrl = "PacketCheckServlet?id="+packet_id+"&user_id="+user_id;
        String url = UrlUtil.getUrl(relativeUrl);
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            canOpen = jsonObject.getInt("data");
                            Log.i("HZWING", "打开标志："+canOpen+"============");
                            switch (canOpen){
                                case 0:
                                    toRedEnveloptDetail("sender");
                                    break;
                                case 1:
                                    openRedenvelope(true);
                                    break;
                                case 2:
                                    openRedenvelope(false);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i("HZWING", "访问失败" + volleyError.toString() + "===============");
                    }
                });
        requestQueue.add(request);

    }
}
