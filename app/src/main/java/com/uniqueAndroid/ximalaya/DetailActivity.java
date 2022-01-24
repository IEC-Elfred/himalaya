package com.uniqueAndroid.ximalaya;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback {

    private static final String TAG = "DetailActivity";
    private ImageView bgCover;
    private ImageView smallCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private AlbumDetailPresenter albumDetailPresenter;
    private int mCurrentPage = 1;
    private RecyclerView albumDetailList;
    private DetailListAdapter mDetailListAdapter;

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

    private void initView() {
        bgCover = this.findViewById(R.id.cover_bg);
        smallCover = this.findViewById(R.id.iv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);
        albumDetailList = this.findViewById(R.id.album_detail_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        albumDetailList.setLayoutManager(layoutManager);
        mDetailListAdapter = new DetailListAdapter();
        albumDetailList.setAdapter(mDetailListAdapter);
        albumDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        //更新ui数据
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        //获取专辑的详情内容
        albumDetailPresenter.getAlbumDetail((int)album.getId(),mCurrentPage);
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
                                ImageBlur.makeBlur(bgCover,bgCover.getContext());
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    LogUtil.d(TAG,"onError---->" +e);
                }
            });

        }
        if (smallCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(smallCover);
        }
    }
}
