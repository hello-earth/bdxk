package org.huakai.bdxk.db;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DevicesCollectionHelper {

    private final Context mContext;

    private DaoBase mDbHelper;

    private SQLiteDatabase mSqlDB;

    public DevicesCollectionHelper(Context context) {
        this.mContext = context;
    }

    /**
     * 初始化数据库连接
     */
    public DevicesCollectionHelper open() {
        mDbHelper = DaoBase.getInstance(mContext);
        mSqlDB = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
        }
        if (mSqlDB != null) {
            mSqlDB.close();
            mSqlDB = null;
        }
    }

    /**
     * 插入一条记录
     *
     //* @param info
     * @return
     */
    public long insertDeviceCollectionInfo(BluetoothDevice info, String description) {
        long insertCount = isHasDeviceInfo(info,description);
        if (insertCount==0) {
            ContentValues values = new ContentValues();
            values.put(DevicesCollectionColumn.DEVICES_ID, info.getType());
            values.put(DevicesCollectionColumn.DEVICES_DESC, description);
            values.put(DevicesCollectionColumn.DEVICES_MAC, info.getAddress());
            try {
                insertCount = mSqlDB.insert(DaoBase.DEVICE_LIST, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return insertCount;
    }

    public int isHasDeviceInfo(BluetoothDevice info, String description) {
        int result = 0;
        SQLiteDatabase mSqlDB = mDbHelper.getReadableDatabase();
        String selection = DevicesCollectionColumn.DEVICES_MAC + "='" + info.getAddress() + "'";
        try {
            Cursor cursor = mSqlDB.query(DaoBase.DEVICE_LIST, null, selection, null, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    result = -1;
                }else{
                    selection = DevicesCollectionColumn.DEVICES_DESC + "='" + description + "'";
                    cursor = mSqlDB.query(DaoBase.DEVICE_LIST, null, selection, null, null,
                            null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            result = -2;
                        }
                    }
                }
                cursor.close();
            }else{
                selection = DevicesCollectionColumn.DEVICES_DESC + "='" + description + "'";
                cursor = mSqlDB.query(DaoBase.DEVICE_LIST, null, selection, null, null,
                        null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        result = -2;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 删除
     */
    public boolean delete(String description) {
        String selection = DevicesCollectionColumn.DEVICES_DESC + "='" + description + "'";
        int ret = mSqlDB.delete(DaoBase.DEVICE_LIST, selection, null);
        return ret > 0 ? true : false;
    }

    public String getDescByMac(String mac) {
        String description = "";
        Cursor cursor = null;
        try {
            SQLiteDatabase mSqlDB = mDbHelper.getReadableDatabase();
            String selection = DevicesCollectionColumn.DEVICES_MAC + "='" + mac + "'";
            cursor = mSqlDB.query(DaoBase.DEVICE_LIST, null, selection, null, null,
                    null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        description = cursor.getString(cursor.getColumnIndex(DevicesCollectionColumn.DEVICES_DESC));
                    }
                }
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }finally {
            if (cursor != null) cursor.close();
        }
        return description;
    }

    /**
     * 清空
     */
    public boolean truncate()
    {
        int ret = mSqlDB.delete(DaoBase.DEVICE_LIST, null, null);
        return ret>0?true:false;
    }
}
