package com.uniqueAndroid.ximalaya;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.uniqueAndroid.ximalaya.adapters.DetailListAdapter;
import com.uniqueAndroid.ximalaya.base.BaseActivity;
import com.uniqueAndroid.ximalaya.interfaces.IAlbumDetailViewCallback;
import com.uniqueAndroid.ximalaya.presenters.AlbumDetailPresenter;
import com.uniqueAndroid.ximalaya.utils.ImageBlur;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.uniqueAndroid.ximalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener {

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView() {
        mDetailListAdapter = new DetailListAdapter();
        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
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


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View createSuccessView(ViewGroup container) {
        View detailList = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        albumDetailList = detailList.findViewById(R.id.album_detail_list);
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
        return detailList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新ui数据
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        long id = album.getId();
        LogUtil.d(TAG,"Album --->" + id);
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
    public void onNetworkError(int errorCode, String errorMsg) {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onRetryClick() {
        //网络不佳的时候
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }
}
