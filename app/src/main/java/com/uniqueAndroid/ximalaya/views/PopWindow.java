package com.uniqueAndroid.ximalaya.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.base.BaseApplication;

public class PopWindow extends PopupWindow {
    public PopWindow() {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        //载入view
        View popView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(popView);
        //进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
    }

}
