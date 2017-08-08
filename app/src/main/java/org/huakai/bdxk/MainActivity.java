package org.huakai.bdxk;

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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.huakai.bdxk.activity.SensorListActivity;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.ScanResult;
import org.huakai.bdxk.db.DevicesCollectionHelper;
import org.huakai.bdxk.view.CustomLoadView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private ArrayList<ScanResult> ScanResults = new ArrayList<>();
    private DeviceListAdapter adapter;
    private RefreshLayout refreshLayout;
    private Context mContext;
    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        (findViewById(R.id.com_head_back_layout)).setVisibility(View.GONE);
        (findViewById(R.id.com_head_add_layout)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.head_title)).setText("BDXK");
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
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
            Toast.makeText(mContext,"请先打开蓝牙开关",Toast.LENGTH_SHORT).show();


        RespondDecoder decoder = new RespondDecoder("557A100035002820946508000034170806094018015418E6FFFC18E618E6A1E320201FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF42442D444358313502");
        String result = decoder.getResult();
        Log.i("MainActivity", result);
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
                CustomLoadView.getInstance(MainActivity.this).showProgress();
                onInputDeviceName(ScanResults.get(position).getDevice());
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


    private void onInputDeviceName(final BluetoothDevice device){
        final DevicesCollectionHelper devicesHelper =  new DevicesCollectionHelper(getApplicationContext());
        devicesHelper.open();
        long flag = devicesHelper.isHasDeviceInfo(device,"");
        CustomLoadView.getInstance(MainActivity.this).dismissProgress();
        if(flag!=-1) {
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
//                            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                            long flag = devicesHelper.insertDeviceCollectionInfo(device, text);
                            if (flag == -1) {
                                Toast.makeText(mContext, "该节点已在数据库中存在", Toast.LENGTH_SHORT).show();
                            } else if (flag == -2) {
                                Toast.makeText(mContext, "该备注已存在，请保证备注名称的唯一性", Toast.LENGTH_SHORT).show();
                            }else if(flag==1){
//                                Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                                nextActivity(device);
                            }
                        }
                    })
                    .show();
        }
        else{
//            Toast.makeText(mContext, "已备注", Toast.LENGTH_SHORT).show();
            nextActivity(device);
        }
    }

    private void nextActivity(BluetoothDevice device){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SensorListActivity.class);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE,device);
        startActivity(intent);
    }

}
