package com.ccz.viewimages.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by user on 2015/5/4.
 */
public class VideoRetriever {

    final String TAG = "VideoRetriever";
    ContentResolver mContentResolver;
    private ArrayList<MediaItem> mItems = new ArrayList<MediaItem>();
    private static VideoRetriever instance = null;

    //private static final PhotoRetriever holder = new PhotoRetriever();
    public static VideoRetriever getInstance(Context context) {
        if (instance == null && null != context)
                instance = new VideoRetriever(context);
        return instance;
    }


    private VideoRetriever(Context context) {
        mContentResolver = context.getContentResolver();
    }

    public ArrayList<MediaItem> getItems() {
        return mItems;
    }

    /**
     * Loads photo data. This method may take long, so be sure to call it asynchronously without
     * blocking the main thread.
     */
    public void prepare() {

        mItems.clear();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] columns = new String[] {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.WIDTH, MediaStore.Video.Media.HEIGHT};

        String orderBy = "DATE_MODIFIED DESC";
        Cursor cur = mContentResolver.query(uri, columns, null, null, orderBy);

        if (cur == null || !cur.moveToFirst()) {
            Log.e(TAG, "<> failed to query.");
            return;
        }


        String thumbnail = "";
        // add each video to mItems
        do {
            int width = 0, height = 0;
            try {
                width = cur.getInt(cur.getColumnIndex(MediaStore.Video.Media.WIDTH));
                height = cur.getInt(cur.getColumnIndex(MediaStore.Video.Media.HEIGHT));
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e("MediaItem", "getWidth Exception:" + e.toString());
            }

            if (width == 0 || height == 0) {
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DATA)));
                String w = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String h = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

                try {
                    width = (null != w) ? Integer.valueOf(w) : 0;
                    height = (null != h) ? Integer.valueOf(h) : 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mItems.add(new MediaItem(
                    MediaItem.SOURCE_VIDEO,
                    cur.getString(cur.getColumnIndex(MediaStore.Video.Media._ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DATA)),         //path
                    cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)), //name
                    cur.getString(cur.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)),
                    width,
                    height,
                    cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.SIZE)),
                    0,  //orientation
                    cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)) * 1000L,  // to ms
                    cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN)),  // unit is ms
                    cur.getString(cur.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)),
                    null
            ));
        } while (cur.moveToNext());

        if (cur != null) cur.close();
    }
    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    public long getDuration(String mediaId) {

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] columns = new String[] {MediaStore.Video.Media.DURATION};

        Cursor cur = mContentResolver.query(uri, columns, "VIDEO_ID =?", new String[] {mediaId}, null);

        if (cur == null || !cur.moveToFirst()) {
            Log.e(TAG, "<> failed to query.");
            return 0;
        }

        long duration = cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DURATION));
        if (cur != null) cur.close();

        return duration;
    }

    public String getThumbPath(String mediaId) {
        String thumbnail = null;
        String[] thumbnailColumn = new String[]{MediaStore.Video.Thumbnails.DATA};
        Cursor cs = mContentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbnailColumn, "VIDEO_ID =?", new String[]{mediaId}, null);
        if (cs != null && cs.moveToFirst()) {
            thumbnail = cs.getString(cs.getColumnIndex(thumbnailColumn[0]));
        }
        if (cs != null) cs.close();
        return thumbnail;
    }

    public Bitmap getMiniThumbnail(String mediaId, BitmapFactory.Options options) {

        long id = 0;
        try {
            id = Long.valueOf(mediaId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (id <= 0) return null;

        return MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Video.Thumbnails.MINI_KIND, options);
    }

    public Bitmap getMicroThumbnail(String mediaId, BitmapFactory.Options options) {

        long id = 0;
        try {
            id = Long.valueOf(mediaId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (id <= 0) return null;

        return MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
    }

    public Bitmap getFullScreenThumbnail(String mediaId, BitmapFactory.Options options) {

        long id = 0;
        try {
            id = Long.valueOf(mediaId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (id <= 0) return null;

        return MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND, options);
    }
}
