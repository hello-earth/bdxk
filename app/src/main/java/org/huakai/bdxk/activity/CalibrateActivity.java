package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.CircleDialog;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.BlueCmdMgr;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.MeasureBean;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.common.SharedPreferencesUtil;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.view.CustomLoadView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/15.
 */

public class CalibrateActivity  extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private BluetoothDevice device;
    private ArrayList<SensorBean> sensorList;
    private static BlueCmdMgr cmdMgr;
    private Button firstmeasure;
    private Button secondmeasure;
    private Button testmeasure;
    private LinearLayout headSettingLayout;
    private ImageView settingButton;
    private TextView firstView;
    private TextView secondView;
    private TextView thirdView;
    private int which=-1;
    private float bjg;
    private float zyl;
    private float xc;
    private int MaleType;
    private int vector;

    private ArrayList<ArrayList<MeasureBean>> MeasureBeans = new ArrayList<ArrayList<MeasureBean>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_layout);
        findViewById(R.id.com_head_add_layout).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.head_title)).setText("初始化修正系数");
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        sensorList = getIntent().getParcelableArrayListExtra(BluetoothDevice.EXTRA_NAME);
        BluetoothHelperService mChatService = BluetoothHelperService.getInstance(this, mHandler);
        cmdMgr = BlueCmdMgr.getInstance(this,mChatService,device);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        which=-1;
//        cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensorList.get(0).getSensorId(),"10"));
    }

    private void initView(){
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        headSettingLayout = (LinearLayout)findViewById(R.id.com_head_setting_layout);
        settingButton = (ImageView)findViewById(R.id.com_head_setting);
        firstmeasure = (Button)findViewById(R.id.button1);
        secondmeasure = (Button)findViewById(R.id.button2);
        testmeasure = (Button)findViewById(R.id.button3);
        firstView = (TextView)findViewById(R.id.firstView);
        secondView = (TextView)findViewById(R.id.secondView);
        thirdView = (TextView)findViewById(R.id.thirdView);
        firstView.setMovementMethod(ScrollingMovementMethod.getInstance());
        secondView.setMovementMethod(ScrollingMovementMethod.getInstance());
        thirdView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initListener() {
        titleLeft.setOnClickListener(this);
        headBackLayout.setOnClickListener(this);
        titleLeft.setOnClickListener(this);
        headBackLayout.setOnClickListener(this);
        headSettingLayout.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        firstmeasure.setOnClickListener(this);
        secondmeasure.setOnClickListener(this);

        testmeasure.setOnClickListener(this);
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    ToastUtil.makeTextAndShow("设备已连接");
                    CustomLoadView.getInstance(CalibrateActivity.this).dismissProgress();
                    break;
                case MessageType.MESSAGE_READ:
                    onReciveData(msg.obj.toString());
                    break;
                case MessageType.MESSAGE_DISCONNECTED:
                    if(!cmdMgr.isConnected()){
                        CustomLoadView.getInstance(CalibrateActivity.this).showProgress("正在连接设备");
                        cmdMgr.connect();
                    }
                    break;
            }
        }
    };

    private void onReciveData(String data){
        if(which==-1) return;
        RespondDecoder decoder = new RespondDecoder();
        if(decoder.initData(data)) {
            Log.d("DeviceDetailActivity", decoder.getResult());
            if(which==0 && MeasureBeans.size()==0){
                MeasureBeans.add(new ArrayList<MeasureBean>());
            }else if(which==1 && MeasureBeans.size()==1){
                MeasureBeans.add(new ArrayList<MeasureBean>());
            }else if(which==0 && MeasureBeans.size()==1){
                if(MeasureBeans.get(0).size()==7)
                    MeasureBeans.get(0).clear();
            }else if(which==1 && MeasureBeans.size()==2){
                if(MeasureBeans.get(1).size()==7)
                    MeasureBeans.get(1).clear();
            }
            if(MeasureBeans.get(which).size()<7){
                MeasureBean mBean = new MeasureBean(decoder.getIdentifier(),decoder.getMeasurementDate(),decoder.getTemperature(),decoder.getOffsetVaule());
                MeasureBeans.get(which).add(mBean);
                if(which==0)
                    firstView.append("\n"+mBean.toString());
                else{
                    secondView.append("\n"+mBean.toString());
                }
            }
            if(MeasureBeans.get(which).size()==7) {
                which=-1;
                CustomLoadView.getInstance(CalibrateActivity.this).dismissProgress();
                if(MeasureBeans.size()==2 && MeasureBeans.get(0).size()==7 && MeasureBeans.get(1).size()==7){
                    calculation();
                }
            }

//            new Thread(new Dataloger(sensorid,decoder.get10SavingStr())).start();
        }else{
            ToastUtil.makeTextAndShow("应答数据校验不正确");
        }
    }

    private final Runnable sender = new Runnable() {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            for (SensorBean sensor : sensorList) {
                cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensor.getSensorId(), "10"));
                try {
                    Thread.sleep(5000);
                    cmdMgr.sendCmd(ByteUtils.getCmdHexStr(sensor.getSensorId(), "10"));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.com_head_back:
            case R.id.com_head_back_layout:
                CalibrateActivity.this.finish();
                break;
            case R.id.com_head_setting:
            case R.id.com_head_setting_layout:
                nextActivity();
                break;
            case R.id.button1:
                which=0;
                onFirstClick();
                break;
            case R.id.button2:
                which=1;
                onFirstClick();
                break;
            case R.id.button3:
                saveFinish();
                break;
            default:
                break;
        }
    }


    private void nextActivity(){
        Intent intent = new Intent();
        intent.setClass(CalibrateActivity.this, CalibrateSettingActivity.class);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE,device);
        startActivity(intent);
    }

    private void onFirstClick(){
        String bjgs = SharedPreferencesUtil.readString("bjg","");
        String zyls = SharedPreferencesUtil.readString("zyl","");
        String xcs = SharedPreferencesUtil.readString("xc","");
        MaleType = SharedPreferencesUtil.readInt("maletype",-1);
        vector = SharedPreferencesUtil.readInt("vector",-1);
        try{
            if(!"".equals(bjgs) && !"".equals(zyls) && !"".equals(xcs) && MaleType!=-1 && vector !=-1 ){
                bjg = Float.parseFloat(bjgs);
                zyl = Float.parseFloat(zyls);
                xc = Float.parseFloat(xcs);
                CustomLoadView.getInstance(CalibrateActivity.this,150000).showProgress("正在测量，请稍后");
                new Thread(sender).start();
            }else{
                new CircleDialog.Builder(this)
                        .setTitle("提示")
                        .setText("请先设置标定参数")
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nextActivity();
                            }
                        })
                        .show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void saveFinish(){
        this.finish();
    }


    private void calculation(){
//        for(int i=0;i<2;i++){
//            MeasureBeans.add(new ArrayList<MeasureBean>());
//            for(int j=0;j<7;j++){
//                String id = "282094650800003";
//                float off = (float)(0-j*0.1);
//                if(j>3)
//                    off = (float)((0-(6-j)*0.1));
//                MeasureBean mBean = new MeasureBean(id+j,"17081617124"+j,28.5f,off);
//                MeasureBeans.get(i).add(mBean);
//                if(i==0)
//                    firstView.append("\n"+mBean.toString());
//                else{
//                    secondView.append("\n"+mBean.toString());
//                }
//            }
//        }

        float d1=0,d2=0,d3=0,d5=0,d7=0,d8=0,d9=0;
        float dy1=0,dy2=0,dy3=0,dy5=0,dy7=0,dy8=0,dy9=0;
        float y1 = zyl - MeasureBeans.get(0).get(0).getOffsetValue();
        float y2 = zyl - MeasureBeans.get(0).get(1).getOffsetValue();
        float y3 = zyl - MeasureBeans.get(0).get(2).getOffsetValue();
        float y5 = zyl - MeasureBeans.get(0).get(3).getOffsetValue();
        float y7 = zyl - MeasureBeans.get(0).get(4).getOffsetValue();
        float y8 = zyl - MeasureBeans.get(0).get(5).getOffsetValue();
        float y9 = zyl - MeasureBeans.get(0).get(6).getOffsetValue();

        float y11 = zyl - MeasureBeans.get(1).get(0).getOffsetValue();
        float y22 = zyl - MeasureBeans.get(1).get(1).getOffsetValue();
        float y33 = zyl - MeasureBeans.get(1).get(2).getOffsetValue();
        float y55 = zyl - MeasureBeans.get(1).get(3).getOffsetValue();
        float y77 = zyl - MeasureBeans.get(1).get(4).getOffsetValue();
        float y88 = zyl - MeasureBeans.get(1).get(5).getOffsetValue();
        float y99 = zyl - MeasureBeans.get(1).get(6).getOffsetValue();
        float k = 0;


        int shanggong = y5<bjg?0:1;
        if(vector==0){ //向1号尺
            if(shanggong==0){ //上拱
                d5 = bjg-y5;
                d3 = 2*(bjg-y55) + d5;
                k = (d3+d5)/2;
                d2 = y33-y3+d3-d5+3*k;
                d1 = y22-y2+d2-d5+4*k;
                d7 = y7-y77+d5+k;
                d8 = y8-y88+d7+d5+2*k;
                d9 = y9-y99+d8+d5+3*k;
            }else{ //下拱
                d5 = y5-bjg;
                d3 = -2*(bjg-y55) + d5;
                k = (d3+d5)/2;
                d2 = y3-y33+d3-d5+3*k;
                d1 = y2-y22+d2-d5+4*k;
                d7 = y77-y7+d5+k;
                d8 = y88-y8+d7+d5+2*k;
                d9 = y99-y9+d8+d5+3*k;
            }
        }
        else{ //向9号尺
            if(shanggong==0){//上拱
                d5 = bjg-y5;
                d7 = 2*(bjg-y55) + d5;
                k = (d7+d5)/2;
                d3 = y3-y33+d5+k;
                d2 = y2-y22+d3+d5+2*k;
                d1 = y1-y11+d2+d5+3*k;
                d8 = y77-y7+d7-d5+3*k;
                d9 = y88-y8+d8-d5+4*k;
            }else{
                d5 = y5-bjg;
                d7 = -2*(bjg-y55) + d5;
                k = (d7+d5)/2;
                d3 = y33-y3+d5+k;
                d2 = y22-y2+d3+d5+2*k;
                d1 = y11-y1+d2+d5+3*k;
                d8 = y7-y77+d7-d5+3*k;
                d9 = y8-y88+d8-d5+4*k;
            }
        }

        if(shanggong==0){
            dy1=bjg-y1+d1;
            dy2=bjg-y2+d2;
            dy3=bjg-y3+d3;
            dy5=bjg-y5-d5;
            dy7=bjg-y7+d7;
            dy8=bjg-y8+d8;
            dy9=bjg-y9+d9;
        }else{
            dy1=bjg-y1-d1;
            dy2=bjg-y2-d2;
            dy3=bjg-y3-d3;
            dy5=bjg-y5+d5;
            dy7=bjg-y7-d7;
            dy8=bjg-y8-d8;
            dy9=bjg-y9-d9;
        }
        thirdView.append(String.format(" %s，%s移动\nδy1=%.2f,δy2=%.2f,δy3=%.2f,δy5=%.2f,δy7=%.2f,δy8=%.2f,δy9=%.2f",shanggong==0?"上拱":"下拱",
                vector==0?"向1号尺":"向9号尺",dy1,dy2,dy3,dy5,dy7,dy8,dy9));
        SharedPreferencesUtil.saveFloat("y1",y1);
        SharedPreferencesUtil.saveFloat("y2",y2);
        SharedPreferencesUtil.saveFloat("y3",y3);
        SharedPreferencesUtil.saveFloat("y5",y5);
        SharedPreferencesUtil.saveFloat("y7",y7);
        SharedPreferencesUtil.saveFloat("y8",y8);
        SharedPreferencesUtil.saveFloat("y9",y9);
        SharedPreferencesUtil.saveFloat("dy1",dy1);
        SharedPreferencesUtil.saveFloat("dy2",dy2);
        SharedPreferencesUtil.saveFloat("dy3",dy3);
        SharedPreferencesUtil.saveFloat("dy5",dy5);
        SharedPreferencesUtil.saveFloat("dy7",dy7);
        SharedPreferencesUtil.saveFloat("dy8",dy8);
        SharedPreferencesUtil.saveFloat("dy9",dy9);
    }
}
