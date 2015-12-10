package com.ccz.viewimages.custom;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by CC on 2015/12/7.
 */
public class CustomRecyclerViewAdapter <VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements View.OnClickListener {

    public interface OnItemClickListener {
        void onItemClick(View view, Object data);
    }

    protected ArrayList           mAdapterItems;
    protected OnItemClickListener mItemClickListener;
    protected int mItemHeight;
    protected int mMargin = 0;
    protected RelativeLayout.LayoutParams mImageViewLayoutParams;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(mAdapterItems.get(position));
        if (null != mImageViewLayoutParams) {
            holder.itemView.setLayoutParams(mImageViewLayoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapterItems.size();
    }

    @Override
    public void onClick(View view) {
        if (null != this.mItemClickListener) {
            Log.e("REC", "view:" + view.getTag() + ", " + mAdapterItems.size());
            this.mItemClickListener.onItemClick(view, view.getTag());
        }
    }

    public void setMargin(int margin) {
        mMargin = margin;
    }

    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(mItemHeight, mItemHeight);
        mImageViewLayoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
        notifyDataSetChanged();
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public void setItemAttributes(int height, int margin) {
        setMargin(margin);
        setItemHeight(height);
    }

}
