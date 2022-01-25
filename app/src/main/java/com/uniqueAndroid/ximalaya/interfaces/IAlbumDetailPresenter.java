package com.uniqueAndroid.ximalaya.interfaces;

import com.uniqueAndroid.ximalaya.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上接加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     */
    void getAlbumDetail(int albumID, int page);


}
