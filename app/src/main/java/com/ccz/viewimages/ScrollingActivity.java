package com.ccz.viewimages;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccz.viewimages.displayingbitmaps.util.ImageCache;
import com.ccz.viewimages.displayingbitmaps.util.ImageFetcher;
import com.ccz.viewimages.media.FolderRetriever;
import com.ccz.viewimages.media.MediaItem;
import com.ccz.viewimages.media.PhotoRetriever;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {

    protected ImageFetcher mImageFetcher;
    protected int                          mImageThumbSize;
    protected int mImageThumbSpacing;

    protected static final String IMAGE_CACHE_DIR      = "thumbs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // init data
        PhotoRetriever.getInstance(this).prepare();
        FolderRetriever.getInstance(this).prepare();

        // init gridview
        RecyclerView gridView = (RecyclerView) findViewById(R.id.gridView);
        GalleryAdapter adapter = new GalleryAdapter(this, FolderRetriever.getInstance(this).getItems());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        gridView.setLayoutManager(layoutManager);
        gridView.setAdapter(adapter);

        // Initialize image size
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        // Set memory cache to 10% of app memory
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.1f);

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getFragmentManager(), cacheParams);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_grid) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

        private Context mContext;
        private ArrayList<FolderRetriever.FolderItem> mFolders;
        private ArrayList<MediaItem>                  mAdapterItems;
        private ArrayList<MediaItem>                  mCoverItems;
//        private BitmapDrawable[]                      mCoverItemsCache;
//        private ArrayList<BitmapDrawable>             mCoverCache;
//        private ImageSwitcher                         mImageSwitcher;
//        private Handler                               mUpdateCoverHandler;
//        private Handler                               mGetCoverHandler;
        private int                                   mScreenWidth;
        private boolean IsPause = false;

        public GalleryAdapter(Context context, ArrayList<FolderRetriever.FolderItem> folders) {
            super();
            mContext = context;
            mFolders = folders;
            mAdapterItems = PhotoRetriever.getInstance(mContext).getItems();
//            Log.e("FD", "folder:" + mFolders.size());
//            Log.e("FD", "folder:" + mAdapterItems.size());
        }

        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.gallery_list_item, parent, false);
            GalleryAdapter.ViewHolder holder = new GalleryAdapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
            try {
                holder.mTitle.setText(mFolders.get(position).getName());
                holder.mPhotoCount.setText(String.valueOf(mFolders.get(position).getPhotoCount()));
                holder.mVideoCount.setText(String.valueOf(mFolders.get(position).getVideoCount()));
//                viewHolder.mHideFolder.setVisibility(View.GONE);

                String folder_id = mFolders.get(position).getId();

                int idx = 0;
                if (!("".equals(folder_id)) && (null != folder_id)) {
                    for (int i = 0; i < mAdapterItems.size(); i++) {
                        if (mAdapterItems.get(i).getFolderId().equals(folder_id)) {
                            idx = i;
                            break;
                        }
                    }
                }

//                Log.e("FD", "path:" + mAdapterItems.get(idx).getPath());
                mImageFetcher.loadImage(mAdapterItems.get(idx), holder.mImage);
//                holder.mImage.setImageResource(R.drawable.example);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mFolders.size();
        }

    public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTitle;
            public TextView      mPhotoCount;
            public TextView      mVideoCount;
            public ImageView mImage;

            public ViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                mPhotoCount = (TextView) itemView.findViewById(R.id.txtPhotoCount);
                mVideoCount = (TextView) itemView.findViewById(R.id.txtVideoCount);
                mImage = (ImageView) itemView.findViewById(R.id.coverImg);
            }
        }
    }

}
