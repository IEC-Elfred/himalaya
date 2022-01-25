package com.uniqueAndroid.ximalaya.presenters;

import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerCallback;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerPresenter;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;
    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();
    private Track mTrack;
    private List<Track> mPlayList;
    private int mCurrentIndex = 0;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //注册广告物料相关的借口
        mPlayerManager.addAdsStatusListener(this);
        mPlayerManager.addPlayerStatusListener(this);
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
            mTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            LogUtil.d(TAG, "mPlayerManager is null");
        }
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackTitleUpdate(mTrack,mCurrentIndex);
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        mIPlayerCallbacks.remove(iPlayerCallback);
    }

    @Override
    public void play() {
        if (isPlayerListSet) {
            LogUtil.d(TAG, "Play!!!");
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            mPlayList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(mPlayList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        return mPlayerManager.isPlaying();

    }


    //==================广告相关回调===============

    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG, "onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG, "onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG, "onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG, "onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG, "onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG, "onCompletePlayAds");
    }

    @Override
    public void onError(int i, int i1) {
        LogUtil.d(TAG, "errorCode" + i + "extraInfo" + i1);
    }

    //========播放器相关==========

    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, "onPlayStart");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG, "onPlayPause");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG, "onPlayStop");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG, "onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG, "onSoundPrepared");
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG, "onSoundSwitch");
        if (lastModel != null) {
            LogUtil.d(TAG, "lastModel--->" + lastModel.getKind());
        }
        LogUtil.d(TAG, "curModel--->" + curModel.getKind());
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        //curModel是当前播放的内容
        if (curModel instanceof Track) {
            mTrack = (Track) curModel;
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackTitleUpdate(mTrack,mCurrentIndex);
            }
        }

    }


    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG, "onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG, "onBufferingStop");
    }


    @Override
    public void onBufferProgress(int i) {
        LogUtil.d(TAG, "onBufferProgress" + i);
    }

    @Override
    public void onPlayProgress(int currentPos, int duration) {
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currentPos, duration);
        }
        LogUtil.d(TAG, "onPlayProgress--->" + currentPos + "duration -- >" + duration);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG, "Exception------->" + e);
        return false;
    }

    //=========播放器相关end=======


}
