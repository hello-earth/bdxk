package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.BlueCmdMgr;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.MeasureBean;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.view.CustomLoadView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/15.
 */

public class CalibrateActivity  extends AppCompatActivity {

    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private EditText editTextH;
    private EditText editTextL;
    private EditText editTextLimitH;
    private RadioButton radioMale5600;
    private RadioButton radioMale4925;
    private RadioButton radioMale4856;
    private RadioButton radio_vector1;
    private RadioButton radio_vector2;
    private BluetoothDevice device;
    private ArrayList<SensorBean> sensorList;
    private static BlueCmdMgr cmdMgr;
    private Button firstmeasure;
    private Button secondmeasure;
    private int which;
    private ArrayList<ArrayList<MeasureBean>> MeasureBeans = new ArrayList<ArrayList<MeasureBean>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_layout);
        findViewById(R.id.com_head_add_layout).setVisibility(View.GONE);
        findViewById(R.id.com_head_setting_layout).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.head_title)).setText("初始化标定信息");
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        sensorList = getIntent().getParcelableArrayListExtra(BluetoothDevice.EXTRA_NAME);
        BluetoothHelperService mChatService = BluetoothHelperService.getInstance(this, mHandler);
        cmdMgr = BlueCmdMgr.getInstance(this,mChatService,device);
        initView();
        initListener();
    }

    private void initView(){
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        editTextH = (EditText)findViewById(R.id.paramter_h);
        editTextL = (EditText)findViewById(R.id.paramter_l);
        editTextLimitH = (EditText)findViewById(R.id.paramter_limith);
        radioMale5600 =(RadioButton)findViewById(R.id.radioMale5600);
        radioMale4925 =(RadioButton)findViewById(R.id.radioMale4925);
        radioMale4856 =(RadioButton)findViewById(R.id.radioMale4856);
        radio_vector1 =(RadioButton)findViewById(R.id.radio_vector1);
        radio_vector2 =(RadioButton)findViewById(R.id.radio_vector2);

        firstmeasure = (Button)findViewById(R.id.button1);
        secondmeasure = (Button)findViewById(R.id.button2);
    }

    private void initListener() {
        View.OnClickListener finish = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalibrateActivity.this.finish();
            }
        };
        titleLeft.setOnClickListener(finish);
        headBackLayout.setOnClickListener(finish);

        View.OnClickListener measure = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = v.getId()==R.id.button1?0:1;
                String h = editTextH.getText().toString();
                String l = editTextL.getText().toString();
                String lh = editTextLimitH.getText().toString();
                if(!"".equals(h) && !"".equals(l) && !"".equals(lh)){
                    editTextH.setEnabled(false);
                    editTextL.setEnabled(false);
                    editTextLimitH.setEnabled(false);
                    CustomLoadView.getInstance(CalibrateActivity.this, 30000).showProgress("正在发送请求");
                    new Thread(sender).start();
                }
            }
        };
        firstmeasure.setOnClickListener(measure);
        secondmeasure.setOnClickListener(measure);
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
            MeasureBeans.get(which).add(new MeasureBean(decoder.getIdentifier(),decoder.getMeasurementDate(),decoder.getTemperature(),decoder.getOffsetVaule()));
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
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
