package com.netease.nim.demo.redenvelope.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.redenvelope.model.Opener;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

/**
 * Created by GeiLe on 2016/8/4.
 */
public class RedenvelopeListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Opener> openerList;

    public RedenvelopeListAdapter(Context mContext, List<Opener> openerList) {
        this.mContext = mContext;
        this.openerList = openerList;
    }

    @Override
    public int getCount() {
        return openerList.size();
    }

    @Override
    public Object getItem(int position) {
        return openerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.redenvelope_openerlist_item, null);
            viewHolder.receiverImg = (HeadImageView) convertView.findViewById(R.id.iv_receiver_image);
            viewHolder.receiverName = (TextView) convertView.findViewById(R.id.tv_receiver_name);
            viewHolder.receiveTime = (TextView) convertView.findViewById(R.id.tv_receive_time);
            viewHolder.receiveMoney = (TextView) convertView.findViewById(R.id.tv_receiver_money);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Opener opener = openerList.get(position);
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(opener.getUser_id()+"");
        viewHolder.receiverImg.loadBuddyAvatar(opener.getUser_id()+"");
        viewHolder.receiverName.setText(userInfo.getName());
        viewHolder.receiveTime.setText(opener.getCreated_at());
        viewHolder.receiveMoney.setText(opener.getMoney()+"");
        return convertView;
    }

    class ViewHolder{
        public HeadImageView receiverImg;
        public TextView receiverName;
        public TextView receiveTime;
        public TextView receiveMoney;
    }
}
