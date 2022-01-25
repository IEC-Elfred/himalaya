package com.uniqueAndroid.ximalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayerTrackPagerAdapter extends PagerAdapter {


    private static final String TAG = "PlayerTrackPagerAdapter";
    private List<Track> mData = new ArrayList<>();

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LogUtil.d(TAG,"1111" );
        View mItemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager, container, false);
        container.addView(mItemView);
        ImageView item = mItemView.findViewById(R.id.track_pager_item);
        Track track = mData.get(position);
        String coverUrlLarge = track.getCoverUrlLarge();
        LogUtil.d(TAG,"coverUrlLarge-->" + coverUrlLarge);
        Picasso.get().load(coverUrlLarge).into(item);
        return mItemView;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }
}
