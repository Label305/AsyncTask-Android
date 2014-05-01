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
import com.label305.stan.R;

/**
 * Created by Label305 on 02/04/2014.
 */
public class SvgCache {

    private static LruCache<Integer, SVG> sCache;

    public static void addSvgToCache(int key, SVG svg) {
        if(sCache == null) {
            createCache();
        }

        sCache.put(key, svg);
    }

    public static SVG getSvgFromCache(int key) {
        SVG retVal = null;

        if(sCache != null) {
            retVal = sCache.get(key);
        }
        return retVal;
    }

    private static void createCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        sCache = new LruCache<Integer, SVG>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, SVG svg) {
                String str = svg.toString();
                return str.length();
            }
        };
    }

    public static void asyncCache(final Context context, final int... svgResources){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    for(int resource : svgResources) {
                        SVG item = SVG.getFromResource(context, resource);
                        addSvgToCache(resource, item);
                    }
                } catch (SVGParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
