package com.uniqueAndroid.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {

    /**
     * 专辑详情内容
     * @param tacks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 把album传给ui使用
     * @param album
     */
    void  onAlbumLoaded(Album album);

    void onNetworkError(int errorCode, String errorMsg);

    /**
     * 加载更多的结果
     * @param size size > 0表示加载成功
     */
    void onLoaderMoreFinished(int size);

    /**
     * 下拉加载更多的结果
     * @param size ,size > 0表示加载成功
     */
    void onRefreshFinished(int size);

}
