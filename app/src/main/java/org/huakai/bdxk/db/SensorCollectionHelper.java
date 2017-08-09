package org.huakai.bdxk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.huakai.bdxk.common.SensorBean;

import java.util.ArrayList;
import java.util.List;


public class SensorCollectionHelper {

    private final Context mContext;

    private DaoBase mDbHelper;

    private SQLiteDatabase mSqlDB;

    public SensorCollectionHelper(Context context) {
        this.mContext = context;
    }

    /**
     * 初始化数据库连接
     */
    public SensorCollectionHelper open() {
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
    public long insertSensorCollectionInfo(String mac, String description, String sensorid) {
        long insertCount = 0;
        if (!isHasSensorInfo(sensorid)) {
            ContentValues values = new ContentValues();
            values.put(SensorCollectionColumn.SENSOR_ID, sensorid);
            values.put(SensorCollectionColumn.SENSOR_DESC, description);
            values.put(SensorCollectionColumn.DEVICES_MAC, mac);
            try {
                insertCount = mSqlDB.insert(DaoBase.SENSOR_LIST, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return insertCount;
    }

    public boolean isHasSensorInfo(String sensorid) {
        boolean result = false;
        SQLiteDatabase mSqlDB = mDbHelper.getReadableDatabase();
        String selection = SensorCollectionColumn.SENSOR_ID + "='" + sensorid + "'";
        try {
            Cursor cursor = mSqlDB.query(DaoBase.SENSOR_LIST, null, selection, null, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    result = true;
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<SensorBean> getAllSensors(){
        List<SensorBean> sensorBean = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase mSqlDB = mDbHelper.getReadableDatabase();
            cursor = mSqlDB.query(DaoBase.SENSOR_LIST, SensorCollectionColumn.PROJECTION, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String sid = cursor.getString(cursor.getColumnIndex(SensorCollectionColumn.SENSOR_ID));
                        String mac = cursor.getString(cursor.getColumnIndex(SensorCollectionColumn.DEVICES_MAC));
                        String desc = cursor.getString(cursor.getColumnIndex(SensorCollectionColumn.SENSOR_DESC));
                        sensorBean.add(new SensorBean(mac,sid,desc));
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
        return sensorBean;
    }

    /**
     * 删除
     */
    public boolean delete(String sid) {
        String selection = SensorCollectionColumn.SENSOR_ID + "='" + sid + "'";
        int ret = mSqlDB.delete(DaoBase.SENSOR_LIST, selection, null);
        return ret > 0 ? true : false;
    }

    /**
     * 清空
     */
    public boolean truncate()
    {
        int ret = mSqlDB.delete(DaoBase.SENSOR_LIST, null, null);
        return ret>0?true:false;
    }
}
