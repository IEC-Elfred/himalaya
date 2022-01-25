package com.uniqueAndroid.ximalaya.presenters;

import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerCallback;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerPresenter;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;
import java.util.logging.Logger;

public class PlayerPresenter implements IPlayerPresenter {

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayerListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (mPlayerManager != null) {
            isPlayerListSet = true;
            mPlayerManager.setPlayList(list, playIndex);
        } else {
            LogUtil.d(TAG,"mPlayerManager is null");
        }
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {

    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {

    }

    @Override
    public void play() {
        if (isPlayerListSet) {
            LogUtil.d(TAG,"Play!!!");
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {

    }
}
