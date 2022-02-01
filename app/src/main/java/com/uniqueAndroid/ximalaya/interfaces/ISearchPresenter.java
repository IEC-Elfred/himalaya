package com.uniqueAndroid.ximalaya.interfaces;

import com.uniqueAndroid.ximalaya.base.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {

    void doSearch(String keyword);

    void reSearch();

    /**
     * 加载更多的搜索结果
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 获取推荐的关键字（相关的关键字）
     * @param keyword
     */
    void getRecommendWord(String keyword);
}
