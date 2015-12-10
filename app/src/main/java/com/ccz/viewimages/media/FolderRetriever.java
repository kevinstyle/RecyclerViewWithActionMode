package com.ccz.viewimages.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FolderRetriever {

    final String TAG = "FolderRetriever";
    ContentResolver mContentResolver;
    private ArrayList<FolderItem> mItems = new ArrayList<FolderItem>();
    private static FolderRetriever instance = null;

    //private static final PhotoRetriever holder = new PhotoRetriever();
    public static FolderRetriever getInstance(Context context) {
        if (instance == null)
            instance = new FolderRetriever(context.getContentResolver());
        return instance;
    }


    private FolderRetriever(ContentResolver cr) {
        mContentResolver = cr;
    }

    public ArrayList<FolderItem> getItems() {
        return mItems;
    }

    public void prepare() {

        mItems.clear();

        mItems.add(new FolderRetriever.FolderItem("", MediaUtil.GALLERY_ALL_FOLDER_NAME, 0, 0));

        ArrayList<FolderItem> _Items = new ArrayList<FolderItem>();

        //Query folders of Photos
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] columns = new String[] {MediaStore.Images.Media.BUCKET_ID, "COUNT(" + MediaStore.Images.Media._ID + ") as cnt", MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String BUCKET_GROUP_BY = "1) GROUP BY (1";
        String orderBy = "bucket_display_name ASC";
        Cursor cur = mContentResolver.query(uri, columns, BUCKET_GROUP_BY, null, orderBy);

        if (cur == null || !cur.moveToFirst()) {
            Log.e(TAG, "<> cursor has errors");
            return;
        }

        do {
            _Items.add(new FolderItem(
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                    cur.getInt(cur.getColumnIndex("cnt")),
                    0
            ));
        } while (cur.moveToNext());

        if (cur != null) cur.close();


        // Query folders of Videos
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        columns = new String[]{MediaStore.Video.Media.BUCKET_ID, "COUNT(" + MediaStore.Video.Media._ID + ") as cnt", MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        cur = mContentResolver.query(uri, columns, BUCKET_GROUP_BY, null, orderBy);

        if (cur == null || !cur.moveToFirst()) {
            Log.e(TAG, "<> cursor has errors");
            return;
        }

        do {
            String id = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            String name = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            int cnt = cur.getInt(cur.getColumnIndex("cnt"));
            boolean bCreateNew = true;
            for (int i = 0; i < _Items.size(); i++) {
                if (_Items.get(i).getId().equals(id)) {
                    _Items.get(i).setVideoCount(cnt);
                    bCreateNew = false;
                    break;
                }
            }

            if (bCreateNew) {
                _Items.add(new FolderItem(id, name, 0, cnt));
            }

        } while (cur.moveToNext());
        if (cur != null) cur.close();

        //sort
        sortByFolder(_Items, MediaUtil.ASC);
        mItems.addAll(_Items);

    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    public static class FolderItem {

        String id;
        String name;
        int photoCount;
        int videoCount;

        public FolderItem(String id, String name, int photoCount, int videoCount) {
            this.id = id;
            this.name = name;
            this.photoCount = photoCount;
            this.videoCount = videoCount;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhotoCount(int cnt) {
            this.photoCount = cnt;
        }

        public void setVideoCount(int cnt) {
            this.videoCount = cnt;
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public int getPhotoCount() {
            return this.photoCount;
        }

        public int getVideoCount() {
            return this.videoCount;
        }
    }

    public static void sortByFolder(ArrayList<FolderItem> items, final int orderBy) {
        Collections.sort(items, new Comparator<FolderItem>() {
            public int compare(FolderItem o1, FolderItem o2) {
                return orderBy * o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }
}
