package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
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
import org.huakai.bdxk.common.SharedPreferencesUtil;
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
    private TextView remarks;
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
        remarks.setText("");
        tableView.removeAllViews();
        if(measureBeans.size()<=currentIndex) return;

        ArrayList<MeasureBean> measureBean = measureBeans.get(position);
        if(measureBean.size()==7){
            String msg = "";
            ArrayList<TableCellData> cellDatas = new ArrayList<>() ;
            for(int i =0; i<measureBean.size();i++){
                MeasureBean bean = measureBean.get(i);
                cellDatas.add(new TableCellData(bean.getSensorName(), i, 0));
                cellDatas.add(new TableCellData(bean.getTemperature()+"", i, 1));
                cellDatas.add(new TableCellData(bean.getOffsetValue()+"", i, 2));
                cellDatas.add(new TableCellData(String.format("%.2f",bean.getWarp()), i, 3));
                cellDatas.add(new TableCellData(bean.getMeasurementDate(), i, 4));
                msg += String.format("%s: %s",bean.getSensorName(),bean.getIdentifier());
                if(i%2==1)
                    msg += "\n";
                else
                    msg += ";";
            }
            LinkedHashMap columns = initTabViewHeader();
            SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(this, cellDatas, 5);
            dataAdapter.setTextSize(12);
            TableHeaderColumnModel columnModel = new TableHeaderColumnModel(columns);
            SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this,columnModel);
            headerAdapter.setTextSize(14);
            tableView.setTableAdapter(headerAdapter,dataAdapter);
            tableView.setHeaderElevation(18);
            msg+=String.format("\n%s移动；δy1=%.2f；δy2=%.2f；δy3=%.2f；δy5=%.2f；δy7=%.2f；δy8=%.2f；δy9=%.2f",
                    SharedPreferencesUtil.readInt("vector",0)==0?"向1号尺":"向9号尺",SharedPreferencesUtil.readFloat("dy1",0),SharedPreferencesUtil.readFloat("dy2",0),
                    SharedPreferencesUtil.readFloat("dy3",0),SharedPreferencesUtil.readFloat("dy5",0),SharedPreferencesUtil.readFloat("dy7",0),SharedPreferencesUtil.readFloat("dy8",0),SharedPreferencesUtil.readFloat("dy9",0));
            remarks.setText(msg);
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
        remarks = (TextView)findViewById(R.id.remarksview);
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
                plateNo.setText("当前第"+(currentIndex+1)+"块板");
                initData(currentIndex);
                break;
            case R.id.button2:
                onMeasureClick();
                break;
            case R.id.button3:
                if(measureBeans.size()-currentIndex>0){
                    currentIndex++;
                    plateNo.setText("当前第"+(currentIndex+1)+"块板");
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
                mBean.setWhichplate(currentIndex);
                beans.add(mBean);
            }
            if(beans.size()==7) {
                ComparatorMeasureBean comparator=new ComparatorMeasureBean();
                Collections.sort(beans, comparator);
                CustomLoadView.getInstance(MeasurementActivity.this).dismissProgress();
                calculation();
                initData(currentIndex);
            }
        }
    }

    private void calculation(){
        float d1=0,d2=0,d3=0,d5=0,d7=0,d8=0,d9=0;
        float dd1=0,dd2=0,dd3=0,dd5=0,dd7=0,dd8=0,dd9=0;
        float dy1=0,dy2=0,dy3=0,dy5=0,dy7=0,dy8=0,dy9=0;
        String bjgs = SharedPreferencesUtil.readString("bjg","");
        String zyls = SharedPreferencesUtil.readString("zyl","");
        int vector = 0;//SharedPreferencesUtil.readInt("vector",0);
        if(!"".equals(bjgs) && !"".equals(zyls) && vector !=-1 ) {
            float bjg = Float.parseFloat(bjgs);
            float zyl = Float.parseFloat(zyls);

            float y1 = zyl - measureBeans.get(currentIndex).get(0).getOffsetValue();
            float y2 = zyl - measureBeans.get(currentIndex).get(1).getOffsetValue();
            float y3 = zyl - measureBeans.get(currentIndex).get(2).getOffsetValue();
            float y5 = zyl - measureBeans.get(currentIndex).get(3).getOffsetValue();
            float y7 = zyl - measureBeans.get(currentIndex).get(4).getOffsetValue();
            float y8 = zyl - measureBeans.get(currentIndex).get(5).getOffsetValue();
            float y9 = zyl - measureBeans.get(currentIndex).get(6).getOffsetValue();
            dy1 = SharedPreferencesUtil.readFloat("dy1",0);
            dy2 = SharedPreferencesUtil.readFloat("dy2",0);
            dy3 = SharedPreferencesUtil.readFloat("dy3",0);
            dy5 = SharedPreferencesUtil.readFloat("dy5",0);
            dy7 = SharedPreferencesUtil.readFloat("dy7",0);
            dy8 = SharedPreferencesUtil.readFloat("dy8",0);
            dy9 = SharedPreferencesUtil.readFloat("dy9",0);

            int shanggong = y5 < bjg ? 0 : 1;

            if (shanggong == 0) { //上拱
                dd1 = dy1-bjg+y1;
                dd2 = dy2-bjg+y2;
                dd3 = dy3-bjg+y3;
                dd5 = bjg-y5-dy5;
                dd7 = dy7-bjg+y7;
                dd8 = dy8-bjg+y8;
                dd9 = dy9-bjg+y9;
            } else { //下拱
                dd1 = bjg-dy1-y1;
                dd2 = bjg-dy2-y2;
                dd3 = bjg-dy3-y3;
                dd5 = dy5-bjg+y5;
                dd7 = bjg-dy7-y7;
                dd8 = bjg-dy8-y8;
                dd9 = bjg-dy9-y9;
            }
            measureBeans.get(currentIndex).get(0).setWarp(dd1);
            measureBeans.get(currentIndex).get(1).setWarp(dd2);
            measureBeans.get(currentIndex).get(2).setWarp(dd3);
            measureBeans.get(currentIndex).get(3).setWarp(dd5);
            measureBeans.get(currentIndex).get(4).setWarp(dd7);
            measureBeans.get(currentIndex).get(5).setWarp(dd8);
            measureBeans.get(currentIndex).get(6).setWarp(dd9);
        }
    }

    private LinkedHashMap initTabViewHeader(){
        LinkedHashMap columns = new LinkedHashMap<>();
//        columns.put(0,new Pair<>("传感器编号",1));
        columns.put(0,new Pair<>("尺名称",1));
        columns.put(1,new Pair<>("温度℃",1));
        columns.put(2,new Pair<>("偏移",1));
        columns.put(3,new Pair<>("翘曲",1));
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
