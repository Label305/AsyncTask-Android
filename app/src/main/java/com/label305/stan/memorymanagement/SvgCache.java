/*
 * |   _            _          _ ____   ___  _____   |
 * |  | |          | |        | |___ \ / _ \| ____|  |
 * |  | |      __ _| |__   ___| | __) | |_| | |__    |
 * |  | |     / _` | '_ \ / _ \ ||__ <|     |___ \   |
 * |  | |____| (_| | |_) |  __/ |___) |     |___) |  |
 * |  |______|\__,_|_.__/ \___|_|____/ \___/|____/   |
 *
 * @author Nick van den Berg <nick@label305.com>
 *
 * Copyright (c) 2013 Label305. All Right Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.label305.stan.memorymanagement;

import android.content.Context;
import android.support.v4.util.LruCache;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.label305.stan.utils.Logger;

public class SvgCache {

    private static LruCache<Integer, SVG> sCache;

    private SvgCache() {
    }

    public static void addSvgToCache(final int key, final SVG svg) {
        if (sCache == null) {
            createCache();
        }

        sCache.put(key, svg);
    }

    public static SVG getSvgFromCache(final int key) {
        SVG result = null;
        if (sCache != null) {
            result = sCache.get(key);
        }
        return result;
    }

    private static void createCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        sCache = new IntegerSVGLruCache(cacheSize);
    }

    /**
     * Starts a new thread for retrieving and caching given svg resources.
     * @param context the context
     * @param svgResources the resource id's of the svg's to cache.
     */
    public static void asyncCache(final Context context, final int... svgResources) {
        AsyncCacheRunnable runnable = new AsyncCacheRunnable(context, svgResources);
        new Thread(runnable).start();
    }

    private static class IntegerSVGLruCache extends LruCache<Integer, SVG> {

        IntegerSVGLruCache(final int cacheSize) {
            super(cacheSize);
        }

        @Override
        protected int sizeOf(final Integer key, final SVG value) {
            return value.toString().length();
        }
    }

    private static class AsyncCacheRunnable implements Runnable {

        private final Context mContext;
        private final int[] mSvgResources;

        AsyncCacheRunnable(final Context context, final int... svgResources) {
            mContext = context;
            mSvgResources = svgResources;
        }

        @Override
        public void run() {
            try {
                for (int resource : mSvgResources) {
                    SVG item = SVG.getFromResource(mContext, resource);
                    addSvgToCache(resource, item);
                }
            } catch (SVGParseException e) {
                Logger.log(e);
            }
        }
    }
}
