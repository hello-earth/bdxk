package org.huakai.bdxk.common;

import android.widget.Toast;

import org.huakai.bdxk.MyApplication;


/**
 * showToast为单例，连续使用不会出现toast长时间呆在屏幕上的情况。
 * makeTextAndShow普通的Toast，将makeText和show连接起来。
 * 
 * @author NashLegend
 */
public class ToastUtil {

    public static Toast toast;
    private static final int INIT_POSY = 500;

    /**
     * 单例，连续使用不会出现toast长时间呆在屏幕上的情况，duration为Toast.LENGTH_SHORT
     * 
     */
    public static void showToast(String text) {
        if (toast != null) { 
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.getInstance().getAppContext(), text, Toast.LENGTH_SHORT);
        
//        toast.setGravity(Gravity.CENTER, 0, INIT_POSY);
        toast.show();
    }

    /**
     * 单例，连续使用不会出现toast长时间呆在屏幕上的情况，使用string资源，duration为Toast.LENGTH_SHORT
     * 
     */
    public static void showToast(int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.getInstance().getAppContext(), resId, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, INIT_POSY);
        toast.show();
    }

    /**
     * 单例，连续使用不会出现toast长时间呆在屏幕上的情况，duration为自定义
     * 
     */
    public static void showToast(String text, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.getInstance().getAppContext(), text, duration);
//        toast.setGravity(Gravity.CENTER, 0, INIT_POSY);
        toast.show();
    }

    /**
     * 单例，连续使用不会出现toast长时间呆在屏幕上的情况，使用string资源，duration为自定义
     * 
     */
    public static void showToast(int resId, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.getInstance().getAppContext(), resId, duration);
//        toast.setGravity(Gravity.CENTER, 0, INIT_POSY);
        toast.show();
    }

    /**
     * 普通的Toast，将makeText和show连接起来，duration为Toast.LENGTH_SHORT
     */
    public static void makeTextAndShow(String text) {
        Toast.makeText(MyApplication.getInstance().getAppContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 普通的Toast，将makeText和show连接起来，使用string资源，duration为Toast.LENGTH_SHORT
     * 
     */
    public static void makeTextAndShow(int resId) {
        Toast.makeText(MyApplication.getInstance().getAppContext(), resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 普通的Toast，将makeText和show连接起来，duration为自定义
     * 
     */
    public static void makeTextAndShow(String text,
            int duration) {
        Toast.makeText(MyApplication.getInstance().getAppContext(), text, duration).show();
    }

    /**
     * 普通的Toast，将makeText和show连接起来，使用string资源，duration为自定义
     * 
     */
    public static void makeTextAndShow(int resId, int duration) {
        Toast.makeText(MyApplication.getInstance().getAppContext(), resId, duration).show();
    }

}
