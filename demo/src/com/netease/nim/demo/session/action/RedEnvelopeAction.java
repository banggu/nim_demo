package com.netease.nim.demo.session.action;

import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.config.DemoServers;
import com.netease.nim.demo.redenvelope.activity.CreateRedEnvelopeActivity;
import com.netease.nim.demo.redenvelope.model.PacketJson;
import com.netease.nim.demo.redenvelope.util.AsyncHttpUtil;
import com.netease.nim.demo.redenvelope.util.UrlUtil;
import com.netease.nim.demo.redenvelope.widget.SerializableJSONObject;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.session.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.activity.TeamMessageActivity;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bangzhu on 2016/7/22.
 */
public class RedEnvelopeAction extends BaseAction {
    private static final String KEY_TYPE = "type";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_SEDER_ID = "sender_id";
    private static final String KEY_GROUP_ID = "group_id";

    private JSONObject jsonData;

    public RedEnvelopeAction() {
        super(R.drawable.apw, R.string.red_envelope);
    }

    private void createRedEnvelope(){
        Intent intent = new Intent();
        intent.putExtra("SessionType", getSessionType().toString());
        intent.setClass(getActivity(), CreateRedEnvelopeActivity.class);
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

                    if(getSessionType() == SessionTypeEnum.P2P){
                        //创建单聊红包
                        sendP2PRedenvelope(data);
                    }else if(getSessionType() == SessionTypeEnum.Team){
                        //创建群聊红包
                        sendTeamRedenvelope(data);
//                        attachment = new RedEnvelopeAttachment(total+"", left_message, packet_id+"");
                    }
                }
                break;
        }
    }

    private void sendP2PRedenvelope(Intent data){
        String left_message = data.getExtras().getString("left_message");
        String lucky_money = data.getExtras().getString("lucky_money");
        RedEnvelopeAttachment attachment = new RedEnvelopeAttachment(lucky_money, left_message, 0+"");
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
            extendData.put("messageId", message.getUuid());
            message.setRemoteExtension(extendData);
        }
        sendMessage(message);
    }

    private void sendTeamRedenvelope(Intent data){
        int type = data.getExtras().getInt("isNormal");
        int quantity = Integer.parseInt(data.getExtras().getString("redenvelope_num"));
        float total;
        if(type == 0){
            total = Float.parseFloat(data.getExtras().getString("total_money"))*quantity;
        }else{
            total = Float.parseFloat(data.getExtras().getString("total_money"));
        }
        String message = data.getExtras().getString("left_message");
        String sessionid = getActivity().getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        Team team = TeamDataCache.getInstance().getTeamById(sessionid);
        int group_id = Integer.parseInt(team.getId().trim());
        String sender_id = DemoCache.getAccount();

        jsonData = new SerializableJSONObject();
        try {
            jsonData.put(KEY_TYPE, type);
            jsonData.put(KEY_QUANTITY, quantity);
            jsonData.put(KEY_TOTAL, total);
            jsonData.put(KEY_MESSAGE, message);
            jsonData.put(KEY_SEDER_ID, sender_id);
            jsonData.put(KEY_GROUP_ID, group_id);
            Log.i("HZWING", jsonData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //使用Volley进行网络加载
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlUtil.getUrl("PacketPostServlet"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int packet_id = jsonObject.getInt("data");
                            RedEnvelopeAttachment attachment = new RedEnvelopeAttachment(jsonData.getDouble("total")+"", jsonData.getString("message"), packet_id+"");
                            CustomMessageConfig config = new CustomMessageConfig();
                            String groupId = getActivity().getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
                            IMMessage message = MessageBuilder.createCustomMessage(groupId, getSessionType(), "红包", attachment, config);
                            sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String,String>();
                map.put("packetJson", jsonData.toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);
//        RequestParams params = new RequestParams();
//        params.put("packetJson", jsonData.toString());
//        AsyncHttpUtil.post("PacketPostServlet", params, new AsyncHttpResponseHandler(){
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                try {
//                    String response = new String(bytes);
//                    JSONObject jsonObject = new JSONObject(response);
//                    int packet_id = jsonObject.getInt("data");
//                    Log.i("HZWING", "红包id："+packet_id+"==========");
//                    RedEnvelopeAttachment attachment = new RedEnvelopeAttachment(jsonData.getDouble("total") + "", jsonData.getString("message"), packet_id + "");
//                    CustomMessageConfig config = new CustomMessageConfig();
//                    String groupId = getActivity().getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
//                    IMMessage message = MessageBuilder.createCustomMessage(groupId, getSessionType(), "红包", attachment, config);
//                    getContainer().proxy.sendMessage(message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//
//            }
//        });
    }
}
