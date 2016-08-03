package com.netease.nim.demo.RedEnvelope;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * Created by bangzhu on 2016/7/29.
 */
public class RedEnvelopeDetailActivity extends UI implements View.OnClickListener{

    private LinearLayout back;
    private TextView actionBarTitle;
    //发送者视图
    private LinearLayout red_envelope_record;
    private RelativeLayout record_item;
    private TextView tv_redenvelope_tip,tv_friend_name, tv_receive_time, receive_money;
    private ImageView iv_friend_img;
    //接收者视图
    private RelativeLayout left_message_panel;
    private TextView friendName, friendMessage, receiveMoney;

    private IMMessage mMessage;
    private String mRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_envelope_detail);
        Bundle bundle = getIntent().getExtras();
        mMessage = (IMMessage) bundle.getSerializable("message");
        mRole = bundle.getString("role");
        initView();
        setContent();
        setListener();
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        actionBarTitle = (TextView) findViewById(R.id.title);
        //发送者视图
        red_envelope_record = (LinearLayout) findViewById(R.id.red_envelope_record);
        record_item = (RelativeLayout) findViewById(R.id.record_item);
        tv_redenvelope_tip = (TextView) findViewById(R.id.tv_redenvelope_tip);
        tv_friend_name = (TextView) findViewById(R.id.tv_friend_name);
        tv_receive_time = (TextView) findViewById(R.id.tv_receive_time);
        receive_money = (TextView) findViewById(R.id.receive_money);
        iv_friend_img = (ImageView) findViewById(R.id.iv_friend_img);
        //接收者视图
        left_message_panel = (RelativeLayout) findViewById(R.id.left_message_panel);
        friendName = (TextView) findViewById(R.id.friend_name);
        friendMessage = (TextView) findViewById(R.id.friend_message);
        receiveMoney = (TextView) findViewById(R.id.tv_receive_money);

    }

    private void setContent(){
        actionBarTitle.setText("红包详情");
        RedEnvelopeAttachment attachment = (RedEnvelopeAttachment) mMessage.getAttachment();
        Map<String, Object> localExtension = mMessage.getLocalExtension();
        String message = attachment.getLeftMessage();
        String money = attachment.getLuckyMoney();
        friendName.setText(mMessage.getFromNick());
        friendMessage.setText(message);
        if(mRole.equals("receiver")){
            left_message_panel.setVisibility(View.VISIBLE);
            red_envelope_record.setVisibility(View.GONE);
            receiveMoney.setText(money);
        }else{
            left_message_panel.setVisibility(View.GONE);
            red_envelope_record.setVisibility(View.VISIBLE);
            if(localExtension == null){
                tv_redenvelope_tip.setText("红包金额" + money + "元，等待对方领取");
                record_item.setVisibility(View.GONE);
            }else{
                tv_redenvelope_tip.setText("一个红包共"+money+"元");
                record_item.setVisibility(View.VISIBLE);
                tv_friend_name.setText(localExtension.get("messageId").toString().split("_")[0]);
                receive_money.setText(money);
            }
        }
    }

    private void setListener(){
        back.setOnClickListener(this);
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
