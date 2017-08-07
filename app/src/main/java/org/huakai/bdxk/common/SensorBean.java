package org.huakai.bdxk.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/7.
 */

public class SensorBean implements Parcelable {

    private String sensor_name;
    private String sensor_id;

    public SensorBean(String name, String mac) {
        sensor_name = name;
        sensor_id = mac;
    }


    protected SensorBean(Parcel in) {
        sensor_name = in.readString();
        sensor_id = in.readString();
    }

    public String getSensorName() {
        return sensor_name;
    }

    public String getSensorId() {
        return sensor_id;
    }

    public void setSensorId(String sensor_mac) {
        this.sensor_id = sensor_mac;
    }

    public void setSensorName(String sensor_name) {
        this.sensor_name = sensor_name;
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
    }


}
