package com.uniqueAndroid.ximalaya.interfaces;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    void onPlayStart();

    void onPlayPause();

    void onPlayStop();

    void onPlayError();

    void nextPlay(Track track);

    void onPrePlay(Track track);

    /**
     * 播放列表数据加载完成
     *
     * @param list
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放器模式改变
     *
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    void onProgressChange(long currentProgress, long total);

    /**
     * 广告加载
     */
    void onAdLoading();

    void onAdFinished();

    /**
     * 更新当前节目的标题
     * @param track
     */
    void onTrackUpdate(Track track, int playIndex);
}
