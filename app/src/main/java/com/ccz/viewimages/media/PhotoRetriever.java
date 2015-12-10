package com.ccz.viewimages.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by user on 2015/5/4.
 */
public class PhotoRetriever {

    final String TAG = "PhotoRetriever";
    ContentResolver mContentResolver;
    private        ArrayList<MediaItem> mItems   = new ArrayList<MediaItem>();
    private static PhotoRetriever       instance = null;

    //private static final PhotoRetriever holder = new PhotoRetriever();
    public static PhotoRetriever getInstance(Context context) {
        if (instance == null && null != context) {
            instance = new PhotoRetriever(context);
        }
        return instance;
    }


    private PhotoRetriever(Context context) {
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

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] columns = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};

        String orderBy = "DATE_MODIFIED DESC";
        Cursor cur = mContentResolver.query(uri, columns, null, null, orderBy);

        if (cur == null || !cur.moveToFirst()) {
            Log.e(TAG, "<> failed to query.");
            return;
        }

        // add each photo to mItems
        int x = 0;
        do {

            int width = 0, height = 0;
            try {
                width = cur.getInt(cur.getColumnIndex(MediaStore.Images.Media.WIDTH));
                height = cur.getInt(cur.getColumnIndex(MediaStore.Images.Media.HEIGHT));
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e("MediaItem", "getWidth Exception:" + e.toString());
            }

            if (width == 0 || height == 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA)), options);

                width = options.outWidth;
                height = options.outHeight;
            }

            mItems.add(new MediaItem(
                    MediaItem.SOURCE_PHOTO,
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media._ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA)),         //path
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)), //name
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)),
                    width,
                    height,
                    cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.SIZE)),
                    cur.getInt(cur.getColumnIndex(MediaStore.Images.Media.ORIENTATION)),
                    cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)) * 1000L, // to ms
                    cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)), //unit is ms
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                    null
            ));

        } while (cur.moveToNext());

        if (cur != null) cur.close();
    }

//    public ContentResolver getContentResolver() {
//        return mContentResolver;
//    }


    public String getThumbPath(String mediaId) {
        //for missing thumbnail
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(cr, this.id, MediaStore.Images.Thumbnails.MINI_KIND,  options);

        String thumbnail = null;
        String[] thumbnailColumn = new String[]{MediaStore.Images.Thumbnails.DATA};
        Cursor cs = mContentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, thumbnailColumn, "IMAGE_ID =?", new String[]{mediaId}, null);
        if (cs != null && cs.moveToFirst()) {
            thumbnail = cs.getString(cs.getColumnIndex(thumbnailColumn[0]));
            Log.e("THUMB", "photoretriever:" + thumbnail);
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

        return MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, options);
    }

    public Bitmap getMicroThumbnail(String mediaId, BitmapFactory.Options options) {

        long id = 0;
        try {
            id = Long.valueOf(mediaId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (id <= 0) return null;

        return MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
    }

    public Bitmap getFullScreenThumbnail(String mediaId, BitmapFactory.Options options) {

        long id = 0;
        try {
            id = Long.valueOf(mediaId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (id <= 0) return null;

        return MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, options);
    }
}
