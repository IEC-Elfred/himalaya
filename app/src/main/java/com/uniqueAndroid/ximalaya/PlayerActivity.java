package com.uniqueAndroid.ximalaya;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.uniqueAndroid.ximalaya.base.BaseActivity;
import com.uniqueAndroid.ximalaya.presenters.PlayerPresenter;

public class PlayerActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.play();
    }
}
