package com.uniqueAndroid.ximalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniqueAndroid.ximalaya.R;
import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.uniqueAndroid.ximalaya.views.PopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private static final String TAG = "PlayListAdapter";
    private List<Track> mData = new ArrayList<>();
    private int mPlayingIndex = 0;
    private ImageView mPlayStatusView;
    private TextView mTrackTitle;
    private PopWindow.PlayListItemClickListener mPlayListItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        LogUtil.d(TAG,"position ---> " + position);
        LogUtil.d(TAG,"now mPlayingIndex ----> " + position);
        Track track = mData.get(position);
        mTrackTitle = holder.itemView.findViewById(R.id.track_title);
        mTrackTitle.setText(track.getTrackTitle());
        mTrackTitle.setTextColor(BaseApplication.getAppContext().getResources().getColor(position == mPlayingIndex ? R.color.main_color : R.color.play_list_text_color ));
        mPlayStatusView = holder.itemView.findViewById(R.id.track_status);
        mPlayStatusView.setVisibility(position == mPlayingIndex ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayListItemClickListener != null) {
                    mPlayListItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        LogUtil.d(TAG,"now Position ----> " + position);
        mPlayingIndex = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(PopWindow.PlayListItemClickListener listener) {
        mPlayListItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
