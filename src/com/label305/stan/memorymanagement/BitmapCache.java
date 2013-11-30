package com.label305.stan.memorymanagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Util for caching bitmaps.
 */
public class BitmapCache implements ImageCache {

	private static LruCache<String, Bitmap> mCache;

	private static File sCacheDir;
	private static boolean sIsDiskWritable = false;
	private static boolean sIsDiskReadable = false;

	/**
	 * Need to initialize Bitmapcache at app startup, to be able to read/write
	 * to disk cache
	 */
	public static void initialize(Context context) {
		sCacheDir = context.getExternalCacheDir();
		if (sCacheDir != null) {
			sIsDiskWritable = sCacheDir.canWrite();
			sIsDiskReadable = sCacheDir.canRead();
		}
	}

	@Override
	public Bitmap getBitmap(String key) {
		return getBitmapFromCache(key);
	}

	public static Bitmap getBitmapFromCache(String key) {
		Bitmap retVal = null;
		if (mCache != null)
			retVal = mCache.get(key);

		if (retVal == null && sIsDiskReadable) {
			retVal = getBitmapFromDiskCache(key);
			if (retVal != null && !retVal.isRecycled()) {
				addBitmapToCache(key, retVal, false);
			}
		}
		return retVal;
	}

	private static Bitmap getBitmapFromDiskCache(String key) {
		if (sIsDiskReadable) {
			File file = new File(sCacheDir, makeFileName(key));

			if (file.exists()) {
				return BitmapFactory.decodeFile(file.getAbsolutePath());
			}
		}
		return null;
	}

	@Override
	public void putBitmap(String key, Bitmap bitmap) {
		addBitmapToCache(key, bitmap);
		addBitmapToDiskCache(key, bitmap);
	}

	public static void addBitmapToCache(String key, Bitmap bitmap) {
		addBitmapToCache(key, bitmap, true);
	}

	public static void addBitmapToCache(String key, Bitmap bitmap, boolean checkForCache) {
		if (mCache == null) {
			createCache();
		}

		if (!checkForCache || getBitmapFromCache(key) == null) {
			mCache.put(key, bitmap);
		}
	}

	public static void addBitmapToDiskCache(final String key, final Bitmap bitmap) {
		if (sIsDiskWritable && getBitmapFromDiskCache(key) == null) {
			Runnable addToDiskCacheRunnable = new Runnable() {

				@Override
				public void run() {
					try {
						File file = new File(sCacheDir, makeFileName(key));
						FileOutputStream fOut = new FileOutputStream(file);

						bitmap.compress(Bitmap.CompressFormat.PNG, 0, fOut);

						fOut.flush();
						fOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			Thread addToDiskCacheThread = new Thread(addToDiskCacheRunnable);
			addToDiskCacheThread.start();
		}
	}

	private static void createCache() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		mCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}

	private static String makeFileName(String key) {
		return key.replaceAll("/", "_").replaceAll("\\.", "_");
	}
}
