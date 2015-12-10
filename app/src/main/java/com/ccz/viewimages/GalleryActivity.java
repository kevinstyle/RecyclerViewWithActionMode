package com.ccz.viewimages;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccz.viewimages.custom.CustomRecyclerViewAdapter;
import com.ccz.viewimages.displayingbitmaps.util.ImageCache;
import com.ccz.viewimages.displayingbitmaps.util.ImageFetcher;
import com.ccz.viewimages.media.MediaItem;
import com.ccz.viewimages.media.MediaUtil;
import com.ccz.viewimages.media.PhotoRetriever;
import com.ccz.viewimages.media.VideoRetriever;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    protected ImageFetcher mImageFetcher;
    protected int          mImageThumbSize;
    protected int          mImageThumbSpacing;
    private   String       mCurrentFolderID;
    private   String       mCurrentFolderName;

    public static final int COLUMNS = 3;
    protected static final String IMAGE_CACHE_DIR = "thumbs";

    public static final String FOLDER_ID   = "FOLDER_ID";
    public static final String FOLDER_NAME = "FOLDER_NAME";

    public final static String FILTER_ALL   = "FILTER_ALL";
    public final static String FILTER_PHOTO = "FILTER_PHOTO";
    public final static String FILTER_VIDEO = "FILTER_VIDEO";

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
        RecyclerView gridView = (RecyclerView) findViewById(R.id.gridView);
        GalleryAdapter adapter = new GalleryAdapter(this, list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMNS);
        gridView.setLayoutManager(layoutManager);
        gridView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CustomRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object data) {
                if (null != data) {
                    Toast.makeText(GalleryActivity.this, "Click image:" + ((MediaItem) data).getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.getFilter().filter(FILTER_ALL + "/" + mCurrentFolderID);

        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

//                final ActionBar ab = getSupportActionBar();
//                if (ab != null) {
//                    Log.e("REC", "motionEvent:" + motionEvent);
//                    switch (motionEvent.getActionMasked()) {
//                        case MotionEvent.ACTION_DOWN:
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            if (ab.isShowing()) {
//                                ab.hide();
//                            }
//                            break;
//                        case MotionEvent.ACTION_UP:
//                            if (!ab.isShowing()) {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ab.show();
//                                    }
//                                }, 1000);
//                            }
//                            break;
//                        case MotionEvent.ACTION_CANCEL:
//                            break;
//                        default:
//                            break;
//                    }
//                }

                return false;
            }
        });

        // Initialize image size
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        // Set item height by ScreenWidth
        adapter.setItemAttributes(calculateItemHeight(mImageThumbSpacing), mImageThumbSpacing);
        gridView.setHasFixedSize(true);

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
        return (screenWidth / COLUMNS) - spacing;
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

//    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> implements View.OnClickListener, Filterable {
    public class GalleryAdapter extends CustomRecyclerViewAdapter<GalleryAdapter.ViewHolder> implements Filterable {

        private Context                               mContext;
//        private ArrayList<MediaItem>                  mAdapterItems;
        private ArrayList<MediaItem>                  mAllList;
        private Filter mFilter;
//        private IOnRecyclerViewItemClickListener mOnItemClickListener = null;

        private final int VIEW_TYPE_PHOTO = 0;
        private final int VIEW_TYPE_VIDEO = 1;

        private final String MIME_IMAGE = "image";
        private final String MIME_VIDEO = "video";

        public GalleryAdapter(Context context, ArrayList<MediaItem> list) {
            super();
            mContext = context;
            mAdapterItems = list;
            mAllList = list;
        }

        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == VIEW_TYPE_VIDEO) {
                View v = LayoutInflater.from(mContext).inflate(R.layout.gallery2_video_item, parent, false);
                GalleryAdapter.ViewHolder holder = new GalleryAdapter.ViewHolder(v);
                v.setOnClickListener(this);
                return holder;
            } else {
                View v = LayoutInflater.from(mContext).inflate(R.layout.gallery2_photo_item, parent, false);
                GalleryAdapter.ViewHolder holder = new GalleryAdapter.ViewHolder(v);
                v.setOnClickListener(this);
                return holder;
            }
        }

        @Override
        public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int position) {
            super.onBindViewHolder(viewHolder, position);
            try {

                MediaItem target = (MediaItem) mAdapterItems.get(position);
//                viewHolder.itemView.setTag(target);

                if (getItemViewType(position) == VIEW_TYPE_VIDEO) {
                    viewHolder.mTitle.setText(target.getName());
                }

//                mImageFetcher.loadImage(target, viewHolder.mImage);
                mImageFetcher.loadImageWithDoubleThumb(target, viewHolder.mImage, true);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean isVideo(int position) {
            if (null == mAdapterItems.get(position)) return false;
            return ((MediaItem) mAdapterItems.get(position)).getMimeType().contains(MIME_VIDEO);
        }

        @Override
        public int getItemViewType(int position) {
            return isVideo(position) ? VIEW_TYPE_VIDEO : VIEW_TYPE_PHOTO;
        }

        @Override
        public int getItemCount() {
            return mAdapterItems.size();
        }

//        @Override
//        public void onClick(View view) {
//            if (mItemClickListener != null) {
//                mItemClickListener.onItemClick(view, view.getTag());
//
//                Toast.makeText(mContext, "Click image:" + ((MediaItem) view.getTag()).getName(), Toast.LENGTH_SHORT).show();
//            }
//
////            Bundle b = new Bundle();
////            b.putString(GalleryActivity.FOLDER_ID, ((FolderRetriever.FolderItem) v.getTag()).getId());
////            b.putString(GalleryActivity.FOLDER_NAME, ((FolderRetriever.FolderItem) v.getTag()).getName());
////            f.setArguments(b);
//
////            Intent intent = new Intent(mContext, GalleryActivity.class);
////            intent.putExtra(GalleryActivity.FOLDER_ID, ((FolderRetriever.FolderItem) view.getTag()).getId());
////            intent.putExtra(GalleryActivity.FOLDER_NAME, ((FolderRetriever.FolderItem) view.getTag()).getName());
////            startActivity(intent);
//        }

//        public void setOnItemClickListener(IOnRecyclerViewItemClickListener listener) {
//            this.mOnItemClickListener = listener;
//        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new FolderFilter();
            }
            return mFilter;
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
//                        if (p.getFolderId().toUpperCase().equals(constraint.toString().toUpperCase())) {
                        // folderId = null is Combo folder
                        if (null == folderId || folderId.length() == 0 || folderId.toLowerCase().equals(p.getFolderId().toUpperCase())) {
                            if (fileType.equals(FILTER_PHOTO)) {
                                if (p.getMimeType().contains(MIME_IMAGE)) {
                                    nList.add(p);
                                }
                            } else if (fileType.equals(FILTER_VIDEO)) {
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
//                MediaUtil.sortMediaItems(mAdapterItems);
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView      mTitle;
            public ImageView     mImage;

            public ViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView) itemView.findViewById(R.id.video_title);
                mImage = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }

}
