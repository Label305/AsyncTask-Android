package com.label305.stan.memorymgmt;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Util for caching bitmaps.
 */
public class BitmapCache {

	private static LruCache<String, Bitmap> mCache;

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

	public static void addBitmapToCache(String key, Bitmap bitmap) {
		if (mCache == null) {
			createCache();
		}

		if (getBitmapFromCache(key) == null) {
			mCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromCache(String key) {
		if (mCache == null)
			return null;
		return mCache.get(key);
	}
}
