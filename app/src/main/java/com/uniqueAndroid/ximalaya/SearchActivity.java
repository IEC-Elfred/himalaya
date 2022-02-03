package com.uniqueAndroid.ximalaya;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uniqueAndroid.ximalaya.adapters.AlbumListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseActivity;
import com.uniqueAndroid.ximalaya.interfaces.ISearchCallback;
import com.uniqueAndroid.ximalaya.presenters.SearchPresenter;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.uniqueAndroid.ximalaya.views.FlowTextLayout;
import com.uniqueAndroid.ximalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchActivity extends BaseActivity implements ISearchCallback {
    private static final String TAG = "SearchActivity";
    private EditText mInputBox;
    private View mBackBtn;
    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private FlowTextLayout mFlowTextLayout;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initEvent() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mInputBox.getText().toString().trim();
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyword);
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });

        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.d(TAG, "");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG, "content" + s);
                LogUtil.d(TAG, "start" + start);
                LogUtil.d(TAG, "before" + before);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
               mInputBox.setText(text);
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(text);
                }
            }
        });

        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {

            }
        });
    }

    private void initView() {
        mBackBtn = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
//        mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mResultContainer.addView(mUILoader);

        }
    }

    /**
     * 创建数据请求成功的view
     *
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        mResultListView = resultView.findViewById(R.id.result_list_view);
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        return resultView;
    }

    @Override
    public void onSearchResultLoad(List<Album> result) {
        mResultListView.setVisibility(View.VISIBLE);
        mFlowTextLayout.setVisibility(View.GONE);

        InputMethodManager systemService = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        systemService.hideSoftInputFromWindow(mInputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (result != null) {
            if (result.size() == 0) {
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            } else {
                mAlbumListAdapter.setData(result);
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoad(List<HotWord> hotWordList) {
        mResultListView.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        LogUtil.d(TAG, "hotWordList size --->" + hotWordList.size());
        List<String> hotwords = new ArrayList<>();
        hotwords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchword = hotWord.getSearchword();
            hotwords.add(searchword);
        }
        Collections.sort(hotwords);
        mFlowTextLayout.setTextContents(hotwords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }
}
