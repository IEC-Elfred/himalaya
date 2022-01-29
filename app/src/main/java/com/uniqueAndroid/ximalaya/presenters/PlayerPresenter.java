package com.uniqueAndroid.ximalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.uniqueAndroid.ximalaya.api.XimalayaApi;
import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerCallback;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerPresenter;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;
    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();
    private Track mCurrentTrack;
    private List<Track> mPlayList;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private boolean mIsReverse = false;
    private final SharedPreferences mPlayModeSp;
    public static final int DEFAULT_PLAY_INDEX = 0;
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    public static final int PLAY_MODE_LIST_INT = 0;
    public static final int PLAY_MODE_LIST_LOOP_INT = 1;
    public static final int PLAY_MODE_RANDOM_INT = 2;
    public static final int PLAY_MODE_SINGLE_LOOP_INT = 3;
    //sp's name and key
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    public static final String PLAY_MODE_SP_KEY = "mCurrentPlayMode";
    private int mProgressDuration = 0;
    private int mCurrentProgressPosition = 0;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //注册广告物料相关的借口
        mPlayerManager.addAdsStatusListener(this);
        mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences("playMode", Context.MODE_PRIVATE);
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
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            LogUtil.d(TAG, "mPlayerManager is null");
        }
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        //通知当前的节目
        iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentProgressPosition,mProgressDuration);
        //更新状态
        handlePlayState(iPlayerCallback);
        int anInt = mPlayModeSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODE_LIST_INT);
        mCurrentPlayMode = getModeByPlayInt(anInt);
        iPlayerCallback.onPlayModeChange(getModeByPlayInt(anInt));
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerCallback.onPlayStart();
        } else {
            iPlayerCallback.onPlayPause();
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
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            //通知ui更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            SharedPreferences.Editor edit = mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY, getIntByPlayMode(mode));
            edit.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch (mode) {
            case PLAY_MODEL_LIST:
                return PLAY_MODE_LIST_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODE_RANDOM_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODE_LIST_LOOP_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODE_SINGLE_LOOP_INT;
        }
        return PLAY_MODE_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByPlayInt(int anInt) {
        switch (anInt) {
            case PLAY_MODE_LIST_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
            case PLAY_MODE_RANDOM_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
            case PLAY_MODE_LIST_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
            case PLAY_MODE_SINGLE_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;
        }
        return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
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
            mCurrentIndex = index;
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        return mPlayerManager.isPlaying();

    }

    @Override
    public void reversePlayList() {
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList, mCurrentIndex);
        //更新ui
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        // TODO: 2022/1/29
        // 1. 获取到专辑的列表内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                // 2. 把专辑内容设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks, 0);
                    isPlayerListSet = true;
                    mCurrentTrack = tracks.get(0);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"errorCode--->" + i);
                LogUtil.d(TAG,"errorCode--->" + s);
                Toast.makeText(BaseApplication.getAppContext(), "请求数据错误...", Toast.LENGTH_SHORT).show();

            }
        }, id, 1);

        // 3. 播放了

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
        mPlayerManager.setPlayMode(mCurrentPlayMode);
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
            mCurrentTrack = (Track) curModel;
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
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
        this.mCurrentProgressPosition = currentPos;
        this.mProgressDuration = duration;
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

    public boolean hasPlayList() {
        return isPlayerListSet;
    }

    //=========播放器相关end=======


}
