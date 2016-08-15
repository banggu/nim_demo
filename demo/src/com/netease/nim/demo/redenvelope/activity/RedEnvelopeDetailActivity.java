package com.netease.nim.demo.redenvelope.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.netease.nim.demo.R;
import com.netease.nim.demo.redenvelope.adapter.RedenvelopeListAdapter;
import com.netease.nim.demo.redenvelope.model.Opener;
import com.netease.nim.demo.redenvelope.util.JSonUtil;
import com.netease.nim.demo.redenvelope.util.UrlUtil;
import com.netease.nim.demo.session.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bangzhu on 2016/7/29.
 */
public class RedEnvelopeDetailActivity extends UI implements View.OnClickListener,Serializable{

    private LinearLayout back;
    private TextView actionBarTitle;
    private HeadImageView senderImage;
    private TextView senderName, senderMessage;
    
    //发送者视图
    private LinearLayout redenvelopeRecord;
    private ListView openerListView;
    private TextView redenvelopeTip;

    private RedenvelopeListAdapter redenvelopeListAdapter;
    private List<Opener> openerList;
    //接收者视图
    private RelativeLayout left_message_panel;
    private TextView receiveMoney;

    private IMMessage mMessage;
    private String mRole;
    private String mUserId;
    private String mOpenTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_envelope_detail);
        Bundle bundle = getIntent().getExtras();
        mMessage = (IMMessage) bundle.getSerializable("message");
        mRole = bundle.getString("role");
        mUserId = bundle.getString("userId");
        mOpenTime = bundle.getString("openTime");
        initView();
        bindView();
        setContent();
        setListener();
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        actionBarTitle = (TextView) findViewById(R.id.title);
        senderImage = (HeadImageView) findViewById(R.id.iv_sender_image);
        senderName = (TextView) findViewById(R.id.tv_sender_name);
        senderMessage = (TextView) findViewById(R.id.tv_sender_message);

        //发送者视图
        redenvelopeRecord = (LinearLayout) findViewById(R.id.ll_redenvelope_record);
        redenvelopeTip = (TextView) findViewById(R.id.tv_redenvelope_tip);
        openerListView = (ListView) findViewById(R.id.lv_opener_list);

        //接收者视图
        left_message_panel = (RelativeLayout) findViewById(R.id.left_message_panel);
        receiveMoney = (TextView) findViewById(R.id.tv_receive_money);

    }

    private void bindView(){
        openerList = new ArrayList<>();
        redenvelopeListAdapter = new RedenvelopeListAdapter(RedEnvelopeDetailActivity.this, openerList);
        openerListView.setAdapter(redenvelopeListAdapter);
    }

    private void setContent(){
        int isOpen;
        actionBarTitle.setText("红包详情");
        //获取领取红包者列表
        RedEnvelopeAttachment attachment = (RedEnvelopeAttachment) mMessage.getAttachment();
        String message = attachment.getLeftMessage();
        String money = attachment.getLuckyMoney();
        senderImage.loadBuddyAvatar(mMessage.getFromAccount());
        senderName.setText(mMessage.getFromNick());
        senderMessage.setText(message);
        if(mMessage.getSessionType() == SessionTypeEnum.P2P){
            getP2POpenerList(attachment);
            Map<String, Object> localExtension = mMessage.getLocalExtension();
            if(localExtension != null){
                isOpen = Integer.parseInt(localExtension.get("isOpen").toString());
            }else{
                isOpen = 0;
            }
        }else if(mMessage.getSessionType() == SessionTypeEnum.Team){
            getTeamOpenerList(attachment);
            isOpen = 1;
        }else{
            isOpen = 0;
        }
        //根据角色显示界面
        if(mRole.equals("receiver")){
            left_message_panel.setVisibility(View.VISIBLE);
            redenvelopeRecord.setVisibility(View.GONE);
            receiveMoney.setText(money+"");
        }else{
            left_message_panel.setVisibility(View.GONE);
            redenvelopeRecord.setVisibility(View.VISIBLE);

            if(isOpen == 0){
                redenvelopeTip.setText("红包金额" + money+"" + "元，等待对方领取");
                openerListView.setVisibility(View.GONE);
            }else{
                redenvelopeTip.setText("一个红包共" + money+"" + "元");
                openerListView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setListener(){
        back.setOnClickListener(this);
    }

    private void getP2POpenerList(RedEnvelopeAttachment attachment) {
        String money = attachment.getLuckyMoney();
        Opener opener = new Opener();
        opener.setUser_id(mUserId);
        opener.setCreated_at(mOpenTime);
        opener.setMoney(Float.parseFloat(money));
        openerList.add(opener);
        redenvelopeListAdapter.notifyDataSetChanged();

    }

    private void getTeamOpenerList(RedEnvelopeAttachment attachment){
        String packet_id = attachment.getPacketId();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String relativeUrl = "PacketShowServlet?id="+packet_id;
        String url = UrlUtil.getUrl(relativeUrl);
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("HZWING", "打开者列表"+response+"============");
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            Log.i("HZWING", "-----" + jsonArray.toString() + "------------");
                            openerList.addAll(JSonUtil.getListFromJson(jsonArray.toString(), Opener.class));
                            redenvelopeListAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
