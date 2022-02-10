package com.uniqueAndroid.ximalaya.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.uniqueAndroid.ximalaya.DetailActivity;
import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.adapters.AlbumListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseFragment;
import com.uniqueAndroid.ximalaya.interfaces.IRecommendViewCallback;
import com.uniqueAndroid.ximalaya.presenters.AlbumDetailPresenter;
import com.uniqueAndroid.ximalaya.presenters.RecommendPresenter;
import com.uniqueAndroid.ximalaya.utils.Constants;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.uniqueAndroid.ximalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, AlbumListAdapter.onAlbumClickListener {
    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendRv;
    private AlbumListAdapter recommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader uiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        uiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };

        //获取到逻辑层到对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口到注册
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();
        if (uiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) uiLoader.getParent()).removeView(uiLoader);
        }

        uiLoader.setOnRetryClickListener(this);
        return uiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        recommendRv = rootView.findViewById(R.id.recommend_list);
        TwinklingRefreshLayout twinklingRefreshLayout = rootView.findViewById(R.id.over_scorll_view);
        twinklingRefreshLayout.setPureScrollModeOn();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommendRv.setLayoutManager(linearLayoutManager);
        recommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        recommendListAdapter = new AlbumListAdapter();
        recommendRv.setAdapter(recommendListAdapter);
        recommendListAdapter.setOnAlbumClickListener(this);
        return rootView;
    }

    private void getRecommendData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    albumList.addAll(gussLikeAlbumList.getAlbumList());
                    updateRecommendUI(albumList);
                }
            }


            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error----->" + i);
                LogUtil.d(TAG, "errorMsg----->" + s);
            }
        });
    }

    private void updateRecommendUI(List<Album> albumList) {
        recommendListAdapter.setData(albumList);
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        LogUtil.d(TAG,"onRecommendListLoaded");
        //当我们获取到推荐内容时，这个方法就会被调用
        //数据回来就更新UI
        recommendListAdapter.setData(result);
        uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        LogUtil.d(TAG,"onNetworkError");
        uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        LogUtil.d(TAG,"onEmpty");
        uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        LogUtil.d(TAG,"onLoading");

        uiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }


    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击，跳转到详情界面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
