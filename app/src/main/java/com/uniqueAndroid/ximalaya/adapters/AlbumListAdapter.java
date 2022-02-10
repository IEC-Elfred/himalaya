package com.uniqueAndroid.ximalaya.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uniqueAndroid.ximalaya.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {
    private static final String TAG = "RecommendListAdapter";
    private List<Album> mData = new ArrayList<>();
    private onRecommendItemClickListener mItemClickListner;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //载view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //设置数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListner != null) {
                    mItemClickListner.onItemClick(position,mData.get(position));
                }
                Log.d(TAG, "itemClick --->" + v.getTag());
            }
        });
        holder.setData(mData.get(position));
    }


    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public int getDataSize() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到控件，设置数据
            // 专辑封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDescTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDescTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(String.format("%d", album.getPlayCount()));
            albumContentCountTv.setText(String.format("%d", album.getIncludeTrackCount()));
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.get().load(album.getCoverUrlLarge()).into(albumCoverIv);
            } else {
                albumCoverIv.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }

    public void setOnAlbumClickListener(onRecommendItemClickListener listener) {
        this.mItemClickListner = listener;
    }

    public interface onRecommendItemClickListener {
        void onItemClick(int position, Album album);
    }
}
