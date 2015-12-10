package com.ccz.viewimages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Checkable;

import com.afollestad.materialcab.MaterialCab;
import com.ccz.viewimages.custom.ClickListener;
import com.ccz.viewimages.custom.DragSelectGalleryAdapter;
import com.ccz.viewimages.displayingbitmaps.util.ImageCache;
import com.ccz.viewimages.displayingbitmaps.util.ImageFetcher;
import com.ccz.viewimages.dragselect.DragSelectRecyclerView;
import com.ccz.viewimages.dragselect.DragSelectRecyclerViewAdapter;
import com.ccz.viewimages.media.MediaItem;
import com.ccz.viewimages.media.MediaUtil;
import com.ccz.viewimages.media.PhotoRetriever;
import com.ccz.viewimages.media.VideoRetriever;

import java.util.ArrayList;

public class DragSelectGalleryActivity extends AppCompatActivity implements DragSelectRecyclerViewAdapter.SelectionListener, ClickListener {

    protected ImageFetcher             mImageFetcher;
    protected int                      mImageThumbSize;
    protected int                      mImageThumbSpacing;
    private   String                   mCurrentFolderID;
    private   String                   mCurrentFolderName;
    private   MaterialCab              mCab;
    private   DragSelectGalleryAdapter mAdapter;
    private   DragSelectRecyclerView   mGridView;
    private   GridLayoutManager        mLayoutManager;

    public static          int    COLUMNS         = 3;
    protected static final String IMAGE_CACHE_DIR = "thumbs";

    public static final String FOLDER_ID   = "FOLDER_ID";
    public static final String FOLDER_NAME = "FOLDER_NAME";

    public final static String FILTER_ALL   = "FILTER_ALL";
    public final static String FILTER_PHOTO = "FILTER_PHOTO";
    public final static String FILTER_VIDEO = "FILTER_VIDEO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init parameters
        mCurrentFolderID = "";
        mCurrentFolderName = MediaUtil.GALLERY_ALL_FOLDER_NAME;

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mCurrentFolderID = bundle.getString(FOLDER_ID);
            mCurrentFolderName = bundle.getString(FOLDER_NAME);
        }

        // init data
        ArrayList<MediaItem> photos = PhotoRetriever.getInstance(this).getItems();
        ArrayList<MediaItem> videos = VideoRetriever.getInstance(this).getItems();
        ArrayList<MediaItem> list = new ArrayList<>();
        list.addAll(photos);
        list.addAll(videos);
        MediaUtil.sortByDate(list, MediaUtil.DESC, MediaUtil.DATE_MODIFIED);

        // init gridview
        mGridView = (DragSelectRecyclerView) findViewById(R.id.gridView);
        mAdapter = new DragSelectGalleryAdapter(this, list, this, mGridView);
        mAdapter.setSelectionListener(this);

        mLayoutManager = new GridLayoutManager(this, COLUMNS);
        mGridView.setLayoutManager(mLayoutManager);
        mGridView.setAdapter(mAdapter);

        mAdapter.getFilter().filter(FILTER_ALL + "/" + mCurrentFolderID);

        // Initialize image size
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mImageThumbSize = calculateItemHeight(mImageThumbSpacing);
        mAdapter.setItemHeightAndMargin(mImageThumbSize, mImageThumbSpacing);
        mGridView.hasFixedSize();

        // Set memory cache to 10% of app memory
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.1f);

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getFragmentManager(), cacheParams);

        mAdapter.setImageFetcher(mImageFetcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_enter) {
            mAdapter.getDragSelectHelper().onStartActionMode();
            mGridView.setDragSelectActive(true);
            return true;
        }

        if (item.getItemId() == R.id.action_grid) {
            if (COLUMNS < 4) {
                COLUMNS++;
            } else {
                COLUMNS = 2;
            }
            int itemHeight = calculateItemHeight(mImageThumbSpacing);
            mAdapter.setItemHeightAndMargin(itemHeight, mImageThumbSpacing);
            mGridView.setLayoutManager(new GridLayoutManager(this, COLUMNS));
            mImageFetcher.setImageSize(itemHeight);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int calculateItemHeight(int spacing) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        return (screenWidth / COLUMNS) - (2 * spacing);
    }

    @Override
    public void onClick(int index) {
        mAdapter.toggleSelected(index);
    }

    @Override
    public void onLongClick(int index) {
        for (int i = 0; i <= mAdapter.getItemCount(); i++) {
            mAdapter.notifyItemChanged(i);
        }
        mGridView.setDragSelectActive(true, index);
    }

    @Override
    public void onCheckChange(int index, Checkable v, boolean isChecked) {
        mAdapter.setSelected(index, isChecked);
    }

    @Override
    public void onDragSelectionChanged(int count) {
        mAdapter.getDragSelectHelper().onItemSelectedStateChanged();
    }
}
