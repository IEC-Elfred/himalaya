package com.uniqueAndroid.ximalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniqueAndroid.ximalaya.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder> {
    private List<QueryResult> mData = new ArrayList<>();
    private ItemClickListener mItemClickListner = null;

    @NonNull
    @Override
    public SearchRecommendAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_recommend, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendAdapter.InnerHolder holder, int position) {
        TextView text = holder.itemView.findViewById(R.id.search_recommend_item);
        QueryResult queryResult = mData.get(position);
        text.setText(queryResult.getKeyword());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListner != null) {
                    mItemClickListner.onItemClick(queryResult.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<QueryResult> keyWordList) {
        mData.clear();
        mData.addAll(keyWordList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListner = listener;
    }
    public interface ItemClickListener{
        void onItemClick(String keyword);
    }
}
