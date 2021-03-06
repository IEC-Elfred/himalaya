package com.uniqueAndroid.ximalaya.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.uniqueAndroid.ximalaya.PlayerActivity;
import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.adapters.TrackListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.base.BaseFragment;
import com.uniqueAndroid.ximalaya.interfaces.IHistoryCallback;
import com.uniqueAndroid.ximalaya.presenters.HistoryPresenter;
import com.uniqueAndroid.ximalaya.presenters.PlayerPresenter;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.uniqueAndroid.ximalaya.views.ConfirmCheckBoxDialog;
import com.uniqueAndroid.ximalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback, TrackListAdapter.ItemClickListener, TrackListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogActionClickListener {
    private static final String TAG = "HistoryFragment";
    private UILoader mUiLoader;
    private TrackListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentClickHistoryItem = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tips = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tips.setText("??????????????????");
                    return emptyView;
                }
            };
        } else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresenter.listHistories();
        rootView.addView(mUiLoader);
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, container, false);
        TwinklingRefreshLayout twinklingRefreshLayout = successView.findViewById(R.id.over_scorll_view);
        twinklingRefreshLayout.setEnableLoadmore(false);
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableOverScroll(true);
        RecyclerView historyList = successView.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setItemClickListener(this);
        mTrackListAdapter.setItemLongClickListener(this);
        historyList.setAdapter(mTrackListAdapter);
        return successView;
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {

    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        LogUtil.d(TAG,"tracks size ---->" + tracks.size());
        if (tracks == null || tracks.size() == 0) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        } else {
            mTrackListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        // ????????????????????????
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, position);
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentClickHistoryItem = track;
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getContext());
        dialog.setOnDialogActionClickListener(this);
        dialog.show();
    }

    @Override
    public void onCancelClick() {

    }

    @Override
    public void onConfirmClick(boolean isCheck) {
        if (mHistoryPresenter != null && mCurrentClickHistoryItem != null) {
            if (!isCheck) {
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            } else {
                mHistoryPresenter.cleanHistories();
            }
        }
    }
}
