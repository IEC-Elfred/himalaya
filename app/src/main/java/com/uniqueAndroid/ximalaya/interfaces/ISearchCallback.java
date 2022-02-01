package com.uniqueAndroid.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

import java.util.List;

public interface ISearchCallback {

    void onSearchResultLoad(List<Album> result);

    void onHotWordLoad(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     * @param result 结果
     * @param isOkay true表示加载更多成功，false表示没有更多
     */
    void onLoadMoreResult(List<Album> result,boolean isOkay);


}
