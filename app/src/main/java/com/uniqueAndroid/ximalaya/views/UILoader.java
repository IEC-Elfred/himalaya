package com.uniqueAndroid.ximalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.utils.LogUtil;

public abstract class UILoader extends FrameLayout {

    private static final String TAG = "UILoader";
    private View loadingView;
    private View successView;
    private View networkErrorView;
    private View emptyView;
    private OnRetryClickListener mOnRetryClickListener = null;

    public enum UIStatus {
        LOADING, SUCCESS, NETWORK_ERROR, EMPTY, NONE
    }

    public UIStatus mCurrentStatus = UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context, null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void updateStatus(UIStatus status) {
        mCurrentStatus = status;
        //更新UI一定要在主线程
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    private void init() {
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        LogUtil.d(TAG, "currentStatus----> " + mCurrentStatus);
        //加载中
        if (loadingView == null) {
            loadingView = getLoadingView();
            addView(loadingView);
        }
        //根据状态设置是否可见
        loadingView.setVisibility(mCurrentStatus == UIStatus.LOADING ? VISIBLE : GONE);

        //成功
        if (successView == null) {
            successView = getSuccessView(this);
            addView(successView);
        }

        successView.setVisibility(mCurrentStatus == UIStatus.SUCCESS ? VISIBLE : GONE);

        //网络错误页面
        if (networkErrorView == null) {
            networkErrorView = getNetworkErrorView();
            addView(networkErrorView);
        }
        //根据状态设置是否可见
        networkErrorView.setVisibility(mCurrentStatus == UIStatus.NETWORK_ERROR ? VISIBLE : GONE);

        //数据为空页面
        if (emptyView == null) {
            emptyView = getEmptyView();
            addView(emptyView);
        }
        //根据状态设置是否可见
        emptyView.setVisibility(mCurrentStatus == UIStatus.EMPTY ? VISIBLE : GONE);
    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
    }

    protected View getNetworkErrorView() {
        View networkErrorView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_network_error_view, this, false);
        networkErrorView.findViewById(R.id.network_error_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return networkErrorView;
    }

    ;

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view, this, false);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        this.mOnRetryClickListener = listener;
    }

    public interface OnRetryClickListener {
        void onRetryClick();
    }

}
