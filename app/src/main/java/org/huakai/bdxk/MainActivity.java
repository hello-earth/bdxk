package org.huakai.bdxk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import org.huakai.bdxk.activity.DeviceListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpNextActivity();
            }
        }, 3000);
    }

    private void jumpNextActivity() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivity(intent);
        finish();
    }
}
