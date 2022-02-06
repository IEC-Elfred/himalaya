package com.uniqueAndroid.ximalaya.interfaces;

import com.uniqueAndroid.ximalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionPresenter> {

    /**
     * 添加订阅
     *
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     *
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     */
    void getSubscriptionList();

}
