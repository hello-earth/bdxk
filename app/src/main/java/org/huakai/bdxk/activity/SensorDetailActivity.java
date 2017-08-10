package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigTitle;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TitleParams;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.view.CustomLoadView;
import org.huakai.bdxk.view.DashboardView;

/**
 * Created by Administrator on 2017/8/4.
 */

public class SensorDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private DashboardView dashboardView;
    private Context mContext;
    private BluetoothHelperService mChatService;
    private BluetoothDevice device;
    private String sensorid;
    private String desc;
    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private LinearLayout headSettingLayout;
    private ImageView settingButton;
    private TextView date;
    private LinearLayout headReloadLayout;
    private ImageView reloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail_layout);
        mContext = this;
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        sensorid = getIntent().getStringExtra(BluetoothDevice.EXTRA_NAME);
        desc = getIntent().getStringExtra("extra_desc");
        mChatService = BluetoothHelperService.getInstance(this, mHandler);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mChatService.isConnected()){
            mChatService.connect(device,false);
            CustomLoadView.getInstance(this).showProgress("正在连接设备");
        }else{
            initData();
        }
    }

    private void initView(){
        ((TextView)findViewById(R.id.head_title)).setText("传感器数据");
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        ((TextView)findViewById(R.id.sensor_desc)).setText(desc);
        ((TextView)findViewById(R.id.sensor_id)).setText(sensorid);
        dashboardView = (DashboardView)findViewById(R.id.dashboard_view);
        headSettingLayout = (LinearLayout)findViewById(R.id.com_head_setting_layout);
        settingButton = (ImageView)findViewById(R.id.com_head_setting);
        reloadButton = (ImageView)findViewById(R.id.com_head_reload);
        headReloadLayout = (LinearLayout)findViewById(R.id.com_head_reload_layout);

        date = (TextView)findViewById(R.id.sensor_date);
    }

    private void initListener(){
        titleLeft.setOnClickListener(this);
        headBackLayout.setOnClickListener(this);
        headSettingLayout.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        headReloadLayout.setOnClickListener(this);
        reloadButton.setOnClickListener(this);
    }


    private void initData(){
        sendCmd(ByteUtils.getCmdHexStr(sensorid,"10"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    private void sendCmd(String orderHex){
        if(!mChatService.isConnected()){
            mChatService.connect(device,false);
            CustomLoadView.getInstance(SensorDetailActivity.this).showProgress("设备连接已断开\n正在重新连接");
        }else {
            CustomLoadView.getInstance(this, 30000).showProgress("正在发送请求");
            byte[] data = ByteUtils.hexStringToBytes(orderHex);
            mChatService.write(data);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomLoadView.getInstance(SensorDetailActivity.this).dismissProgress();
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    ToastUtil.makeTextAndShow("设备已连接");
                    CustomLoadView.getInstance(SensorDetailActivity.this).showProgress("正在发送请求");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    },2000);
                    break;
                case MessageType.MESSAGE_READ:
                    onReciveData(msg.obj.toString());
                    break;
                case MessageType.MESSAGE_DISCONNECTED:
                    if(!mChatService.isConnected()){
                        mChatService.connect(device,false);
                        CustomLoadView.getInstance(SensorDetailActivity.this).showProgress("设备连接已断开\n正在重新连接");
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.com_head_back:
            case R.id.com_head_back_layout:
                SensorDetailActivity.this.finish();
                break;
            case R.id.com_head_setting:
            case R.id.com_head_setting_layout:
                showMenu();
                break;
            case R.id.com_head_reload:
            case R.id.com_head_reload_layout:
                initData();
                break;
        }
    }


    private void showMenu(){
        final String[] items = {"传感器信息", "传感器调零","读取标定信息", "设置标定信息"};
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
        switch (position){
            case 0:
            default:
                sendCmd(ByteUtils.getCmdHexStr(sensorid,"01"));
                break;
            case 1:
                sendCmd(ByteUtils.getCmdHexStr(sensorid,"80"));
                break;
            case 2:
                sendCmd(ByteUtils.getCmdHexStr(sensorid,"05"));
                break;
            case 3:
                sendCmd(ByteUtils.getCmdHexStr(sensorid,"84"));
                break;
        }
    }

    private void onReciveData(String data){
        RespondDecoder decoder = new RespondDecoder(data);
        if(decoder.getRequestId().equals("10")) {
            float tmp = decoder.getTemperature();
            tmp = (float) (Math.round(tmp * 100)) / 100;
            dashboardView.setRealTimeValue(tmp);
            date.setText(decoder.getMeasurementDate());
        }
        else if(decoder.getRequestId().equals("01")) {
            showData("传感器信息",decoder.getResult());
        }
        Log.d("DeviceDetailActivity",decoder.getResult());
    }

    private void showData(String title, String msg){
        new CircleDialog.Builder(this)
                .setTitle(title)
                .setText(msg)
                .setPositive("确定", null)
                .show();
    }
}
