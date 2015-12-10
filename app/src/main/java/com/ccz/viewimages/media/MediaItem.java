package com.ccz.viewimages.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

/**
 * Created by user on 2015/5/6.
 */
public class MediaItem implements Serializable {

    public static final int DATE_MODIFIED = 0;
    public static final int DATE_ADDED    = 1;
    public static final int DATE_TAKEN    = 2;

    // Story Item source
    public static final String SOURCE_PHOTO  = "SOURCE_PHOTO";
    public static final String SOURCE_VIDEO  = "SOURCE_VIDEO";
    public static final String SOURCE_MUSIC  = "SOURCE_MUSIC";
    public static final String SOURCE_SHARE  = "SOURCE_SHARE";
    public static final String SOURCE_AUSTOR = "SOURCE_AUSTOR";
    public static final String SOURCE_UPNP   = "SOURCE_UPNP";

    // for Different source, ex: Photo, Video, Music, AuStor, Upnp
    protected String mSource;
    protected String mMediaId;
    protected String mName;
    protected String mPath;
    protected String mThumbPath;
    protected String mMimeType;
    protected int mWidth  = 0;
    protected int mHeight = 0;
    protected long mSize;
    protected int  mOrientation;
    protected long mDate_modified;
    protected long mDate_added;
    protected long mDate_taken;

    /*
    *  for MediaStore.Images and MediaStore.Video
    *  Folder id = BUCKET_ID ; Folder name = BUCKET_DISPLAY_NAME
    */
    protected String mFolder_id;
    protected String mFolder_name;

    public static final int SCALE_ORIGINAL = 0;
    public static final int SCALE_HD       = 1;
    public static final int SCALE_4K       = 2;  //4096x2160 or 3840x2160
    public static final int SCALE_SQUARE   = 3;  //1080x1080
    public static final int SCALE_480      = 4;  //min-length is 480

    public MediaItem(String source, String id, String path, String name, String mime_type, int width, int height, long size, int orientation, long date_modified, long date_taken, String folder_id, String folder_name, String thumb) {
        mSource = source;
        mMediaId = id;
        mName = name;
        mPath = path;
        mDate_modified = date_modified;
        mDate_taken = date_taken;
        mMimeType = mime_type;
        mWidth = width;
        mHeight = height;
        mSize = size;
        mOrientation = orientation;
        mFolder_id = folder_id;
        mFolder_name = folder_name;
        mThumbPath = thumb;
    }

    public String getMediaId() {
        return mMediaId;
    }

    public String getName() {
        return mName;
    }

    public String getPath() {
        return mPath;
    }

    public long getDate(int dType) {
        if (dType == DATE_ADDED)
            return mDate_added;
        else if (dType == DATE_TAKEN)
            return mDate_taken;
        else
            return mDate_modified;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public long getSize() {
        return mSize;
    }

    public String getFolderId() {
        return mFolder_id;
    }

    public String getFolderName() {
        return mFolder_name;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public Bitmap getThumbnail() {
        return getThumbnail(null);
    }

    public Bitmap getThumbnail(BitmapFactory.Options options) {

        if (mSource.equals(SOURCE_PHOTO)) {
            return PhotoRetriever.getInstance(null).getMiniThumbnail(this.mMediaId, options);
        } else if (mSource.equals(SOURCE_VIDEO)) {
            return VideoRetriever.getInstance(null).getMiniThumbnail(this.mMediaId, options);
        }
        return null;
    }

    public Bitmap getMicroThumbnail() {
        return getMicroThumbnail(null);
    }

    public Bitmap getMicroThumbnail(BitmapFactory.Options options) {

        if (mSource.equals(SOURCE_PHOTO)) {
            return PhotoRetriever.getInstance(null).getMicroThumbnail(this.mMediaId, options);
        } else if (mSource.equals(SOURCE_VIDEO)) {
            return VideoRetriever.getInstance(null).getMicroThumbnail(this.mMediaId, options);
        }
        return null;
    }

    public Bitmap getFullScreenThumbnail() {
        return getFullScreenThumbnail(null);
    }

    public Bitmap getFullScreenThumbnail(BitmapFactory.Options options) {

        if (mSource.equals(SOURCE_PHOTO)) {
            return PhotoRetriever.getInstance(null).getFullScreenThumbnail(this.mMediaId, options);
        } else if (mSource.equals(SOURCE_VIDEO)) {
            return VideoRetriever.getInstance(null).getFullScreenThumbnail(this.mMediaId, options);
        }
        return null;
    }

    public String getThumbnailPath() {
        if (null != mThumbPath) return mThumbPath;

        if (mSource.equals(SOURCE_PHOTO)) {
            mThumbPath = PhotoRetriever.getInstance(null).getThumbPath(this.mMediaId);
        } else if (mSource.equals(SOURCE_VIDEO)) {
            mThumbPath = VideoRetriever.getInstance(null).getThumbPath(this.mMediaId);
        } else { // Music, File
            return null;
        }

//        Log.e("ADDIT", "mThumbPath:" + mThumbPath);

        return mThumbPath;
    }

    public String getCacheKey() {
        return mPath;
    }

    public String getDetailPath() { // Get path to view big image
        if (mSource.equals(SOURCE_PHOTO)) {
//            Log.e("ADDIT", "mPath:" + mPath);
            return mPath;
        } else {
            return getThumbnailPath();
        }
    }

    public String getCastPath(Context context) {
        // is local file
        if (mSource.equals(SOURCE_PHOTO)) {
//            String path = MediaUtil.getHDThumbPath(getDetailPath(), getName(), getOrientation()).thumbPath;
            return getCompressPath(context, SCALE_4K, ".cast");
        } else {
            return mPath;
        }
    }

    public String getCompressPath(Context context, int type) {
        return getCompressPath(context, type, ".thumb");
    }

    public String getCompressPath(Context context, int type, String cacheFolder) {

        if (this.mSource.equals(SOURCE_SHARE) || this.mSource.equals(SOURCE_MUSIC)) {
            return null;
        } else if (this.mSource.equals(SOURCE_AUSTOR)) {
            //TODO:?
            return null;
        } else if (this.mSource.equals(SOURCE_VIDEO)) {
            return mThumbPath;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(this.mPath, options);

        int RESOLUTION_HD = 1280;
        int RESOLUTION_480 = 480;
        int RESOLUTION_4K = 4096;
        int RESOLUTION_SQUARE = 1080;

        int max_length;
        int width = options.outWidth;
        int height = options.outHeight;
        int reqWidth, reqHeight;

        max_length = RESOLUTION_HD;
        if (type == SCALE_4K) {
            max_length = RESOLUTION_4K;
        } else if (type == SCALE_SQUARE) {
            max_length = RESOLUTION_SQUARE;
        } else if (type == SCALE_480) {
            max_length = RESOLUTION_480;
        }

        String header = "compress_";
        if ((width >= max_length) || (height >= max_length)) {
            if (type == SCALE_SQUARE) {
                header = "square_";
                if (width >= height) {
                    reqWidth = ((max_length * width) / height);
                    reqHeight = max_length;
                } else {
                    reqHeight = ((max_length * height) / width);
                    reqWidth = max_length;
                }
            } else if (type == SCALE_480) {  //TODO: has bugs, ex: w>>h, mini-length is 480
                if (width >= height) {
                    reqHeight = max_length;
                    reqWidth = ((max_length * width) / height);
                } else {
                    reqWidth = max_length;
                    reqHeight = ((max_length * height) / width);
                }
            } else {
                if (width >= height) {
                    reqHeight = ((max_length * height) / width);
                    reqWidth = max_length;
                } else {
                    reqWidth = ((max_length * width) / height);
                    reqHeight = max_length;
                }
            }
        } else if ((width >= RESOLUTION_HD) || (height >= RESOLUTION_HD)) {
            // Keep the original size and compress 70
            header = "original_";
            reqWidth = width;
            reqHeight = height;
        } else {
            //No process
            return this.mPath;
        }

//        Log.e("COMPRESS", "image wxh:" + width + " x " + height + " >> " + reqWidth + " x " + reqHeight);

        // Calculate inSampleSize
        options.inSampleSize = MediaUtil.calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeFile(this.mPath, options);
            bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);

            if (type == SCALE_SQUARE && ((width >= max_length) || (height >= max_length))) {
                if (width >= height) {
                    bitmap = Bitmap.createBitmap(bitmap, (reqWidth - reqHeight) / 2, 0, RESOLUTION_SQUARE, RESOLUTION_SQUARE);
                } else {
                    bitmap = Bitmap.createBitmap(bitmap, 0, (reqHeight - reqWidth) / 2, RESOLUTION_SQUARE, RESOLUTION_SQUARE);
                }
            }

            if (this.mOrientation > 0) {
                bitmap = MediaUtil.rotateBitmap(bitmap, this.mOrientation);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //save HD thumbnail to internal storage
        File folder = new File(context.getApplicationInfo().dataDir + "/" + cacheFolder + "/");

        if (!folder.exists()) {
            folder.mkdirs();
//            Log.e("COMPRESS", "mkdirs: " + folder.getPath());
        }

        if (null == bitmap) return null;

        String targetPath = folder.getAbsolutePath() + "/" + header + this.getName();

        try {
            FileOutputStream outputStream = new FileOutputStream(targetPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Log.e("COMPRESS", "FileNotFoundEx:" + this.mPath + ":" + e.toString());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e("COMPRESS", "Exception:" + this.mPath + ":" + e.toString());
            return null;
        }

//        Log.e("COMPRESS", "targetPath:" + targetPath);
        return targetPath;
    }


    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public String getSource() {
        return mSource;
    }

    // Set
    public void setSource(String source) {
        mSource = source;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public void setDate(int dType, long newDate) {
        if (dType == DATE_ADDED)
            mDate_added = newDate;
        else if (dType == DATE_TAKEN)
            mDate_taken = newDate;
        else
            mDate_modified = newDate;
    }
}
