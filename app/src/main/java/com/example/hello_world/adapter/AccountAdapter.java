package com.example.hello_world.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.R;

import java.util.List;

public class AccountAdapter extends BaseAdapter {

    Context context;
    List<AccountIn>mDatas;
    LayoutInflater inflater;

    public AccountAdapter(Context context,List<AccountIn>mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = inflater.inflate(R.layout.item_mainlv,viewGroup,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        AccountIn accountIn = mDatas.get(i);
        holder.typeIv.setImageResource(accountIn.getFocusImageID());
        holder.typeTv.setText(accountIn.getTypename());
        holder.noteTv.setText(accountIn.getNote());
        holder.timeTv.setText(accountIn.getTime());
        holder.studyTv.setText(String.valueOf(accountIn.getStudyTime()));
        return view;
    }

    class ViewHolder{
        ImageView typeIv;
        TextView typeTv,noteTv,timeTv,studyTv;
        public ViewHolder(View view){
            typeIv= view.findViewById(R.id.item_mainlv_iv);
            typeTv=view.findViewById(R.id.item_mainlv_tv_title);
            timeTv=view.findViewById(R.id.item_mainlv_tv_time);
            studyTv=view.findViewById(R.id.item_mainlv_tv_money);
            noteTv=view.findViewById(R.id.item_mainlv_tv_beizhu);
        }
    }
}
