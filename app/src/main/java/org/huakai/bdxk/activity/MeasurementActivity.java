package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wjk.tableview.TableView;
import com.wjk.tableview.common.TableCellData;
import com.wjk.tableview.common.TableHeaderColumnModel;
import com.wjk.tableview.toolkits.SimpleTableDataAdapter;
import com.wjk.tableview.toolkits.SimpleTableHeaderAdapter;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.BlueCmdMgr;
import org.huakai.bdxk.common.BluetoothHelperService;
import org.huakai.bdxk.common.ByteUtils;
import org.huakai.bdxk.common.ComparatorMeasureBean;
import org.huakai.bdxk.common.MeasureBean;
import org.huakai.bdxk.common.MessageType;
import org.huakai.bdxk.common.RespondDecoder;
import org.huakai.bdxk.common.SensorBean;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.view.CustomLoadView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/8/17.
 */

public class MeasurementActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private Button lastplate;
    private Button readValue;
    private Button nextplate;
    private TextView plateNo;
    private TableView tableView;
    private BluetoothDevice device;
    private static BlueCmdMgr cmdMgr;
    private ArrayList<SensorBean> sensorList;
    private int currentIndex=0;
    ArrayList<ArrayList<MeasureBean>> measureBeans = new ArrayList<ArrayList<MeasureBean>>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_layout);
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        sensorList = getIntent().getParcelableArrayListExtra(BluetoothDevice.EXTRA_NAME);
        BluetoothHelperService mChatService = BluetoothHelperService.getInstance(this, mHandler);
        cmdMgr = BlueCmdMgr.getInstance(this,mChatService,device);
        initView();
        initListener();
    }

    private void initData(int position) {
        if(measureBeans.size()<currentIndex) return;
        if(measureBeans.size()==currentIndex) {
            tableView.removeAllViews();
            return;
        }
        tableView.removeAllViews();
        ArrayList<MeasureBean> measureBean = measureBeans.get(position);
        if(measureBean.size()==7){
            ArrayList<TableCellData> cellDatas = new ArrayList<>() ;
            for(int i =0; i<measureBean.size();i++){
                MeasureBean bean = measureBean.get(i);
                cellDatas.add(new TableCellData(bean.getIdentifier(), i, 0));
                cellDatas.add(new TableCellData(bean.getSensorName(), i, 1));
                cellDatas.add(new TableCellData(bean.getTemperature()+"", i, 2));
                cellDatas.add(new TableCellData(bean.getOffsetValue()+"", i, 3));
                cellDatas.add(new TableCellData(bean.getMeasurementDate(), i, 4));
            }
            LinkedHashMap columns = initTabViewHeader();
            SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(this, cellDatas, 4);
            dataAdapter.setTextSize(12);
            TableHeaderColumnModel columnModel = new TableHeaderColumnModel(columns);
            SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this,columnModel);
            headerAdapter.setTextSize(14);
            tableView.setTableAdapter(headerAdapter,dataAdapter);
            tableView.setHeaderElevation(18);
        }
    }

    private void initView() {
        ((TextView)findViewById(R.id.head_title)).setText("测量数据");
        findViewById(R.id.com_head_add_layout).setVisibility(View.GONE);
        findViewById(R.id.com_head_setting_layout).setVisibility(View.GONE);
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView) findViewById(R.id.com_head_back);
        lastplate = (Button)findViewById(R.id.button1);
        readValue = (Button)findViewById(R.id.button2);
        nextplate = (Button)findViewById(R.id.button3);
        plateNo = (TextView)findViewById(R.id.plate_no);
        plateNo.setText("当前第1块板");
        lastplate.setVisibility(View.GONE);
        tableView = (TableView)findViewById(R.id.tableview);
        currentIndex = 0;
    }

    private void initListener(){
        titleLeft.setOnClickListener(this);
        headBackLayout.setOnClickListener(this);
        lastplate.setOnClickListener(this);
        readValue.setOnClickListener(this);
        nextplate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.com_head_back_layout:
            case R.id.com_head_back:
                MeasurementActivity.this.finish();
                break;
            case R.id.button1:
                if(currentIndex>0) currentIndex--;
                if(currentIndex==0) lastplate.setVisibility(View.GONE);
                initData(currentIndex);
                break;
            case R.id.button2:
                onMeasureClick();
                break;
            case R.id.button3:
                if(measureBeans.size()-currentIndex>0){
                    currentIndex++;
                    lastplate.setVisibility(View.VISIBLE);
                    initData(currentIndex);
                }
                break;
            default:
                break;

        }

    }

    private void onMeasureClick(){
        if(measureBeans.size()>currentIndex+1)
            measureBeans.remove(currentIndex);
        CustomLoadView.getInstance(MeasurementActivity.this,150000).showProgress("正在测量，请稍后");
        new Thread(sender).start();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessageType.MESSAGE_CONNECTED:
                    ToastUtil.makeTextAndShow("设备已连接");
                    CustomLoadView.getInstance(MeasurementActivity.this).dismissProgress();
                    break;
                case MessageType.MESSAGE_READ:
                    onReciveData(msg.obj.toString());
                    break;
                case MessageType.MESSAGE_DISCONNECTED:
                    if(!cmdMgr.isConnected()){
                        CustomLoadView.getInstance(MeasurementActivity.this).showProgress("正在连接设备");
                        cmdMgr.connect();
                    }
                    break;
            }
        }
    };

    private void onReciveData(String data){
        RespondDecoder decoder = new RespondDecoder();
        if(measureBeans.size()<=currentIndex){
            measureBeans.add(new ArrayList<MeasureBean>());
        }
        ArrayList<MeasureBean> beans = measureBeans.get(currentIndex);
        if(beans.size()>0 && beans.get(0).getWhichplate()!=currentIndex){
            measureBeans.add(currentIndex,new ArrayList<MeasureBean>());
            beans = measureBeans.get(currentIndex);
        }
        if(beans.size()==7) return;
        if(decoder.initData(data)) {
            if(beans.size()<7) {
                String name = SensorBean.getNameByid(sensorList,decoder.getIdentifier());
                MeasureBean mBean = new MeasureBean(decoder.getIdentifier(),name, decoder.getMeasurementDate(), decoder.getTemperature(), decoder.getOffsetVaule());
                beans.add(mBean);
            }
            if(beans.size()==7) {
                ComparatorMeasureBean comparator=new ComparatorMeasureBean();
                Collections.sort(beans, comparator);
                CustomLoadView.getInstance(MeasurementActivity.this).dismissProgress();
                initData(currentIndex);
            }
        }
    }

    private LinkedHashMap initTabViewHeader(){
        LinkedHashMap columns = new LinkedHashMap<>();
        columns.put(0,new Pair<>("传感器编号",1));
        columns.put(1,new Pair<>("传感器名称",1));
        columns.put(2,new Pair<>("温度℃",1));
        columns.put(3,new Pair<>("偏移",1));
        columns.put(4,new Pair<>("测量日期",1));
        return columns;
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
}
