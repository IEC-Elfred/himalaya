package com.uniqueAndroid.ximalaya.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.uniqueAndroid.ximalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {
    private List<Track> mDetailData = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat mUpdateDateFormat = new SimpleDateFormat("YYYY-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListner = null;
    private ItemLongClickListener mItemLongClickListner = null;

    @NonNull
    @Override
    public TrackListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_layout, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.InnerHolder holder, int position) {
        View itemView = holder.itemView;
        TextView orderTv = itemView.findViewById(R.id.order_text);
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);
        TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
        TextView updateDateTv = itemView.findViewById(R.id.detail_item_update_time);

        Track track = mDetailData.get(position);
        orderTv.setText(position + 1 + "");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount() + "");
        String duration = mDurationFormat.format(track.getDuration() * 1000);
        durationTv.setText(duration);
        String updateTimeTv = mUpdateDateFormat.format(track.getUpdatedAt());
        updateDateTv.setText(updateTimeTv);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListner != null) {
                    //参数需要有数据和位置
                    mItemClickListner.onItemClick(mDetailData, position);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListner != null) {
                    mItemLongClickListner.onItemLongClick(track);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        mDetailData.clear();
        mDetailData.addAll(tracks);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListner = listener;
    }

    public interface ItemClickListener {
        void onItemClick(List<Track> detailData, int position);
    }

    public void setItemLongClickListener(ItemLongClickListener listener) {
        this.mItemLongClickListner = listener;
    }

    public interface ItemLongClickListener {
        void onItemLongClick(Track track);
    }

}
