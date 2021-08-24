package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.newsHolder>{
    private ArrayList<NewsData> mData = new ArrayList<>();
    final private ListItemClickListener mOnClickListener;
    public NewsAdapter(ArrayList<NewsData> data, ListItemClickListener listener) {
        mData = data;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public newsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new newsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newsHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(mData.get(position).image).into(holder.mImageView);
        holder.mDataDisplay.setText(mData.get(position).title);
        holder.mSource.setText(mData.get(position).source);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class newsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mDataDisplay;
        ImageView mImageView;
        TextView mSource;
        public newsHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img);
            mDataDisplay = (TextView) itemView.findViewById(R.id.tvDisplay);
            mSource = itemView.findViewById(R.id.sourceNews);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition, mData.get(clickedPosition).url);
        }
    }
    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex, String url);
    }
}
