package org.huakai.bdxk.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.huakai.bdxk.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView content;
    public TextView mac;
    public TextView delete;
    public LinearLayout layout;

    public ImageView bluetooth;
    public TextView id_rssi;

    public MyViewHolder(View itemView) {
        super(itemView);
        content = (TextView) itemView.findViewById(R.id.item_content);
        mac  = (TextView) itemView.findViewById(R.id.item_mac);
        delete = (TextView) itemView.findViewById(R.id.item_delete);
        layout = (LinearLayout) itemView.findViewById(R.id.item_layout);
        bluetooth = (ImageView) itemView.findViewById(R.id.icon_bluetooth);
        id_rssi = (TextView) itemView.findViewById(R.id.id_rssi);
    }
}
