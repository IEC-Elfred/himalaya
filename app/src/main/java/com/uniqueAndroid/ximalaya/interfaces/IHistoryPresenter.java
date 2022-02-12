package com.uniqueAndroid.ximalaya.interfaces;

import com.uniqueAndroid.ximalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryPresenter extends IBasePresenter {
    /**
     * 获取历史内容
     */
    void listHistories();

    /**
     * 添加历史
     *
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     *
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史
     */
    void cleanHistories();

}
