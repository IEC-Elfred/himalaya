package com.uniqueAndroid.ximalaya.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.uniqueAndroid.ximalaya.DetailActivity;
import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.adapters.AlbumListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseFragment;
import com.uniqueAndroid.ximalaya.interfaces.ISubscriptionCallback;
import com.uniqueAndroid.ximalaya.presenters.AlbumDetailPresenter;
import com.uniqueAndroid.ximalaya.presenters.SubscriptionPresenter;
import com.uniqueAndroid.ximalaya.utils.Constants;
import com.uniqueAndroid.ximalaya.views.ConfirmDialog;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.onAlbumClickListener, AlbumListAdapter.onAlbumItemLongClickListener, ConfirmDialog.OnDialogActionClickListener {
    private SubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlumlistAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subscription,container,false);
        TwinklingRefreshLayout refreshLayout = rootView.findViewById(R.id.over_scorll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        mSubListView = (RecyclerView)rootView.findViewById(R.id.sub_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mAlumlistAdapter = new AlbumListAdapter();
        mAlumlistAdapter.setOnAlbumClickListener(this);
        mAlumlistAdapter.setOnAlbumItemLongClickListener(this);
        mSubListView.setAdapter(mAlumlistAdapter);
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
        return rootView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {
        //更新UI
        if (mAlumlistAdapter != null) {
            mAlumlistAdapter.setData(albums);
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getContext(), "订阅数量不得超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        mAlumlistAdapter.setOnAlbumClickListener(null);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击，跳转到详情界面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        //订阅的item被长按了
        ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setOnDialogActionClickListener(this);
        confirmDialog.show();
    }

    @Override
    public void onCancelSubClick() {
        //取消订阅
    }

    @Override
    public void onGiveUpClick() {
        //放弃取消订阅
    }
}
