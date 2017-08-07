package org.huakai.bdxk;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.huakai.bdxk.common.ScanResult;
import org.huakai.bdxk.db.DevicesCollectionHelper;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/25.
 */

public class DeviceListAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private ArrayList<ScanResult> ScanResultss = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;
    private  static  DevicesCollectionHelper devicesHelper;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public DeviceListAdapter(Context context, ArrayList<ScanResult> _ScanResultss) {
        ScanResultss = _ScanResultss;
        devicesHelper =  new DevicesCollectionHelper(context);
        devicesHelper.open();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_drivce_list_item, parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ScanResult result = ScanResultss.get(position);
        String name = devicesHelper.getDescByMac(result.getDevice().getAddress());
        if(name==null || "".equals(name)){
            name = result.getDevice().getName();
            if(name==null || "".equals(name)){
                name = result.getDevice().getAddress();
            }
        }
        ((MyViewHolder)holder).tv.setText(name);
        ((MyViewHolder)holder).id_rssi.setText(result.getRssi()+"");
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return ScanResultss.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public TextView id_rssi;
        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_num);
            id_rssi = (TextView) view.findViewById(R.id.id_rssi);
        }
    }
}
