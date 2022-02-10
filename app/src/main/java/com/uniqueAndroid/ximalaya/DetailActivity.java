package com.uniqueAndroid.ximalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.uniqueAndroid.ximalaya.adapters.DetailListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseActivity;
import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.interfaces.IAlbumDetailViewCallback;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerCallback;
import com.uniqueAndroid.ximalaya.interfaces.ISubscriptionCallback;
import com.uniqueAndroid.ximalaya.presenters.AlbumDetailPresenter;
import com.uniqueAndroid.ximalaya.presenters.PlayerPresenter;
import com.uniqueAndroid.ximalaya.presenters.SubscriptionPresenter;
import com.uniqueAndroid.ximalaya.utils.Constants;
import com.uniqueAndroid.ximalaya.utils.ImageBlur;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.uniqueAndroid.ximalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {
    private static final String TAG = "DetailActivity";
    private ImageView bgCover;
    private ImageView smallCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private AlbumDetailPresenter albumDetailPresenter;
    private int mCurrentPage = 1;
    private RecyclerView albumDetailList;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle = "";
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;
    private View subscriptionContainer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        initPresenter();
        updateSubState();
        updatePlayState(mPlayerPresenter.isPlaying());
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }


    private void initListener() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayerPresenter != null) {
                        //判断播放器是否有播放列表
                        if (mPlayerPresenter.hasPlayList()) {
                            handlePlayControl();
                        } else {
                            handleNoPlayList();
                        }
                    }
                }
            });
        }


        subscriptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    if (isSub) {
                        LogUtil.d(TAG, "elfred: sub");
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    } else {
                        LogUtil.d(TAG, "elfred: unsub");
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }


    /**
     * 当播放器里面没有播放内容，我们要进行处理一下
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView() {
        mDetailListAdapter = new DetailListAdapter();
        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    LogUtil.d(TAG, "success");
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        bgCover = this.findViewById(R.id.cover_bg);
        smallCover = this.findViewById(R.id.iv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);
        mPlayControlBtn = this.findViewById(R.id.play_icon);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);
        mSubBtn = this.findViewById(R.id.detail_sub_text);
        subscriptionContainer = this.findViewById(R.id.subscription_container);
    }

    private boolean mIsLoaderMore = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View createSuccessView(ViewGroup container) {
        View detailList = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        albumDetailList = detailList.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailList.findViewById(R.id.refresh_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        albumDetailList.setLayoutManager(layoutManager);
        albumDetailList.setAdapter(mDetailListAdapter);
        albumDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mDetailListAdapter.setItemClickListener(this);
        mRefreshLayout.setHeaderView(new BezierLayout(this));
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (albumDetailPresenter != null) {
                    albumDetailPresenter.loadMore();
                    mIsLoaderMore = true;
                }
            }

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getsHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功...", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 2000);
            }
        });
        return detailList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;
        }
        this.mCurrentTracks = tracks;
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null) {
            LogUtil.d(TAG, "UILoader.UIStatus.SUCCESS");
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新ui数据
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        long id = album.getId();
        this.mCurrentAlbum = album;
        LogUtil.d(TAG, "Album --->" + id);
        mCurrentId = id;
        //获取专辑的详情内容
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail((int) album.getId(), mCurrentPage);
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (albumTitle != null) {
            albumTitle.setText(album.getAlbumTitle());
        }

        if (albumAuthor != null) {
            albumAuthor.setText(album.getAnnouncer().getNickname());
        }
        //做毛玻璃效果
        if (bgCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(bgCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = bgCover.getDrawable();
                    if (drawable != null) {
                        //到这里才是说明有图片的
                        bgCover.post(new Runnable() {
                            @Override
                            public void run() {
                                ImageBlur.makeBlur(bgCover, bgCover.getContext());
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    LogUtil.d(TAG, "onError---->" + e);
                }
            });

        }
        if (smallCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(smallCover);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (albumDetailPresenter != null) {
            albumDetailPresenter.unRegisterViewCallback(this);
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size > 0) {
            Toast.makeText(this, "成功加载" + size + "条节目", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多节目", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        //网络不佳的时候
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        // 设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, position);
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    private void updatePlayState(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(playing ? R.drawable.ic_baseline_pause_24 : R.drawable.ic_baseline_play_circle_outline_24);
            if (!playing) {
                mPlayControlTips.setText(R.string.click_play_text);
            } else {
                mPlayControlTips.setText(mCurrentTrackTitle);
            }
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayState(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTips != null) {
                mPlayControlTips.setText(mCurrentTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        LogUtil.d(TAG, "elfred:add" + " ..isSuccess-->" + isSuccess);
        if (isSuccess) {
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        LogUtil.d(TAG, "elfred:delete" + " ..isSuccess-->" + isSuccess);
        if (isSuccess) {
            mSubBtn.setText(R.string.sub_tips_text);
        }
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {

    }

    @Override
    public void onSubFull() {
        Toast.makeText(this, "订阅数量不得超过"+ Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }
}
