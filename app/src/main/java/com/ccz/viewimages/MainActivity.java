package com.ccz.viewimages;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ccz.viewimages.custom.CustomRecyclerViewAdapter;
import com.ccz.viewimages.displayingbitmaps.util.ImageCache;
import com.ccz.viewimages.displayingbitmaps.util.ImageFetcher;
import com.ccz.viewimages.media.FolderRetriever;
import com.ccz.viewimages.media.MediaItem;
import com.ccz.viewimages.media.MediaUtil;
import com.ccz.viewimages.media.PhotoRetriever;
import com.ccz.viewimages.media.VideoRetriever;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected ImageFetcher mImageFetcher;
    protected int          mImageThumbSize;
    protected int          mImageThumbSpacing;

    public static final int COLUMNS = 2;
    protected static final String IMAGE_CACHE_DIR = "thumbs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        FolderRetriever.getInstance(this).prepare();
        PhotoRetriever.getInstance(this).prepare();
        VideoRetriever.getInstance(this).prepare();


        // init gridview
        RecyclerView gridView = (RecyclerView) findViewById(R.id.gridView);
        final FolderAdapter adapter = new FolderAdapter(this, FolderRetriever.getInstance(this).getItems());
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMNS);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0) ? COLUMNS : 1;
            }
        });
        gridView.setLayoutManager(layoutManager);
        gridView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CustomRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object data) {
                //            Bundle b = new Bundle();
                //            b.putString(GalleryActivity.FOLDER_ID, ((FolderRetriever.FolderItem) v.getTag()).getId());
                //            b.putString(GalleryActivity.FOLDER_NAME, ((FolderRetriever.FolderItem) v.getTag()).getName());
                //            f.setArguments(b);

                if (null != data) {
                    Intent intent = new Intent(MainActivity.this, DragSelectGalleryActivity.class);
                    intent.putExtra(DragSelectGalleryActivity.FOLDER_ID, ((FolderRetriever.FolderItem) data).getId());
                    intent.putExtra(DragSelectGalleryActivity.FOLDER_NAME, ((FolderRetriever.FolderItem) data).getName());
                    startActivity(intent);
                }
            }
        });


        // Initialize image size
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        // Set item height by ScreenWidth
        adapter.setItemAttributes(calculateItemHeight(mImageThumbSpacing), mImageThumbSpacing);

        // Set memory cache to 10% of app memory
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.1f);

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getFragmentManager(), cacheParams);

    }

    private int calculateItemHeight(int spacing) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        return (screenWidth / COLUMNS) - (2 * spacing);
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

    public class FolderAdapter extends CustomRecyclerViewAdapter<FolderAdapter.ViewHolder> implements ViewSwitcher.ViewFactory {

        private Context                               mContext;
        private ArrayList<MediaItem>                  mFolderItems;



//        private ArrayList<FolderRetriever.FolderItem> mFolders;
//        private ArrayList<MediaItem>                  mAdapterItems;
//        private ArrayList<MediaItem>                  mCoverItems;
        //        private BitmapDrawable[]                      mCoverItemsCache;
//        private ArrayList<BitmapDrawable>             mCoverCache;
//        private ImageSwitcher                         mImageSwitcher;
//        private Handler                               mUpdateCoverHandler;
//        private Handler                               mGetCoverHandler;
//        private int                                   mScreenWidth;
//        private boolean IsPause = false;


        private final int VIEW_TYPE_HEADER = 0;
        private final int VIEW_TYPE_ITEM   = 1;

        public FolderAdapter(Context context, ArrayList<FolderRetriever.FolderItem> folders) {
            super();
            mContext = context;
//            mFolders = folders;
            mAdapterItems = folders;
//            Log.e("FD", "folder:" + mFolders.size());
//            Log.e("FD", "folder:" + mAdapterItems.size());

            initFolderData();
        }

        private void initFolderData() {
            ArrayList<MediaItem> photos = PhotoRetriever.getInstance(mContext).getItems();
            ArrayList<MediaItem> videos = VideoRetriever.getInstance(mContext).getItems();
            mFolderItems = new ArrayList<>();
            mFolderItems.addAll(photos);
            mFolderItems.addAll(videos);
            MediaUtil.sortByDate(mAdapterItems, MediaUtil.DESC, MediaUtil.DATE_MODIFIED);

            for (Object object : mAdapterItems) {
                FolderRetriever.FolderItem folder = (FolderRetriever.FolderItem) object;
                if (folder.getName().equals(MediaUtil.GALLERY_ALL_FOLDER_NAME)) {
                    folder.setPhotoCount(photos.size());
                    folder.setVideoCount(videos.size());
                    break;
                }
            }
        }

        @Override
        public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == VIEW_TYPE_HEADER) {
                View v = LayoutInflater.from(mContext).inflate(R.layout.gallery_list_header, parent, false);
                FolderAdapter.ViewHolder holder = new FolderAdapter.ViewHolder(v);
                v.setOnClickListener(this);
                return holder;
            } else {
                View v = LayoutInflater.from(mContext).inflate(R.layout.gallery_list_item, parent, false);
                FolderAdapter.ViewHolder holder = new FolderAdapter.ViewHolder(v);
                v.setOnClickListener(this);
                return holder;
            }
        }

        @Override
        public void onBindViewHolder(FolderAdapter.ViewHolder viewHolder, int position) {
            super.onBindViewHolder(viewHolder, position);

            try {
//                viewHolder.itemView.setTag(mFolders.get(position));

                FolderRetriever.FolderItem target = ((FolderRetriever.FolderItem) mAdapterItems.get(position));
                viewHolder.mTitle.setText(target.getName());
                viewHolder.mPhotoCount.setText(String.valueOf(target.getPhotoCount()));
                viewHolder.mVideoCount.setText(String.valueOf(target.getVideoCount()));

                String folder_id = target.getId();

                int idx = 0;
                if (!("".equals(folder_id)) && (null != folder_id)) {
                    for (int i = 0; i < mFolderItems.size(); i++) {
                        if (mFolderItems.get(i).getFolderId().equals(folder_id)) {
                            idx = i;
                            break;
                        }
                    }
                }

                if (getItemViewType(position) == VIEW_TYPE_HEADER) {
                    viewHolder.mImageSwitcher.setFactory(this);
                    viewHolder.mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.zoom_in));
                    viewHolder.mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.zoom_out));
                    viewHolder.mImageSwitcher.setImageDrawable(getCoverItems(1));
                } else {
                    mImageFetcher.loadImage(mFolderItems.get(idx), viewHolder.mImage);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean isHeader(int position) {
            return (position == 0);
        }

        private Drawable getCoverItems(int index) {
            if (null == mFolderItems.get(index)) return null;
            Bitmap bitmap = mFolderItems.get(index).getThumbnail();
            Log.e("REC", "bitmap:" + bitmap);
            return new BitmapDrawable(getResources(), bitmap);
        }

        @Override
        public int getItemCount() {
            return mAdapterItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return isHeader(position) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
        }

        @Override
        public View makeView() {
            ImageView imageView = new ImageView(mContext);
            imageView.setBackgroundColor(0xFF000000);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new
                    ImageSwitcher.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return imageView;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView      mTitle;
            public TextView      mPhotoCount;
            public TextView      mVideoCount;
            public ImageView     mImage;
            public ImageSwitcher mImageSwitcher;

            public ViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                mPhotoCount = (TextView) itemView.findViewById(R.id.txtPhotoCount);
                mVideoCount = (TextView) itemView.findViewById(R.id.txtVideoCount);
                mImage = (ImageView) itemView.findViewById(R.id.coverImg);
                mImageSwitcher = (ImageSwitcher) itemView.findViewById(R.id.imgSwitcher);
            }
        }
    }

}
