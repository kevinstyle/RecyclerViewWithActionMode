package com.ccz.viewimages.custom;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccz.viewimages.GalleryActivity;
import com.ccz.viewimages.R;
import com.ccz.viewimages.displayingbitmaps.util.ImageFetcher;
import com.ccz.viewimages.dragselect.DragSelectRecyclerView;
import com.ccz.viewimages.dragselect.DragSelectRecyclerViewAdapter;
import com.ccz.viewimages.media.MediaItem;

import java.util.ArrayList;

/**
 * Created by user on 2015/12/7.
 */

public class DragSelectGalleryAdapter extends DragSelectRecyclerViewAdapter<DragSelectGalleryAdapter.ViewHolder> implements Filterable, ActionMode.Callback {

    private Context                     mContext;
    private ArrayList<MediaItem>        mAdapterItems;
    private ArrayList<MediaItem>        mAllList;
    private Filter                      mFilter;
    private ImageFetcher                mImageFetcher;
    private ClickListener               mCallback;
    private RelativeLayout.LayoutParams mImageViewLayoutParams;
    private DragSelectRecyclerView      mContainer;

    private final int VIEW_TYPE_PHOTO = 0;
    private final int VIEW_TYPE_VIDEO = 1;

    private final String MIME_IMAGE = "image";
    private final String MIME_VIDEO = "video";

    public DragSelectGalleryAdapter(Context context, ArrayList<MediaItem> list, ClickListener callback, DragSelectRecyclerView container) {
        super(context);
        mContext = context;
        mAdapterItems = list;
        mAllList = list;
        mCallback = callback;
        mContainer = container;
        setDragSelectHelper(new DragSelectAdapterHelper(this));
    }

    public void setImageFetcher(ImageFetcher imageFetcher) {
        mImageFetcher = imageFetcher;
    }

    public void setItemHeightAndMargin(int height, int margin) {
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(height, height);
        mImageViewLayoutParams.setMargins(margin, margin, margin, margin);
        notifyDataSetChanged();
    }

    @Override
    public DragSelectGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_VIDEO) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.gallery2_video_item, parent, false);
            DragSelectGalleryAdapter.ViewHolder holder = new DragSelectGalleryAdapter.ViewHolder(v, mCallback);
            return holder;
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.gallery2_photo_item, parent, false);
            DragSelectGalleryAdapter.ViewHolder holder = new DragSelectGalleryAdapter.ViewHolder(v, mCallback);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(DragSelectGalleryAdapter.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        if (null != mImageViewLayoutParams) {
            viewHolder.itemView.setLayoutParams(mImageViewLayoutParams);
        }

        try {

            MediaItem item = mAdapterItems.get(position);

            if (getItemViewType(position) == VIEW_TYPE_VIDEO) {
                viewHolder.mTitle.setText(item.getName());
            }

            if (null != mImageFetcher) {
                mImageFetcher.loadImageWithDoubleThumb(item, viewHolder.mImage, true);
            }

            if (getDragSelectHelper().isInActionMode()) {
                viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mCheckBox.setVisibility(View.INVISIBLE);
            }

            if (isIndexSelected(position)) {
                viewHolder.mCheckBox.setChecked(true);
                viewHolder.mImage.animate().scaleX((float) 0.7).scaleY((float) 0.7).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            } else {
                viewHolder.mCheckBox.setChecked(false);
                viewHolder.mImage.animate().scaleX(1).scaleY(1).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isVideo(int position) {
        if (null == mAdapterItems.get(position)) return false;
        return mAdapterItems.get(position).getMimeType().contains(MIME_VIDEO);
    }

    @Override
    public int getItemViewType(int position) {
        return isVideo(position) ? VIEW_TYPE_VIDEO : VIEW_TYPE_PHOTO;
    }

    @Override
    public int getItemCount() {
        return mAdapterItems.size();
    }

    public MediaItem getItem(int index) {
        return mAdapterItems.get(index);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new FolderFilter();
        }
        return mFilter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.clear();
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.main_action_menu, menu);
        Log.e("actionmode", "onCreateActionMode = " + mode.toString());
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Log.e("actionmode", "onPrepareActionMode = " + mode.toString());
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        clearSelected();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        Log.e("actionmode", "onDestroyActionMode = " + mode.toString());
        getDragSelectHelper().onDestroyActionMode();
        mContainer.setDragSelectActive(false);
        clearSelected();
    }

    private class FolderFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = mAllList;
                results.count = mAllList.size();
            } else {

                String str = constraint.toString();
                String fileType = str.substring(0, str.indexOf("/"));
                String folderId = str.substring(str.indexOf("/") + 1);

                ArrayList<MediaItem> nList = new ArrayList<MediaItem>();
                for (MediaItem p : mAllList) {
                    if (null == folderId || folderId.length() == 0 || folderId.toLowerCase().equals(p.getFolderId().toUpperCase())) {
                        if (fileType.equals(GalleryActivity.FILTER_PHOTO)) {
                            if (p.getMimeType().contains(MIME_IMAGE)) {
                                nList.add(p);
                            }
                        } else if (fileType.equals(GalleryActivity.FILTER_VIDEO)) {
                            if (p.getMimeType().contains(MIME_VIDEO)) {
                                nList.add(p);
                            }
                        } else {
                            nList.add(p);
                        }
                    }
                }

                results.values = nList;
                results.count = nList.size();
                Log.e("SPINNER", "showlist count:" + results.count);
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAdapterItems = (ArrayList<MediaItem>) results.values;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
        public        TextView      mTitle;
        public        ImageView     mImage;
        public        CheckBox      mCheckBox;
        private final ClickListener mCallback;

        public ViewHolder(View itemView, ClickListener callback) {
            super(itemView);

            mCallback = callback;
            mTitle = (TextView) itemView.findViewById(R.id.video_title);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);

            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
            this.mCheckBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                mCallback.onClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mCallback != null) {
                mCallback.onLongClick(getAdapterPosition());
            }
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mCallback != null) {
                mCallback.onCheckChange(getAdapterPosition(), buttonView, isChecked);
            }
        }
    }
}
