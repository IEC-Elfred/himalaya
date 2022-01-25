package com.uniqueAndroid.ximalaya;

import android.os.Bundle;
import android.os.Trace;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.uniqueAndroid.ximalaya.adapters.PlayerTrackPagerAdapter;
import com.uniqueAndroid.ximalaya.base.BaseActivity;
import com.uniqueAndroid.ximalaya.interfaces.IPlayerCallback;
import com.uniqueAndroid.ximalaya.presenters.PlayerPresenter;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.List;

public class PlayerActivity extends BaseActivity implements IPlayerCallback {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat minFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mSeekBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitleTv;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private ViewPager mTrackPageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        mPlayerPresenter.getPlayList();
        initEvent();
        startPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }

    /**
     * 给控件设置相关的事件
     */
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                } else {
                    mPlayerPresenter.play();
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开进度条的时候
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(TAG,"click pre btn");
                if (mPlayerPresenter != null) {
                    LogUtil.d(TAG,"click pre btn");
                    mPlayerPresenter.playPre();
                }
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    LogUtil.d(TAG,"click next btn");
                    mPlayerPresenter.playNext();
                }
            }
        });
    }

    //找到各个控件
    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mSeekBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        mTrackPageView = this.findViewById(R.id.track_pager_view);
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        mTrackPageView.setAdapter(mTrackPagerAdapter);
    }

    @Override
    public void onPlayStart() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.ic_baseline_pause_24);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        }
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
        LogUtil.d(TAG,"list --->" + list);
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(long currentDuration, long total) {
        String totalDuration;
        String currentPosition;
        mSeekBar.setMax((int)total);
        if (total > 1000 * 60 * 60) {
            totalDuration = hourFormat.format(total);
            currentPosition = hourFormat.format(currentDuration);
        } else {
            totalDuration = minFormat.format(total);
            currentPosition = minFormat.format(currentDuration);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        if (!mIsUserTouchProgressBar) {
            mSeekBar.setProgress((int) currentDuration);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackTitleUpdate(Track track) {
        if (mTrackTitleTv != null) {
            mTrackTitleTv.setText(track.getTrackTitle());
        }
        //当节目改变当时候，我们就获取当前播放中当位置
    }
}
