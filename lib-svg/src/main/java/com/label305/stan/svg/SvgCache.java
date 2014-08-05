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

package com.label305.stan.svg;

import android.content.Context;
import android.support.v4.util.LruCache;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.label305.stan.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SvgCache {

    private static SvgCache sInstance;

    @NotNull
    private final LruCache<Integer, SVG> mCache;

    private SvgCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mCache = new IntegerSVGLruCache(cacheSize);
    }

    public static SvgCache getInstance() {
        synchronized (SvgCache.class) {
            if (sInstance == null) {
                sInstance = new SvgCache();
            }
            return sInstance;
        }
    }

    public void addSvgToCache(final int key, final SVG svg) {
        mCache.put(key, svg);
    }

    @Nullable
    public SVG getSvgFromCache(final int key) {
        return mCache.get(key);
    }


    /**
     * Starts a new thread for retrieving and caching given svg resources.
     *
     * @param context      the context
     * @param svgResources the resource id's of the svg's to cache.
     */
    public void asyncCache(@NotNull final Context context, @NotNull final int... svgResources) {
        AsyncCacheRunnable runnable = new AsyncCacheRunnable(context, svgResources);
        new Thread(runnable).start();
    }

    private static class IntegerSVGLruCache extends LruCache<Integer, SVG> {

        IntegerSVGLruCache(final int cacheSize) {
            super(cacheSize);
        }

        @Override
        protected int sizeOf(final Integer key, @NotNull final SVG value) {
            return value.toString().length();
        }
    }

    private class AsyncCacheRunnable implements Runnable {

        @NotNull
        private final Context mContext;

        @NotNull
        private final int[] mSvgResources;

        AsyncCacheRunnable(@NotNull final Context context, @NotNull final int... svgResources) {
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
