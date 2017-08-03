package org.huakai.bdxk;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/25.
 */

public class DeviceListAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private ArrayList<BluetoothDevice> ScanResultss = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

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


    public DeviceListAdapter(ArrayList<BluetoothDevice> _ScanResultss) {
        ScanResultss = _ScanResultss;
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
        BluetoothDevice result = ScanResultss.get(position);
        String name = result.getName();
        if(name==null || "".equals(name))
            name = result.getAddress();
        ((MyViewHolder)holder).tv.setText(name);
        ((MyViewHolder)holder).id_rssi.setText(result.describeContents()+"");
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
