package com.netease.nim.demo.RedEnvelope;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;

/**
 * Created by bangzhu on 2016/7/22.
 */
public class CreateRedEnvelopeActivity extends UI implements View.OnClickListener {

    private LinearLayout back;
    private EditText et_lucky_money, et_left_message;
    private TextView tv_final_money, tv_create_red_envelope, actionBarTile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_envelope_create);
        initView();
        setContent();
        setListener();
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        actionBarTile = (TextView) findViewById(R.id.title);
        et_lucky_money = (EditText) findViewById(R.id.et_lucky_money);
        et_left_message = (EditText) findViewById(R.id.et_left_message);
        tv_final_money = (TextView) findViewById(R.id.tv_final_money);
        tv_create_red_envelope = (TextView) findViewById(R.id.tv_create_red_envelope);
    }

    private void setContent(){
        actionBarTile.setText("发红包");
    }

    private void setListener() {
        back.setOnClickListener(this);
        et_lucky_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = et_lucky_money.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    double money = Double.parseDouble(text);
                    tv_final_money.setText("￥" + money);
                } else {
                    tv_final_money.setText("￥0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_create_red_envelope.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.back :
                finish();
                break;
            case R.id.tv_create_red_envelope :
                bundle.putString("lucky_money", et_lucky_money.getText().toString());
                bundle.putString("left_message", et_left_message.getText().toString());
                intent.putExtras(bundle);
                CreateRedEnvelopeActivity.this.setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}