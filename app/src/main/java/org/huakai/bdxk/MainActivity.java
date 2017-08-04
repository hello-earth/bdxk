package org.huakai.bdxk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.db.DevicesCollectionHelper;
import org.huakai.bdxk.view.CustomLoadView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private ArrayList<BluetoothDevice> ScanResults = new ArrayList<>();
    private DeviceListAdapter adapter;
    private RefreshLayout refreshLayout;
    private Context mContext;
    private BluetoothAdapter mBtAdapter;
    private BluetoothHelperService mChatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceListAdapter(this,ScanResults);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        refreshLayout.setEnableLoadmore(false);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mChatService = new BluetoothHelperService(this, mHandler);

        initListener();
        if(mBtAdapter.isEnabled())
            doDiscovery();
        else
            Toast.makeText(mContext,"请先打开蓝牙开关",Toast.LENGTH_SHORT).show();
    }

    private void initListener(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        this.registerReceiver(mReceiver, filter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLayout.finishRefresh(10000);
                doDiscovery();
            }
        });
        adapter.setOnItemClickListener(new DeviceListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                mChatService.stop();
                CustomLoadView.getInstance(MainActivity.this).showProgress();
                mChatService.connect(ScanResults.get(position), false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ScanResults.add(device);
                adapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                refreshLayout.finishRefresh();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                ScanResults.clear();
                adapter.notifyDataSetChanged();
            }
        }
    };


    private void onInputDeviceName(final Object obj){
        final BluetoothDevice device = (BluetoothDevice)obj;
        final DevicesCollectionHelper devicesHelper =  new DevicesCollectionHelper(getApplicationContext());
        devicesHelper.open();
        if(devicesHelper.isHasDeviceInfo(device,"")!=-1) {
            new CircleDialog.Builder(MainActivity.this)
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(true)
                    .setTitle("初始化节点名称")
                    .setInputHint("请该设备节点备注")
                    .configInput(new ConfigInput() {
                        @Override
                        public void onConfig(InputParams params) {
                        }
                    })
                    .setNegative("取消", null)
                    .setPositiveInput("确定", new OnInputClickListener() {
                        @Override
                        public void onClick(String text, View v) {
                            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                            long flag = devicesHelper.insertNewsCollectionInfo(device, text);
                            if (flag == -1) {
                                Toast.makeText(mContext, "该节点已在数据库中存在", Toast.LENGTH_SHORT).show();
                            } else if (flag == -2) {
                                Toast.makeText(mContext, "该备注已存在，请保证备注名称的唯一性", Toast.LENGTH_SHORT).show();
                            }else if(flag==1){
                                Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        }
        else{
            Toast.makeText(mContext, "已备注", Toast.LENGTH_SHORT).show();
            String orderHex = "AA7501000E000000000000000000170803162239C1";
            byte[] data = ByteUtils.hexStringToBytes(orderHex);
            mChatService.write(data);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomLoadView.getInstance(MainActivity.this).dismissProgress();
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    onInputDeviceName(msg.obj);
                    break;
                case MessageType.MESSAGE_READ:
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
