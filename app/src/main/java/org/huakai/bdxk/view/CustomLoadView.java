
package org.huakai.bdxk.view;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.huakai.bdxk.R;


public class CustomLoadView extends FrameLayout {

    private static Context mContext;
    private Context mLastContext;
    private static CustomLoadView mProgressView;
    private ViewGroup mLayoutLoading;
    private ProgressBar mPgLoading;
    private TextView mTvLoading;
    private FrameLayout mRootContainer;
    private static int timeout = 0;

    public CustomLoadView(Context context) {
        super(context);
        initView(context);
    }

    public CustomLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public static CustomLoadView getInstance(Context context) {
        mContext = context;
        timeout = 0;
        if (mProgressView == null) {
            mProgressView = new CustomLoadView(context);
        }
        return mProgressView;
    }

    public static CustomLoadView getInstance(Context context,int _timeout) {
        mContext = context;
        timeout = _timeout;
        if (mProgressView == null) {
            mProgressView = new CustomLoadView(context);
        }
        return mProgressView;
    }

    private void initView(Context context) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);
        LayoutInflater.from(context).inflate(R.layout.custom_view_loading, this);
        mLayoutLoading = (ViewGroup) findViewById(R.id.layout_loading);
        mPgLoading = (ProgressBar) findViewById(R.id.pg_loading);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
    }

    private void setLoadingMsg(String message) {
        mTvLoading.setText("请稍后^-^");
//        mTvLoading.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(message)) {
            mTvLoading.setText(message);
            mTvLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * loading是否可见
     */
    private void setLodingVisible(boolean isVisible) {
        if (isVisible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * 显示loading
     */
    public void showProgress() {
        showProgress("");
    }

    public void showProgress(String message) {
        mLastContext = mContext;
        if (mContext != null && mContext instanceof Activity) {
            Activity act = ((Activity) mContext);
            if (act.isFinishing()) {
                return;
            } else {
                dismissProgress();
                mRootContainer = (FrameLayout) act.findViewById(android.R.id.content);
                mRootContainer.addView(this);
                setLoadingMsg(message);
                setLodingVisible(true);
                if(timeout>0)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgress();
                        }
                    },timeout);
            }
        }
    }

    /**
     * 隐藏loading
     */
    public void dismissProgress() {
        if (mContext == mLastContext) {
            setLodingVisible(false);
            if (mRootContainer != null) {
                mRootContainer.removeView(this);
            }
        }
    }

}
