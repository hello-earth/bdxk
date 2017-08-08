package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.view.CustomLoadView;
import org.huakai.bdxk.view.OnItemClickListener;
import org.huakai.bdxk.view.SwipeRecyclerView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/4.
 */

public class SensorListActivity extends AppCompatActivity {
    private SwipeRecyclerView mRecyclerView ;
    private BluetoothHelperService mChatService;
    private ArrayList<SensorBean> sensorList = new ArrayList<>();
    private SensorAdapter adapter;
    private RefreshLayout refreshLayout;
    private Context mContext;
    private BluetoothDevice device;
    private LinearLayout headBackLayout;
    private ImageView titleLeft;

    private LinearLayout headAddLayout;
    private ImageView addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        mContext = this;
        initMenu();
        initView();
        initListener();
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mChatService = BluetoothHelperService.getInstance(this, mHandler);
        if(!mChatService.isConnected() && device!=null){
            mChatService.connect(device,false);
            CustomLoadView.getInstance(this).showProgress("正在连接设备");
        }
    }

    private void initView(){
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        mRecyclerView = (SwipeRecyclerView) findViewById(R.id.recyclerview);
        ((TextView)findViewById(R.id.head_title)).setText("传感器列表");
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        headAddLayout = (LinearLayout)findViewById(R.id.com_head_add_layout);
        addButton = (ImageView)findViewById(R.id.com_head_add);
        headAddLayout.setVisibility(View.VISIBLE);
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
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddcaitonActivity();
            }
        });
        headAddLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddcaitonActivity();
            }
        });
    }

    private void initMenu(){
        sensorList.add(new SensorBean(device.getAddress(),"2820946508000034","温度传感器1"));
        sensorList.add(new SensorBean(device.getAddress(),"2820946508000035","温度传感器2"));
        sensorList.add(new SensorBean(device.getAddress(),"2820946508000036","温度传感器3"));
        sensorList.add(new SensorBean(device.getAddress(),"2820946508000037","温度传感器4"));
        sensorList.add(new SensorBean(device.getAddress(),"2820946508000038","温度传感器5"));
        sensorList.add(new SensorBean(device.getAddress(),"2820946508000039","温度传感器6"));
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

    private void startAddcaitonActivity(){
        Intent intent = new Intent();
        intent.setClass(SensorListActivity.this, SensorInputActivity.class);
        startActivity(intent);
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomLoadView.getInstance(SensorListActivity.this).dismissProgress();
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    Toast.makeText(SensorListActivity.this, "设备已连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
