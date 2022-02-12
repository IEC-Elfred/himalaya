package com.uniqueAndroid.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {

    void onHistoryAdd(boolean isSuccess);

    void onHistoryDel(boolean isSuccess);

    void onHistoriesLoaded(List<Track> tracks);

    void onHistoriesClean(boolean isSuccess);
}
