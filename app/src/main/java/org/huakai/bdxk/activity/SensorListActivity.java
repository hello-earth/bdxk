package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.view.OnItemClickListener;
import org.huakai.bdxk.view.SwipeRecyclerView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/4.
 */

public class SensorListActivity extends AppCompatActivity {
    SwipeRecyclerView mRecyclerView ;
    private ArrayList<SensorBean> sensorList = new ArrayList<>();
    private SensorAdapter adapter;
    private RefreshLayout refreshLayout;
    private Context mContext;
    private BluetoothDevice device;
    private LinearLayout headBackLayout;
    private ImageView titleLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        mContext = this;
        initMenu();
        initView();
        initListener();
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    }

    private void initView(){
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        mRecyclerView = (SwipeRecyclerView) findViewById(R.id.recyclerview);
        ((TextView)findViewById(R.id.head_title)).setText("传感器列表");
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        (findViewById(R.id.com_head_add_layout)).setVisibility(View.VISIBLE);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SensorAdapter(this, sensorList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
    }

    private void initListener(){
        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                nextActivity(device, sensorList.get(position).getSensorId());
            }
            @Override
            public void onDeleteClick(int position) {
                adapter.removeItem(position);
            }
        });
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorListActivity.this.finish();
            }
        });
        headBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorListActivity.this.finish();
            }
        });
    }

    private void initMenu(){
        sensorList.add(new SensorBean("温度传感器1","2820946508000034"));
        sensorList.add(new SensorBean("温度传感器2","2820946508000035"));
        sensorList.add(new SensorBean("温度传感器3","2820946508000036"));
        sensorList.add(new SensorBean("温度传感器4","2820946508000037"));
        sensorList.add(new SensorBean("温度传感器5","2820946508000038"));
        sensorList.add(new SensorBean("温度传感器6","2820946508000039"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void nextActivity(BluetoothDevice device, String sensorid){
        Intent intent = new Intent();
        intent.setClass(SensorListActivity.this, DeviceDetailActivity.class);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE,device);
        intent.putExtra(BluetoothDevice.EXTRA_NAME,sensorid);
        startActivity(intent);
    }

}
