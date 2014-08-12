package com.label305.stan.svg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Util for caching bitmaps.
 */
public class BitmapCache {

    private static final Pattern SLASH_PATTERN = Pattern.compile("/");

    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

    @Nullable
    private static BitmapCache sInstance;

    @Nullable
    private final File mCacheDir;

    @NotNull
    private final LruCache<String, Bitmap> mCache;

    private boolean mIsDiskWritable;

    private boolean mIsDiskReadable;

    private BitmapCache(@NotNull final Context context) {
        mCacheDir = context.getExternalCacheDir();
        if (mCacheDir != null) {
            mIsDiskWritable = mCacheDir.canWrite();
            mIsDiskReadable = mCacheDir.canRead();
        }

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mCache = new MyCache(cacheSize);
    }

    @NotNull
    public static BitmapCache getInstance(@NotNull final Context context) {
        synchronized (BitmapCache.class) {
            if (sInstance == null) {
                sInstance = new BitmapCache(context);
            }
            return sInstance;
        }
    }

    @Nullable
    public Bitmap getBitmapFromCache(@NotNull final String key) {
        Bitmap result = mCache.get(key);

        if (result == null && mIsDiskReadable) {
            result = getBitmapFromDiskCache(key);
            if (result != null && !result.isRecycled()) {
                addBitmapToCache(key, result);
            }
        }
        return result;
    }

    public void addBitmapToCache(@NotNull final String key, final Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            mCache.put(key, bitmap);
        }
    }

    public void addBitmapToDiskCache(@NotNull final String key, @NotNull final Bitmap bitmap) {
        if (mIsDiskWritable && getBitmapFromDiskCache(key) == null) {
            Runnable addToDiskCacheRunnable = new AddToDiskCacheRunnable(key, bitmap);
            Thread addToDiskCacheThread = new Thread(addToDiskCacheRunnable);
            addToDiskCacheThread.start();
        }
    }

    @Nullable
    public Bitmap getBitmap(@NotNull final String key) {
        return getBitmapFromCache(key);
    }

    public void putBitmap(@NotNull final String key, @NotNull final Bitmap bitmap) {
        addBitmapToCache(key, bitmap);
        addBitmapToDiskCache(key, bitmap);
    }

    @Nullable
    private Bitmap getBitmapFromDiskCache(@NotNull final CharSequence key) {
        if (mIsDiskReadable) {
            File file = new File(mCacheDir, makeFileName(key));

            if (file.exists()) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }
        return null;
    }

    private static String makeFileName(@NotNull final CharSequence key) {
        return DOT_PATTERN.matcher(SLASH_PATTERN.matcher(key).replaceAll("_")).replaceAll("_");
    }

    private static class MyCache extends LruCache<String, Bitmap> {

        private MyCache(final int cacheSize) {
            super(cacheSize);
        }

        @Override
        protected int sizeOf(final String key, @NotNull final Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    }

    private class AddToDiskCacheRunnable implements Runnable {

        private final String mKey;

        private final Bitmap mBitmap;

        private AddToDiskCacheRunnable(final String key, final Bitmap bitmap) {
            mKey = key;
            mBitmap = bitmap;
        }

        @Override
        public void run() {
            try {
                File file = new File(mCacheDir, makeFileName(mKey));
                FileOutputStream fOut = new FileOutputStream(file);

                mBitmap.compress(Bitmap.CompressFormat.PNG, 0, fOut);

                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
