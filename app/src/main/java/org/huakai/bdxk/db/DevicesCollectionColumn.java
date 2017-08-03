package org.huakai.bdxk.db;

import android.provider.BaseColumns;


public class DevicesCollectionColumn implements BaseColumns {

    // 列名
    public static final String DEVICES_ID = "DEVICES_ID";
    public static final String DEVICES_DESC = "DEVICES_DESC";
    public static final String DEVICES_MAC = "DEVICES_MAC";


    // 查询结果集
    public static final String[] PROJECTION = {
            DEVICES_ID, DEVICES_DESC, DEVICES_MAC
    };
}
