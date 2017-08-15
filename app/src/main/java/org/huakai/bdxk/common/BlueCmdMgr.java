package org.huakai.bdxk.common;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import org.huakai.bdxk.MyApplication;
import org.huakai.bdxk.view.CustomLoadView;

/**
 * Created by Administrator on 2017/8/15.
 */

public class BlueCmdMgr {

    private static BlueCmdMgr mgr;
    private static Context mContext;
    private static BluetoothHelperService mChatService;
    private static BluetoothDevice device;

    private BlueCmdMgr(BluetoothHelperService mChatService, BluetoothDevice device){
        this.mChatService = mChatService;
        this.device = device;
    }

    public static BlueCmdMgr getInstance(Context mContext, BluetoothHelperService mChatService, BluetoothDevice device){
        if(mgr==null){
            mgr = new BlueCmdMgr(mChatService,device);
        }
        BlueCmdMgr.mChatService = mChatService;
        BlueCmdMgr.mContext = mContext;
        return mgr;
    }

    public void connect(){
        if(mChatService!=null){
            if(!mChatService.isConnected()) {
                mChatService.connect(device, false);
            }
        }else{
            ToastUtil.makeTextAndShow("参数非法");
        }
    }

    public void sendCmd(String orderHex){
        if(mChatService!=null && !mChatService.isConnected()){
            this.connect();
        }else {
            byte[] data = ByteUtils.hexStringToBytes(orderHex);
            mChatService.write(data);
        }
    }

    public void stop() {
        if(mChatService!=null && mChatService.isConnected()){
            mChatService.stop();
        }
    }

    public boolean isConnected() {
        return mChatService!=null && mChatService.isConnected();
    }
}
