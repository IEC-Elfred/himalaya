package com.uniqueAndroid.ximalaya.interfaces;

import com.uniqueAndroid.ximalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    void play();

    void pause();

    void stop();

    void playPre();

    void playNext();

    void switchPlayMode(XmPlayListControl.PlayMode mode);

    void getPlayList();

    void playByIndex(int index);

    void seekTo(int progress);

    boolean isPlaying();

    /**
     * 把播放器列表反转
     */
    void reversePlayList();
}
