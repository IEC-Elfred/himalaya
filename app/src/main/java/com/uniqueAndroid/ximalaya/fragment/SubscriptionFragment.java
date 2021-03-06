package com.uniqueAndroid.ximalaya.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.uniqueAndroid.ximalaya.DetailActivity;
import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.adapters.AlbumListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.base.BaseFragment;
import com.uniqueAndroid.ximalaya.interfaces.ISubscriptionCallback;
import com.uniqueAndroid.ximalaya.presenters.AlbumDetailPresenter;
import com.uniqueAndroid.ximalaya.presenters.SubscriptionPresenter;
import com.uniqueAndroid.ximalaya.utils.Constants;
import com.uniqueAndroid.ximalaya.views.ConfirmDialog;
import com.uniqueAndroid.ximalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import org.w3c.dom.Text;

import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.onAlbumClickListener, AlbumListAdapter.onAlbumItemLongClickListener, ConfirmDialog.OnDialogActionClickListener {
    private SubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlumlistAdapter;
    private Album mCurrentClickAlbum = null;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_subscription, container, false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tipsView = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.no_sub_content_tips_text);
                    return emptyView;
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }

        return rootView;
    }

    private View createSuccessView() {
        View itemView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_subscription, null, false);
        TwinklingRefreshLayout refreshLayout = itemView.findViewById(R.id.over_scorll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        mSubListView = (RecyclerView) itemView.findViewById(R.id.sub_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
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
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        Toast.makeText(BaseApplication.getAppContext(), isSuccess ? R.string.cancel_sub_success_tips_text : R.string.cancel_sub_fail_tips_text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {
        if (albums.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
        //??????UI
        if (mAlumlistAdapter != null) {
            mAlumlistAdapter.setData(albums);
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getContext(), "????????????????????????" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //?????????????????????
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        mAlumlistAdapter.setOnAlbumClickListener(null);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item?????????????????????????????????
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        this.mCurrentClickAlbum = album;
        //?????????item????????????
        ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setOnDialogActionClickListener(this);
        confirmDialog.show();
    }

    @Override
    public void onCancelSubClick() {
        //????????????
        if (mCurrentClickAlbum != null && mSubscriptionPresenter != null) {
            mSubscriptionPresenter.deleteSubscription(mCurrentClickAlbum);
        }
    }

    @Override
    public void onGiveUpClick() {
        //??????????????????
    }
}
