package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import org.huakai.bdxk.R;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.view.CustomLoadView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/4.
 */

public class DeviceDetailActivity  extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<String> menus = new ArrayList<>();
    private DeviceDetailAdapter adapter;
    private RefreshLayout refreshLayout;
    private Context mContext;
    private BluetoothHelperService mChatService;
    private BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initMenu();
        initView();
        initListener();
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mChatService = new BluetoothHelperService(this, mHandler);
        mChatService.connect(device,false);
        CustomLoadView.getInstance(this).showProgress("正在连接设备");
    }

    private void initView(){
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceDetailAdapter(this,menus);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
    }

    private void initListener(){
        adapter.setOnItemClickListener(new DeviceDetailAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
            onMenuClick(position);
            }
        });
    }

    private void onMenuClick(int position){
        switch (position){
            case 0:
            default:
                sendCmd(ByteUtils.getCmdHexStr("01"));
                break;
            case 1:
                sendCmd(ByteUtils.getCmdHexStr("10"));
                break;
            case 2:
                sendCmd(ByteUtils.getCmdHexStr("80"));
                break;
            case 3:
                sendCmd(ByteUtils.getCmdHexStr("81"));
                break;
            case 4:
                sendCmd(ByteUtils.getCmdHexStr("82"));
                break;
            case 5:
                sendCmd(ByteUtils.getCmdHexStr("84"));
                break;
            case 6:
                sendCmd(ByteUtils.getCmdHexStr("05"));
                break;
        }
        Toast.makeText(mContext, menus.get(position), Toast.LENGTH_SHORT).show();
    }

    private void initMenu(){
        menus.add("传感器信息");
        menus.add("采集数据");
        menus.add("传感器调零");
        menus.add("设置备注信息");
        menus.add("设置自编号");
        menus.add("设置型号");
        menus.add("设置标定信息");
        menus.add("读取标定信息");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    private void sendCmd(String orderHex){
        CustomLoadView.getInstance(this).showProgress("正在发送请求");
        byte[] data = ByteUtils.hexStringToBytes(orderHex);
        mChatService.write(data);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomLoadView.getInstance(DeviceDetailActivity.this).dismissProgress();
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    Toast.makeText(DeviceDetailActivity.this, "设备已连接", Toast.LENGTH_SHORT).show();
                    break;
                case MessageType.MESSAGE_READ:
                    RespondDecoder decoder = new RespondDecoder(msg.obj.toString());
                    Toast.makeText(mContext, decoder.getResult(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
