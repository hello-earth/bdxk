package org.huakai.bdxk.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/7.
 */

public class SensorBean implements Parcelable {

    private String sensor_name;
    private String sensor_id;
    private String device_mac;

    public SensorBean(String mac, String sid, String name) {
        sensor_name = name;
        sensor_id = sid;
        device_mac = mac;
    }


    protected SensorBean(Parcel in) {
        sensor_name = in.readString();
        sensor_id = in.readString();
        device_mac = in.readString();
    }

    public String getSensorName() {
        return sensor_name;
    }

    public String getSensorId() {
        return sensor_id;
    }

    public void setSensorId(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public void setSensorName(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getDeviceMac() {
        return device_mac;
    }

    public void setDeviceMac(String sensor_mac) {
        this.device_mac = sensor_mac;
    }

    public static final Creator<SensorBean> CREATOR = new Creator<SensorBean>() {
        @Override
        public SensorBean createFromParcel(Parcel in) {
            return new SensorBean(in);
        }

        @Override
        public SensorBean[] newArray(int size) {
            return new SensorBean[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sensor_name);
        dest.writeString(sensor_id);
        dest.writeString(device_mac);
    }


}
