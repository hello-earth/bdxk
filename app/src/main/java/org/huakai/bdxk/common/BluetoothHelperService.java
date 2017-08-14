package org.huakai.bdxk.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/28.
 */

public class BluetoothHelperService {
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothHelperService";
    private static int mState;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    private static Handler mHandler;
    private static ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;
    private static BluetoothHelperService bluetoothHelperService;

    private BluetoothHelperService(Context context, Handler handler) {
        mState = STATE_NONE;
        mHandler = handler;
    }

    public static BluetoothHelperService getInstance(Context context, Handler handler){
        if(mState!=STATE_CONNECTED || bluetoothHelperService==null || mConnectedThread==null || !mConnectedThread.isConnected()){
            bluetoothHelperService = new BluetoothHelperService(context,handler);
        }else{
            mHandler = handler;
        }
        return bluetoothHelperService;
    }


    public static boolean isConnected(){
        return mState==STATE_CONNECTED && mConnectedThread!=null && mConnectedThread.isConnected();
    }
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if(device==null) return;
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);
        mState = STATE_CONNECTED;
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        if(mHandler!=null){
            Message msg = new Message();
            msg.obj = device;
            msg.what = MessageType.MESSAGE_CONNECTED;
            mHandler.sendMessage(msg);
        }

    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);
            mState = STATE_CONNECTING;
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                Log.e(TAG, "call connect() " + mSocketType +
                        " socket during connection failure", e);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothHelperService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int length=0, datalength = 0;
            String respond = "";
            Message msg = new Message();
            msg.what = MessageType.MESSAGE_DISCONNECTED;

            // Keep listening to the InputStream while connected
            while (mmInStream!=null && mmSocket.isConnected()) {
                try {
                    // Read from the InputStream
                    int bylen = mmInStream.read(buffer);
                    if(bylen==0)
                        continue;
                    byte[] tmp = new byte[bylen];
                    length += bylen;
                    System.arraycopy(buffer, 0, tmp, 0, bylen);
                    respond += ByteUtils.byteToHexStr(tmp);
                    if(length>=10 && datalength==0){
                        datalength =  Integer.parseInt(respond.substring(6,10), 16);
                    }
                    if(datalength!=0 && respond.length()/2>=datalength+7){
                        if (mHandler!=null){
                            msg.what = MessageType.MESSAGE_READ;
                            msg.obj = respond;
                            mHandler.sendMessage(msg);
                        }
                        Log.d(TAG,"respond's length="+respond.length()+" and datalength's length="+datalength + ". \ndata rev "+respond);
                        length=0;
                        datalength = 0;
                        respond = "";
                        msg = new Message();
                        msg.what =  -1;
                    }
                } catch (IOException e) {
                    msg.what = MessageType.MESSAGE_DISCONNECTED;
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                    msg.what = MessageType.MESSAGE_DISCONNECTED;
                    break;
                }
            }
            mHandler.sendMessage(msg);
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            Log.i(TAG, "BEGIN mConnectedThread write");
            try {
                mmOutStream.write(buffer);
                mmOutStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
                Message msg = new Message();
                msg.what = MessageType.MESSAGE_DISCONNECTED;
                mHandler.sendMessage(msg);
                this.cancel();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

        public boolean isConnected(){
            return mmSocket.isConnected();
        }
    }
}
