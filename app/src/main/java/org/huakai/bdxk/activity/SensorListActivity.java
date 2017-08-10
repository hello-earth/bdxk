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
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.db.SensorCollectionHelper;
import org.huakai.bdxk.view.CustomLoadView;
import org.huakai.bdxk.view.OnItemClickListener;
import org.huakai.bdxk.view.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

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
    private SensorCollectionHelper sensorHelper;
    private LinearLayout headAddLayout;
    private ImageView addButton;
    private static List<SensorBean> sensorBeens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        mContext = this;
        initView();
        initListener();
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mChatService = BluetoothHelperService.getInstance(this, mHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMenu();
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
                nextActivity(device, sensorList.get(position).getSensorId(), sensorList.get(position).getSensorName());
            }
            @Override
            public void onDeleteClick(int position) {
                onSensorDelete(position);
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
        if(!mChatService.isConnected() && device!=null){
            mChatService.connect(device,false);
            CustomLoadView.getInstance(this).showProgress("正在连接设备");
        }else{
            CustomLoadView.getInstance(this).showProgress("");
        }
        sensorList.clear();
        sensorHelper =  new SensorCollectionHelper(this);
        sensorHelper.open();
        sensorBeens =sensorHelper.getAllSensors(device.getAddress());
        for(SensorBean sensor : sensorBeens)
            sensorList.add(sensor);
        adapter.notifyDataSetChanged();
        CustomLoadView.getInstance(this).dismissProgress();
    }

    private void onSensorDelete(int position){
        sensorHelper.delete(sensorList.get(position).getSensorId());
        adapter.removeItem(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void nextActivity(BluetoothDevice device, String sensorid, String desc){
        Intent intent = new Intent();
        intent.setClass(SensorListActivity.this, SensorDetailActivity.class);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE,device);
        intent.putExtra(BluetoothDevice.EXTRA_NAME,sensorid);
        intent.putExtra("extra_desc",desc);
        startActivity(intent);
    }

    private void startAddcaitonActivity(){
        Intent intent = new Intent();
        intent.setClass(SensorListActivity.this, SensorInputActivity.class);
        intent.putExtra("device_mac",device.getAddress());
        startActivity(intent);
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomLoadView.getInstance(SensorListActivity.this).dismissProgress();
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    ToastUtil.makeTextAndShow("设备已连接");
                    break;
            }
        }
    };

}
