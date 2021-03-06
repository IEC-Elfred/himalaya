package com.uniqueAndroid.ximalaya.presenters;

import com.uniqueAndroid.ximalaya.data.XimalayaApi;
import com.uniqueAndroid.ximalaya.interfaces.ISearchCallback;
import com.uniqueAndroid.ximalaya.interfaces.ISearchPresenter;
import com.uniqueAndroid.ximalaya.utils.Constants;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private final XimalayaApi mXimalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private List<Album> mSearchResults = new ArrayList<>();

    private SearchPresenter() {
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }


    private List<ISearchCallback> mCallbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }

    @Override
    public void doSearch(String keyword) {
        mCurrentPage = DEFAULT_PAGE;
        mSearchResults.clear();
        this.mCurrentKeyword = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearchResults.addAll(albums);
                if (albums != null) {
                    LogUtil.d(TAG, "albums size --->" + albums.size());
                    if (mIsLoaderMore) {
                        for (ISearchCallback callback : mCallbacks) {
                            callback.onLoadMoreResult(mSearchResults,albums.size() !=0);
                        }
                        mIsLoaderMore = false;
                    } else {
                        for (ISearchCallback iSearchCallback : mCallbacks) {
                            iSearchCallback.onSearchResultLoad(mSearchResults);
                        }
                    }
                } else {
                    LogUtil.d(TAG, "albums is null ..");
                }

            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error code --->" + i);
                LogUtil.d(TAG, "errorMsg --->" + s);
                for (ISearchCallback callback : mCallbacks) {
                    if (mIsLoaderMore) {
                        callback.onLoadMoreResult(mSearchResults, false);
                        mIsLoaderMore = false;
                        mCurrentPage--;
                    } else {
                        callback.onError(i, s);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    private boolean mIsLoaderMore = false;

    @Override
    public void loadMore() {
        //判断有没有必要进行加载更多
        if (mSearchResults.size() < Constants.COUNT_DEFAULT) {
            for (ISearchCallback callback : mCallbacks) {
                callback.onLoadMoreResult(mSearchResults,false);
            }
        } else {
            mIsLoaderMore = true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }
    }

    @Override
    public void getHotWord() {
        //todo: 缓存
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG, "hotWords size ---> " + hotWords.size());
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onHotWordLoad(hotWords);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "getHotWord errorCode --->" + i);
                LogUtil.d(TAG, "getHotWord errorMsg ---> " + s);

            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onRecommendWordLoaded(keyWordList);
                    }
                    LogUtil.d(TAG, "keyWordList size ---> " + keyWordList.size());
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "getRecommendWord errorCode --->" + i);
                LogUtil.d(TAG, "getRecommendWord errorMsg ---> " + s);
            }
        });
    }
}
