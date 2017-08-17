package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigTitle;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.huakai.bdxk.MyApplication;
import org.huakai.bdxk.R;
import org.huakai.bdxk.common.BlueCmdMgr;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.common.SharedPreferencesUtil;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.db.DevicesCollectionHelper;
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
    private ArrayList<SensorBean> sensorList = new ArrayList<>();
    private SensorAdapter adapter;
    private RefreshLayout refreshLayout;
    private LinearLayout emptylayout;
    private LinearLayout headSettingLayout;
    private ImageView settingButton;
    private Context mContext;
    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private SensorCollectionHelper sensorHelper;
    private LinearLayout headAddLayout;
    private ImageView addButton;
    private BluetoothDevice device;
    private static BlueCmdMgr cmdMgr;
    private static List<SensorBean> sensorBeens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        mContext = this;
        initView();
        initListener();
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        BluetoothHelperService mChatService = BluetoothHelperService.getInstance(this, mHandler);
        cmdMgr = BlueCmdMgr.getInstance(this,mChatService,device);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMenu();
    }

    private void initView(){
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        mRecyclerView = (SwipeRecyclerView) findViewById(R.id.recyclerview);
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        headAddLayout = (LinearLayout)findViewById(R.id.com_head_add_layout);
        addButton = (ImageView)findViewById(R.id.com_head_add);
        headSettingLayout = (LinearLayout)findViewById(R.id.com_head_setting_layout);
        settingButton = (ImageView)findViewById(R.id.com_head_setting);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SensorAdapter(this, sensorList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        emptylayout = (LinearLayout) findViewById(R.id.emptylayout);
    }

    private void initListener(){
        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                nextActivity(device, sensorList.get(position),SensorCommonActivity.class);
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
        headSettingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }

    private void initMenu(){
        if(cmdMgr!=null){
            CustomLoadView.getInstance(SensorListActivity.this).showProgress("正在连接设备");
            cmdMgr.connect();
        }else{
            CustomLoadView.getInstance(this).showProgress("");
        }
        mRecyclerView.setVisibility(View.GONE);
        emptylayout.setVisibility(View.VISIBLE);
        sensorList.clear();
        sensorHelper =  new SensorCollectionHelper(this);
        sensorHelper.open();
        sensorBeens =sensorHelper.getAllSensors(device.getAddress());
        sensorHelper.close();
        for(SensorBean sensor : sensorBeens)
            sensorList.add(sensor);
        adapter.notifyDataSetChanged();
        emptylayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        DevicesCollectionHelper devicesHelper =  new DevicesCollectionHelper(this);
        devicesHelper.open();
        String name = devicesHelper.getDescByMac(device.getAddress());
        devicesHelper.close();
        ((TextView)findViewById(R.id.head_title)).setText("传感器列表("+name+")");
        CustomLoadView.getInstance(this).dismissProgress();
    }

    private void showMenu(){
        final String[] items = {"设置标定", "开始测量", "传感器调零"};
        new CircleDialog.Builder(this)
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        //增加弹出动画
                        params.animStyle = R.style.dialogWindowAnim;
                    }
                })
                .setTitleColor(Color.BLUE)
                .setItems(items, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int
                            position, long id) {
                        onMenuClick(position);
                    }
                })
                .setNegative("取消", null)
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        //取消按钮字体颜色
                        params.textColor = Color.RED;
                    }
                })
                .configTitle(new ConfigTitle() {
                    @Override
                    public void onConfig(TitleParams params) {
                        params.height = 0;
                    }
                })
                .show();
    }

    private void onMenuClick(int position){
        if(sensorList.size()!=7){
            ToastUtil.makeTextAndShow("计算修正系数需要7个传感器节点");
            return;
        }
        switch (position){
            case 2:
                CustomLoadView.getInstance(SensorListActivity.this,30000).showProgress("正在发送请求");
                cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensorList.get(0).getSensorId(),"80"));
                break;
            case 0:
                nextActivity(device, sensorList,CalibrateActivity.class);
                break;
            case 1:
                float dy5 = SharedPreferencesUtil.readFloat("dy5",9999);
                if(dy5!=9999)
                    nextActivity(device, sensorList,MeasurementActivity.class);
                else
                    ToastUtil.makeTextAndShow("请先设置标定系数");
                break;
            default:
                break;
        }
    }

    private void onSensorDelete(int position){
        sensorHelper =  new SensorCollectionHelper(this);
        sensorHelper.open();
        sensorHelper.delete(sensorList.get(position).getSensorId(), sensorList.get(position).getSensorName());
        sensorHelper.close();
        adapter.removeItem(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void nextActivity(BluetoothDevice device, ArrayList<SensorBean> sensorBeans, Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SensorListActivity.this, cls);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE,device);
        intent.putParcelableArrayListExtra(BluetoothDevice.EXTRA_NAME,sensorBeans);
        startActivity(intent);
    }

    private void nextActivity(BluetoothDevice device, SensorBean sensorBean, Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SensorListActivity.this, cls);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE,device);
        intent.putExtra(BluetoothDevice.EXTRA_NAME,sensorBean);
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
                case MessageType.MESSAGE_READ:
                    ToastUtil.makeTextAndShow("传感器已调零");
                    break;
            }
        }
    };

}
