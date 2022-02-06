package com.uniqueAndroid.ximalaya.presenters;


import com.uniqueAndroid.ximalaya.data.XimalayaApi;
import com.uniqueAndroid.ximalaya.interfaces.IAlbumDetailPresenter;
import com.uniqueAndroid.ximalaya.interfaces.IAlbumDetailViewCallback;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;


public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private Album mTargetAlbum = null;

    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();
    private int mCurrentAlbumId = -1;
    private int mCurrentPageIndex = 0;

    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        mCurrentPageIndex++;
        //结追加到列表后方
        doLoad(true);
    }

    private void doLoad(boolean isLoaderMore) {
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    if (isLoaderMore) {
                        //上拉加载，结果放到后面
                        mTracks.addAll(tracks);
                        handlerLoaderMoreResult(tracks.size());
                    } else {
                        //下拉加载，结果放到前面
                        mTracks.addAll(0, tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (isLoaderMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG, "ErrorCode---->" + i);
                LogUtil.d(TAG, "ErrorMsg---->" + s);
                handlerError(i, s);
            }
        }, mCurrentAlbumId, mCurrentPageIndex);

    }

    /**
     * 处理加载更多的结果
     *
     * @param size >0意味着加载成功
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumID, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumID;
        this.mCurrentPageIndex = page;
        doLoad(false);
    }

    private void handlerError(int i, String s) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetworkError(i, s);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum != null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (mCallbacks.contains(detailViewCallback)) {
            mCallbacks.remove(detailViewCallback);
        }
    }

    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
