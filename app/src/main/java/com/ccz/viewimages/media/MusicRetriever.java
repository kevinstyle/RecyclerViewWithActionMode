package com.ccz.viewimages.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by user on 2015/5/4.
 */
public class MusicRetriever {

    final String TAG = "MusicRetriever";
    ContentResolver mContentResolver;
    private ArrayList<MediaItem> mItems = new ArrayList<MediaItem>();
    private static MusicRetriever instance = null;

    //private static final PhotoRetriever holder = new PhotoRetriever();
    public static MusicRetriever getInstance(Context context) {
            if (instance == null  && null != context)
                instance = new MusicRetriever(context);
        return instance;
    }


    private MusicRetriever(Context context) {
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

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //Log.e(TAG, "<> Querying media, URI: " + uri.toString());

        String[] columns = new String[] {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATE_MODIFIED, MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE };
        String orderBy = "_display_name ASC";  // or MediaStore.Audio.Media.DISPLAY_NAME + " ASC"

        Cursor cur = mContentResolver.query(uri, columns, null, null, orderBy);

        if (cur == null || !cur.moveToFirst()) {
            Log.e(TAG, "<> failed to query.");
            return;
        }

        // add each photo to mItems
        do {
            mItems.add(new MediaItem(
                    MediaItem.SOURCE_MUSIC,
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),         //path
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)), //name
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)),
                    0,
                    0,
                    cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.SIZE)),
                    0,    //orientation
                    cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)) * 1000L,  // to ms
                    0,    //date_taken
                    null, //folder_id
                    null, //folder_name
                    null  //thumb
            ));
        } while (cur.moveToNext());

        if (cur != null) cur.close();
    }
    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

}
