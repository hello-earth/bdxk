package org.huakai.bdxk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import org.huakai.bdxk.R;
import org.huakai.bdxk.common.SharedPreferencesUtil;

/**
 * Created by Administrator on 2017/8/15.
 */

public class CalibrateSettingActivity extends AppCompatActivity {

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
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_setting_layout);
        findViewById(R.id.com_head_add_layout).setVisibility(View.GONE);
        findViewById(R.id.com_head_setting_layout).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.head_title)).setText("设置标定参数");
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

        confirmButton = (Button)findViewById(R.id.button1);
    }

    private void initListener() {
        View.OnClickListener finish = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalibrateSettingActivity.this.finish();
            }
        };
        titleLeft.setOnClickListener(finish);
        headBackLayout.setOnClickListener(finish);

        View.OnClickListener measure = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String h = editTextH.getText().toString();
                String l = editTextL.getText().toString();
                String lh = editTextLimitH.getText().toString();
                int MaleType = 0;
                if(radioMale4925.isChecked()) MaleType=1;
                else if(radioMale4856.isChecked()) MaleType=2;
                if(!"".equals(h) && !"".equals(l) && !"".equals(lh)){
                    SharedPreferencesUtil.saveString("bjg",h);
                    SharedPreferencesUtil.saveString("zyl",l);
                    SharedPreferencesUtil.saveString("xc",lh);
                    SharedPreferencesUtil.saveInt("maletype",MaleType);
                    SharedPreferencesUtil.saveInt("vector",radio_vector1.isChecked()?0:1);
                    SharedPreferencesUtil.saveString("savedtime",System.currentTimeMillis()+"");
                    CalibrateSettingActivity.this.finish();
                }
            }
        };
        confirmButton.setOnClickListener(measure);
    }

}
