package com.netease.nim.demo.redenvelope.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Map;

/**
 * Created by bangzhu on 2016/7/29.
 */
public class RedEnvelopeDetailActivity extends UI implements View.OnClickListener{

    private LinearLayout back;
    private TextView actionBarTitle;
    private HeadImageView senderImage;
    private TextView senderName, senderMessage;
    
    //发送者视图
    private LinearLayout redenvelopeRecord;
    private RelativeLayout recordItem;
    private TextView redenvelopeTip,receiverName, receiverTime, receiverMoney;
    private HeadImageView receiverImage;
    //接收者视图
    private RelativeLayout left_message_panel;
    private TextView receiveMoney;

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
        senderImage = (HeadImageView) findViewById(R.id.iv_sender_image);
        senderName = (TextView) findViewById(R.id.tv_sender_name);
        senderMessage = (TextView) findViewById(R.id.tv_sender_message);

        //发送者视图
        redenvelopeRecord = (LinearLayout) findViewById(R.id.ll_redenvelope_record);
        recordItem = (RelativeLayout) findViewById(R.id.rl_record_item);
        redenvelopeTip = (TextView) findViewById(R.id.tv_redenvelope_tip);
        receiverName = (TextView) findViewById(R.id.tv_receiver_name);
        receiverTime = (TextView) findViewById(R.id.tv_receive_time);
        receiverMoney = (TextView) findViewById(R.id.tv_receiver_money);
        receiverImage = (HeadImageView) findViewById(R.id.iv_receiver_image);

        //接收者视图
        left_message_panel = (RelativeLayout) findViewById(R.id.left_message_panel);
        receiveMoney = (TextView) findViewById(R.id.tv_receive_money);

    }

    private void setContent(){
        int isOpen;
        actionBarTitle.setText("红包详情");
        RedEnvelopeAttachment attachment = (RedEnvelopeAttachment) mMessage.getAttachment();
        String message = attachment.getLeftMessage();
        String money = attachment.getLuckyMoney();
        senderImage.loadBuddyAvatar(mMessage.getFromAccount());
        senderName.setText(mMessage.getFromNick());
        senderMessage.setText(message);
        Map<String, Object> localExtension = mMessage.getLocalExtension();
        if(localExtension != null){
            isOpen = Integer.parseInt(localExtension.get("isOpen").toString());
        }else{
            isOpen = 0;
        }
        //根据角色显示界面
        if(mRole.equals("receiver")){
            left_message_panel.setVisibility(View.VISIBLE);
            redenvelopeRecord.setVisibility(View.GONE);
            receiveMoney.setText(money);
        }else{
            left_message_panel.setVisibility(View.GONE);
            redenvelopeRecord.setVisibility(View.VISIBLE);

            if(isOpen == 0){
                redenvelopeTip.setText("红包金额" + money + "元，等待对方领取");
                recordItem.setVisibility(View.GONE);
            }else{
                redenvelopeTip.setText("一个红包共" + money + "元");
                recordItem.setVisibility(View.VISIBLE);
                receiverImage.loadBuddyAvatar(localExtension.get("messageId").toString().split("_")[0]);
                receiverName.setText(localExtension.get("messageId").toString().split("_")[0]);
                receiveMoney.setText(money);
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
