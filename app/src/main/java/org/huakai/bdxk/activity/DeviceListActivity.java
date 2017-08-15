package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import org.huakai.bdxk.MyApplication;
import org.huakai.bdxk.R;
import org.huakai.bdxk.common.ScanResult;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.db.DevicesCollectionHelper;
import org.huakai.bdxk.view.CustomLoadView;
import org.huakai.bdxk.view.OnItemClickListener;
import org.huakai.bdxk.view.SwipeRecyclerView;

import java.util.ArrayList;

public class DeviceListActivity extends AppCompatActivity{

    private SwipeRecyclerView mRecyclerView;
    private ArrayList<ScanResult> ScanResults = new ArrayList<>();
    private DeviceListAdapter adapter;
    private RefreshLayout refreshLayout;
    private LinearLayout emptylayout;
    private Context mContext;
    private BluetoothAdapter mBtAdapter;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ((TextView)findViewById(R.id.head_title)).setText("无砟轨道板翘曲快速检测");
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        emptylayout = (LinearLayout) findViewById(R.id.emptylayout);
        mRecyclerView = (SwipeRecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceListAdapter(this,ScanResults);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        refreshLayout.setEnableLoadmore(false);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        initListener();
        if(mBtAdapter.isEnabled())
            doDiscovery();
        else
            ToastUtil.makeTextAndShow("请先打开蓝牙开关");

        mRecyclerView.setVisibility(View.GONE);
        emptylayout.setVisibility(View.VISIBLE);
//        RespondDecoder decoder = new RespondDecoder();
//        if(decoder.initData("557A100035002820946508000034170811152308FFFF18E1FFFF18E118E1A1E320201FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF42442D44435831352B")) {
//            String result = decoder.getResult();
//            Log.i("DeviceListActivity", result);
//        }else{
//            ToastUtil.makeTextAndShow("应答数据校验不正确");
//        }
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
        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CustomLoadView.getInstance(DeviceListActivity.this).showProgress();
                onInputDeviceName(ScanResults.get(position).getDevice(),false);
            }
            @Override
            public void onDeleteClick(int position) {
                CustomLoadView.getInstance(DeviceListActivity.this).showProgress();
                onInputDeviceName(ScanResults.get(position).getDevice(),true);
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
                emptylayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ScanResults.add(new ScanResult(device,intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI)));
                adapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                refreshLayout.finishRefresh();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                ScanResults.clear();
                adapter.notifyDataSetChanged();
            }
        }
    };


    private void onInputDeviceName(final BluetoothDevice device, final boolean isRename){
        final DevicesCollectionHelper devicesHelper = new DevicesCollectionHelper(getApplicationContext());
        devicesHelper.open();
        long flag = 0;
        if(!isRename)
            flag = devicesHelper.isHasDeviceInfo(device,"");
        CustomLoadView.getInstance(DeviceListActivity.this).dismissProgress();
        if(flag!=-1) {
            new CircleDialog.Builder(DeviceListActivity.this)
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
                            if(isRename)
                                devicesHelper.delete(device.getAddress());
                            long flag = devicesHelper.insertDeviceCollectionInfo(device, text);
                            if (flag == -1) {
                                ToastUtil.makeTextAndShow("该节点已在数据库中存在");
                            } else if (flag == -2) {
                                ToastUtil.makeTextAndShow("该备注已存在，请保证备注名称的唯一性");
                            }else if(flag==1){
                                if(isRename)
                                    adapter.notifyDataSetChanged();
                                else
                                    nextActivity(device);
                            }
                        }
                    })
                    .show();
        }
        else{
            nextActivity(device);
        }
    }

    private void nextActivity(BluetoothDevice device){
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        Intent intent = new Intent();
        intent.setClass(DeviceListActivity.this, SensorListActivity.class);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE,device);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.makeTextAndShow("再按一次退出");
                mExitTime = System.currentTimeMillis();
            } else {
                MyApplication.getInstance().applicationExit();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
