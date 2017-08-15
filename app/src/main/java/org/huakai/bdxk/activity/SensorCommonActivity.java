package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import org.huakai.bdxk.common.BlueCmdMgr;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.Dataloger;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.view.CustomLoadView;

/**
 * Created by Administrator on 2017/8/11.
 */

public class SensorCommonActivity extends AppCompatActivity implements View.OnClickListener  {

    private Context mContext;
    private BluetoothDevice device;
    private static BlueCmdMgr cmdMgr;
    private SensorBean sensor;
    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private LinearLayout headSettingLayout;
    private ImageView settingButton;
    private TextView dataView;
    private LinearLayout headReloadLayout;
    private ImageView reloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_common);
        mContext = this;
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        sensor = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_NAME);
        BluetoothHelperService mChatService = BluetoothHelperService.getInstance(this, mHandler);
        cmdMgr = BlueCmdMgr.getInstance(this,mChatService,device);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cmdMgr!=null) {
            if (!cmdMgr.isConnected()) {
                CustomLoadView.getInstance(SensorCommonActivity.this).showProgress("正在连接设备");
                cmdMgr.connect();
            } else {
                initData();
            }
        }
    }

    private void initView(){
        ((TextView)findViewById(R.id.head_title)).setText("传感器数据");
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        headSettingLayout = (LinearLayout)findViewById(R.id.com_head_setting_layout);
        settingButton = (ImageView)findViewById(R.id.com_head_setting);
        reloadButton = (ImageView)findViewById(R.id.com_head_add);
        reloadButton.setBackgroundResource(R.mipmap.ic_action_reload);
        headReloadLayout = (LinearLayout)findViewById(R.id.com_head_add_layout);
        dataView = (TextView)findViewById(R.id.sensor_data);
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
        CustomLoadView.getInstance(SensorCommonActivity.this,300000).showProgress("正在发送请求");
        cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensor.getSensorId(),"01"));
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (cmdMgr != null) {
//            cmdMgr.stop();
//        }
//    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomLoadView.getInstance(SensorCommonActivity.this).dismissProgress();
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    ToastUtil.makeTextAndShow("设备已连接");
                    CustomLoadView.getInstance(SensorCommonActivity.this).showProgress("正在发送请求");
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
                    if(!cmdMgr.isConnected()){
                        CustomLoadView.getInstance(SensorCommonActivity.this).showProgress("正在连接设备");
                        cmdMgr.connect();
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
                SensorCommonActivity.this.finish();
                break;
            case R.id.com_head_setting:
            case R.id.com_head_setting_layout:
                showMenu();
                break;
            case R.id.com_head_add:
            case R.id.com_head_add_layout:
                initData();
                break;
        }
    }


    private void showMenu(){
        final String[] items = {"传感器信息", "采集数据", "传感器调零", "详细数据"};
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
            default:
            case 0:
                CustomLoadView.getInstance(SensorCommonActivity.this,30000).showProgress("正在发送请求");
                cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensor.getSensorId(),"01"));
                break;
            case 1:
                CustomLoadView.getInstance(SensorCommonActivity.this,30000).showProgress("正在发送请求");
                cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensor.getSensorId(),"10"));
                break;
            case 2:
                CustomLoadView.getInstance(SensorCommonActivity.this,30000).showProgress("正在发送请求");
                cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensor.getSensorId(),"80"));
                break;
            case 3:
                CustomLoadView.getInstance(SensorCommonActivity.this,30000).showProgress("正在发送请求");
                cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensor.getSensorId(),"0F"));
                break;
        }
    }

    private void onReciveData(String data){
        RespondDecoder decoder = new RespondDecoder();
        if(decoder.initData(data)) {
            dataView.setText(decoder.getResult());
            Log.d("DeviceDetailActivity", decoder.getResult());
//            new Thread(new Dataloger(sensorid,decoder.get10SavingStr())).start();
        }else{
            ToastUtil.makeTextAndShow("应答数据校验不正确");
        }
    }

    private void showData(String title, String msg){
        new CircleDialog.Builder(this)
                .setTitle(title)
                .setText(msg)
                .setPositive("确定", null)
                .show();
    }

}
