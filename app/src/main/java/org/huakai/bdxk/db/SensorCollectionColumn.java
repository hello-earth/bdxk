package org.huakai.bdxk.db;

import android.provider.BaseColumns;


public class SensorCollectionColumn implements BaseColumns {

    // 列名
    public static final String SENSOR_ID = "SENSOR_ID";
    public static final String SENSOR_DESC = "SENSOR_DESC";
    public static final String DEVICES_MAC = "DEVICES_MAC";


    // 查询结果集
    public static final String[] PROJECTION = {
            SENSOR_ID, SENSOR_DESC, DEVICES_MAC
    };
}
