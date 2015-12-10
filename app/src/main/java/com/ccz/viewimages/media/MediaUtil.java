package com.ccz.viewimages.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 2015/5/12.
 */
public class MediaUtil {
    public static final int DESC = -1;
    public static final int ASC  = 1;

    public static final int DATE_MODIFIED     = 0;
    public static final int DATE_ADDED        = 1;
    public static final int DATE_TAKEN        = 2;
    public static final int ORDER_BY_NAME     = 3;
    public static final int ORDER_BY_SIZE     = 4;
    public static final int hdThumbnailLength = 1280;
    public static File HD_thumbnailDir;
    public static final String GALLERY_ALL_FOLDER_NAME = "COMBO";

    private MediaUtil() {
    }


    public static void sortByName(ArrayList<MediaItem> items, final int orderBy) {
        try {
            Collections.sort(items, new Comparator<MediaItem>() {
                public int compare(MediaItem o1, MediaItem o2) {
                    return orderBy * o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sortByFolder(ArrayList<MediaItem> items, final int orderBy) {
        try {
            Collections.sort(items, new Comparator<MediaItem>() {
                public int compare(MediaItem o1, MediaItem o2) {
                    return orderBy * o1.getFolderName().compareToIgnoreCase(o2.getFolderName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sortByDate(ArrayList<MediaItem> items, final int orderBy, final int dType) {

        try {
            Collections.sort(items, new Comparator<MediaItem>() {
                public int compare(MediaItem o1, MediaItem o2) {
                    if (o1.getDate(dType) > o2.getDate(dType))
                        return orderBy;
                    else if (o1.getDate(dType) < o2.getDate(dType))
                        return orderBy * -1;
                    else
                        return 0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sortBySize(ArrayList<MediaItem> items, final int orderBy) {
        try {
            Collections.sort(items, new Comparator<MediaItem>() {
                public int compare(MediaItem o1, MediaItem o2) {
                    if (o1.getSize() > o2.getSize())
                        return orderBy;
                    else if (o1.getSize() < o2.getSize())
                        return orderBy * -1;
                    else
                        return 0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap rotateBitmap(Bitmap oriBitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(oriBitmap, 0, 0, oriBitmap.getWidth(), oriBitmap.getHeight(), matrix, true);
    }

}
