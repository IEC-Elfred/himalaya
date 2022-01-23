package com.uniqueAndroid.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallback {
    /**
     * 获取推荐内容的结果
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);


    void onNetworkError();

    void onEmpty();

    void onLoading();

    /**
     * 上拉加载更多
     * @param result
     */
    void onLoaderMore(List<Album> result);

    /**
     * 下拉加载更多内容
     */
    void onRefreshMore(List<Album> result);
}
