package com.uniqueAndroid.ximalaya.presenters;

import com.uniqueAndroid.ximalaya.data.XimalayaApi;
import com.uniqueAndroid.ximalaya.interfaces.IRecommendPresenter;
import com.uniqueAndroid.ximalaya.interfaces.IRecommendViewCallback;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> callbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter() {

    }

    private static RecommendPresenter sInstance = null;

    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getRecommendList() {
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    albumList.addAll(gussLikeAlbumList.getAlbumList());
                    //updateRecommendUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error----->" + i);
                LogUtil.d(TAG, "errorMsg----->" + s);
                handleError();
            }
        });
    }

    private void handleError() {
        if (callbacks != null) {
            for (IRecommendViewCallback callback : callbacks) {
                callback.onNetworkError();
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommendViewCallback callback : callbacks) {
                    callback.onEmpty();
                }
            } else {
                for (IRecommendViewCallback callback : callbacks) {
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    /**
     * 获取当前的推荐专辑列表
     *
     * @return 推荐专辑列表，使用之前要判空
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }

    private void updateLoading() {
        for (IRecommendViewCallback callback : callbacks) {
            callback.onLoading();
        }
    }


    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (callbacks != null && !callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (callbacks != null) {
            callbacks.remove(callback);
        }
    }
}
