package org.huakai.bdxk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.db.SensorCollectionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SensorInputActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private LinearLayout headConfirmLayout;
    private TextView confirmButton;
    private Spinner sensorDesc;
    private EditText sensorId;
    private String address;
    private ArrayList<String> spinnerlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_addcation);
        ((TextView)findViewById(R.id.head_title)).setText("添加传感器");
        address = getIntent().getExtras().getString("device_mac");
        initView();
        initSpinnerData();
        initListener();
    }

    private void initView(){
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        headConfirmLayout = (LinearLayout) findViewById(R.id.com_head_add_confirm_layout);
        confirmButton = (TextView)findViewById(R.id.com_head_add_confirm);
        sensorDesc = (Spinner)findViewById(R.id.sensor_desc);
        sensorId = (EditText)findViewById(R.id.sensor_id);
        sensorDesc.requestFocus();
    }

    private void initSpinnerData() {
        spinnerlist = new ArrayList<>();
        spinnerlist.add("1号尺");
        spinnerlist.add("2号尺");
        spinnerlist.add("3号尺");
        spinnerlist.add("5号尺");
        spinnerlist.add("7号尺");
        spinnerlist.add("8号尺");
        spinnerlist.add("9号尺");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerlist);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sensorDesc.setAdapter(adapter);
    }

    private void initListener(){
        headBackLayout.setOnClickListener(this);
        titleLeft.setOnClickListener(this);
        headConfirmLayout.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.com_head_back_layout:
            case R.id.com_head_back:
                SensorInputActivity.this.finish();
                break;
            case R.id.com_head_add_confirm_layout:
            case R.id.com_head_add_confirm:
                if(!inputAllhaveContent())
                    ToastUtil.makeTextAndShow("请正确填写信息");
                else {
                    SensorCollectionHelper sensorHelper = new SensorCollectionHelper(this);
                    sensorHelper.open();
                    sensorHelper.insertSensorCollectionInfo(address,spinnerlist.get(sensorDesc.getSelectedItemPosition()),sensorId.getText().toString());
                    sensorHelper.close();
                    SensorInputActivity.this.finish();
                }
                break;
        }
    }

    private boolean inputAllhaveContent(){
        String id = sensorId.getText().toString();
        return id.length()==16;
    }


}
