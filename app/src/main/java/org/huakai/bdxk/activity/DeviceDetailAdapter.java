package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.huakai.bdxk.R;
import org.huakai.bdxk.db.DevicesCollectionHelper;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/25.
 */

public class DeviceDetailAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private ArrayList<String> menus = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;
    private  static  DevicesCollectionHelper devicesHelper;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public DeviceDetailAdapter(Context context, ArrayList<String> _menus) {
        menus = _menus;
        devicesHelper =  new DevicesCollectionHelper(context);
        devicesHelper.open();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_menu_item, parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String name = menus.get(position);
        ((MyViewHolder)holder).tv.setText(name);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_num);
        }
    }
}
