package com.uniqueAndroid.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubDaoCallback {

    /**
     * 添加的结果回调方法
     *
     * @param isSuccess
     */
    void
    onAddResult(boolean isSuccess);

    /**
     * 删除结果回调方法
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 加载的结果
     *
     * @param result
     */
    void onSubListLoaded(List<Album> result);

}
