package com.uniqueAndroid.ximalaya.interfaces;

import com.uniqueAndroid.ximalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback extends IBasePresenter<IHistoryCallback> {

    /**
     * 历史内容加载结果
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);
    
}
