package org.huakai.bdxk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.huakai.bdxk.R;
import org.huakai.bdxk.common.ToastUtil;
import org.huakai.bdxk.db.SensorCollectionHelper;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SensorInputActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private LinearLayout headConfirmLayout;
    private TextView confirmButton;
    private EditText sensorDesc;
    private EditText sensorId;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_addcation);
        ((TextView)findViewById(R.id.head_title)).setText("添加传感器");
        address = getIntent().getExtras().getString("device_mac");
        initView();
        initListener();
    }

    private void initView(){
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        headConfirmLayout = (LinearLayout) findViewById(R.id.com_head_add_confirm_layout);
        confirmButton = (TextView)findViewById(R.id.com_head_add_confirm);
        sensorDesc = (EditText)findViewById(R.id.sensor_desc);
        sensorId = (EditText)findViewById(R.id.sensor_id);
        sensorDesc.requestFocus();
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
                    sensorHelper.insertSensorCollectionInfo(address,sensorDesc.getText().toString(),sensorId.getText().toString());
                    SensorInputActivity.this.finish();
                }
                break;
        }
    }

    private boolean inputAllhaveContent(){
        String desc = sensorDesc.getText().toString();
        String id = sensorId.getText().toString();
        return !"".equals(desc) && id.length()==16;
    }


}
