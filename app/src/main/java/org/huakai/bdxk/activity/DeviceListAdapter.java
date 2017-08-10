package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.ScanResult;
import org.huakai.bdxk.db.DevicesCollectionHelper;
import org.huakai.bdxk.view.MyViewHolder;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/25.
 */

public class DeviceListAdapter extends RecyclerView.Adapter {

    private ArrayList<ScanResult> ScanResultss = new ArrayList<>();
    private  static  DevicesCollectionHelper devicesHelper;
    private LayoutInflater mInflater;

    public DeviceListAdapter(Context context, ArrayList<ScanResult> _ScanResultss) {
        mInflater = LayoutInflater.from(context);
        ScanResultss = _ScanResultss;
        devicesHelper =  new DevicesCollectionHelper(context);
        devicesHelper.open();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.recyclerview_item_layout, parent, false));
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
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.content.setText(name);
        viewHolder.mac.setText(result.getDevice().getAddress());
        viewHolder.id_rssi.setText(result.getRssi()+"");
        viewHolder.delete.setText("重命名");
    }

    @Override
    public int getItemCount() {
        return ScanResultss.size();
    }

    public void renameItem() {
        notifyDataSetChanged();
    }
}
