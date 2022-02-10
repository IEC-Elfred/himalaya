package com.uniqueAndroid.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {

    /**
     * 调用添加的时候，去通知ui结果
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除订阅的回调方法
     *
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 订阅专辑加载结果回调
     *
     * @param albums
     */
    void onSubscriptionLoaded(List<Album> albums);

    /**
     * 订阅数量满了
     */
    void onSubFull();
}
