package com.netease.nim.demo.redenvelope.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * Created by bangzhu on 2016/7/22.
 */
public class CreateRedEnvelopeActivity extends UI implements View.OnClickListener, TextWatcher{

    private LinearLayout back;
    private TextView finalMoney, createRedenvelope, actionBarTile;
    //单聊红包视图
    private EditText luckyMoney, leftMessage;
    private LinearLayout p2pRedenvelope;
    //群聊红包视图
    private LinearLayout teamRedenvelope;
    private EditText redenvelopeNum;
    private EditText totalMoney;
    private TextView moneyTip, typeTip, redenvelopeType;
    private int isNormal = 0;

    private SessionTypeEnum mSessionType;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_envelope_create);
        mSessionType = SessionTypeEnum.valueOf(getIntent().getStringExtra("SessionType"));
        initView();
        setContent();
        setListener();
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        actionBarTile = (TextView) findViewById(R.id.title);
        finalMoney = (TextView) findViewById(R.id.tv_final_money);
        createRedenvelope = (TextView) findViewById(R.id.tv_create_red_envelope);
        //单聊红包
        luckyMoney = (EditText) findViewById(R.id.et_lucky_money);
        leftMessage = (EditText) findViewById(R.id.et_left_message);
        p2pRedenvelope = (LinearLayout) findViewById(R.id.ll_lucky_money);
        //群聊红包
        teamRedenvelope = (LinearLayout) findViewById(R.id.ll_team_redenvelope);
        redenvelopeNum = (EditText) findViewById(R.id.et_redenvelope_num);
        totalMoney = (EditText) findViewById(R.id.et_total_money);
        moneyTip = (TextView) findViewById(R.id.tv_money_tip);
        typeTip = (TextView) findViewById(R.id.tv_type_tip);
        redenvelopeType = (TextView) findViewById(R.id.tv_redenvelope_type);

    }

    private void setContent(){
        actionBarTile.setText("发红包");
        if(mSessionType == SessionTypeEnum.P2P){
            p2pRedenvelope.setVisibility(View.VISIBLE);
            teamRedenvelope.setVisibility(View.GONE);
        }else if(mSessionType == SessionTypeEnum.Team){
            p2pRedenvelope.setVisibility(View.GONE);
            teamRedenvelope.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        back.setOnClickListener(this);
        luckyMoney.addTextChangedListener(this);
        totalMoney.addTextChangedListener(this);
        createRedenvelope.setOnClickListener(this);
        redenvelopeType.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        String lucky_money = luckyMoney.getText().toString();
        String left_message = leftMessage.getText().toString();
        String redenvelope_num = redenvelopeNum.getText().toString();
        String total_money = totalMoney.getText().toString();
        switch(v.getId()){
            case R.id.back :
                finish();
                break;
            case R.id.tv_redenvelope_type:
                if(isNormal == 1){
                    moneyTip.setText(getResources().getText(R.string.redenvelope_total_money));
                    typeTip.setText(getResources().getText(R.string.redenvelope_random_tip));
                    redenvelopeType.setText(getResources().getText(R.string.redenvelope_normal));
                    isNormal = 0;
                }else if(isNormal == 0){
                    moneyTip.setText(getResources().getText(R.string.redenvelope_per_money));
                    typeTip.setText(getResources().getText(R.string.redenvelope_normal_tip));
                    redenvelopeType.setText(getResources().getText(R.string.redenvelope_random));
                    isNormal = 1;
                }
                break;

            case R.id.tv_create_red_envelope :
                if(TextUtils.isEmpty(left_message)){
                    left_message = "恭喜发财，大吉大利！";
                }
                if(mSessionType == SessionTypeEnum.P2P){
                    bundle.putString("lucky_money", lucky_money);
                    bundle.putString("left_message", left_message);
                }else if(mSessionType == SessionTypeEnum.Team){
                    bundle.putString("redenvelope_num", redenvelope_num);
                    bundle.putString("total_money", total_money);
                    bundle.putString("left_message", left_message);
                }
                bundle.putInt("isNormal", isNormal);
                intent.putExtras(bundle);
                CreateRedEnvelopeActivity.this.setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text="";
        if(mSessionType == SessionTypeEnum.P2P){
            text = luckyMoney.getText().toString();
        }else if(mSessionType == SessionTypeEnum.Team){
            text = totalMoney.getText().toString();
        }
        if (!TextUtils.isEmpty(text)) {
            double money = Double.parseDouble(text);
            finalMoney.setText("￥" + money);
        } else {
            finalMoney.setText("￥0.00");
        }
    }
    @Override
    public void afterTextChanged(Editable s) {

    }
}


