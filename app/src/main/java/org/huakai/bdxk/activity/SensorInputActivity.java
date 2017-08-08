package org.huakai.bdxk.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.huakai.bdxk.R;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SensorInputActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout headBackLayout;
    private ImageView titleLeft;
    private LinearLayout headConfirmLayout;
    private TextView confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muti_input_dialog_layout);
        ((TextView)findViewById(R.id.head_title)).setText("添加传感器");
        headBackLayout = (LinearLayout) findViewById(R.id.com_head_back_layout);
        titleLeft = (ImageView)findViewById(R.id.com_head_back);
        headConfirmLayout = (LinearLayout) findViewById(R.id.com_head_add_confirm_layout);
        confirmButton = (TextView)findViewById(R.id.com_head_add_confirm);
        initListener();
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
                SensorInputActivity.this.finish();
                break;
        }
    }
}
