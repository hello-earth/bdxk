package org.huakai.bdxk.common;


import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;


public class ScanResult implements Parcelable {

    private BluetoothDevice mDevice;
    private int mRssi;

    public ScanResult(BluetoothDevice device, int rssi) {
        mDevice = device;
        mRssi = rssi;
    }


    protected ScanResult(Parcel in) {
        mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        mRssi = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mDevice, flags);
        dest.writeInt(mRssi);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanResult> CREATOR = new Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel in) {
            return new ScanResult(in);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

}
