package org.huakai.bdxk;

import android.app.Application;
import android.content.Context;
import org.huakai.bdxk.db.DaoBase;
import java.lang.Thread.UncaughtExceptionHandler;



/**
 * Created by Administrator on 2017/8/3.
 */

public class MyApplication  extends Application{
    public static final String PACKAGE_NAME = "com.huakai.bdxk";

    private static Context sContext;
    private static MyApplication sInstance;

    private UncaughtExceptionHandler mSystemExcptionHandler;
    private static DaoBase sDaoBase;

    @Override
    public void onCreate() {
        super.onCreate();
        mSystemExcptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        sContext = this;
        sInstance = this;
        sDaoBase = DaoBase.getInstance(sContext);
    }

    public static Application getInstance() {
        if (null == sInstance) {
            sInstance = new MyApplication();
        }
        return sInstance;
    }

    public static DaoBase getDaoBase() {
        return sDaoBase;
    }

    public Context getAppContext() {
        return sContext;
    }

    public void applicationExit() {
        System.exit(0);
    }
}
