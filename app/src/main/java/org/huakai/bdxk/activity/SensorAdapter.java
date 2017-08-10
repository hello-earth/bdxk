package org.huakai.bdxk.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.view.MyViewHolder;

import java.util.ArrayList;

public class SensorAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<SensorBean> mList;

    public SensorAdapter(Context context, ArrayList<SensorBean> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.recyclerview_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.content.setText(mList.get(position).getSensorName());
        viewHolder.mac.setText(mList.get(position).getSensorId());
        viewHolder.bluetooth.setVisibility(View.GONE);
        viewHolder.id_rssi.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

}
