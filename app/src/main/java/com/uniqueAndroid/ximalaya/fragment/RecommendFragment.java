package com.uniqueAndroid.ximalaya.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.adapters.RecommendListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseFragment;
import com.uniqueAndroid.ximalaya.utils.Constants;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {
    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView recommendRv;
    private RecommendListAdapter recommendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        recommendRv = rootView.findViewById(R.id.recommend_list);
        Context context;
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
        recommendListAdapter = new RecommendListAdapter();
        recommendRv.setAdapter(recommendListAdapter);
        getRecommendData();
        return rootView;
    }

    private void getRecommendData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
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
}
