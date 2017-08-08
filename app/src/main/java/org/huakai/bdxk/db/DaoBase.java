
package org.huakai.bdxk.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DaoBase extends SQLiteOpenHelper {
    public static final String DEVICE_LIST = "device_list";
    public static final String DATA_LIST = "data_list";
    public static final String SENSOR_LIST = "sensor_list";
    private static final int DATABASE_VERSION = 1;
    private static String sDatabaseName = "dbhk.db";
    private static DaoBase sDaoBaseInstance = null;

    public SQLiteDatabase mDatabase = null;

    public DaoBase(Context context) {
        super(context, sDatabaseName, null, DATABASE_VERSION);
    }

    public static DaoBase getInstance(Context context) {
        if (sDaoBaseInstance == null) {
            sDaoBaseInstance = new DaoBase(context);
        }

        return sDaoBaseInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDeviceDB(db);
        createSensorDB(db);
    }

    private void createDeviceDB(SQLiteDatabase db) {
        String info_sql = "CREATE TABLE IF NOT EXISTS " + DEVICE_LIST + "("
                + DevicesCollectionColumn.DEVICES_ID + "  varchar(10)  default '',"
                + DevicesCollectionColumn.DEVICES_DESC + " varchar(50)  default '',"
                + DevicesCollectionColumn.DEVICES_MAC + " varchar(50)  default '')";
        try {
            db.execSQL(info_sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSensorDB(SQLiteDatabase db) {
        String info_sql = "CREATE TABLE IF NOT EXISTS " + SENSOR_LIST + "("
                + SensorCollectionColumn.SENSOR_ID + "  varchar(28)  default '',"
                + SensorCollectionColumn.SENSOR_DESC + " varchar(30)  default '',"
                + SensorCollectionColumn.DEVICES_MAC + " varchar(50)  default '')";
        try {
            db.execSQL(info_sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(" DROP TABLE IF EXISTS " + DEVICE_LIST);
            db.execSQL(" DROP TABLE IF EXISTS " + DATA_LIST);
            db.execSQL(" DROP TABLE IF EXISTS " + SENSOR_LIST);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void deleteAllData() {
        execSQL("Delete from " + DEVICE_LIST);
        execSQL("Delete from " + DATA_LIST);
        execSQL("Delete from " + SENSOR_LIST);
    }

    public Cursor select(String sql){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public synchronized void execSQL(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public long insert(String table, ContentValues initialValues) {
        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = getWritableDatabase();
        long rowId = -1;

        try {
            rowId = db.insert(table, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return rowId;
    }

    public int delete(String table, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int count = -1;

        try {
            count = db.delete(table, selection, selectionArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return count;
    }

    public int update(String table, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int count = -1;

        try {
            count = db.update(table, values, selection, selectionArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return count;
    }

    public void beginTransaction() {
        beginTransaction();
    }

    public void setTransactionSuccessful() {
        setTransactionSuccessful();
    }

    public void endTransaction() {
        endTransaction();
    }
}
