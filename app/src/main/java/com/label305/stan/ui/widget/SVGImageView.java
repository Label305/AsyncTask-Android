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

package com.label305.stan.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.label305.stan.R;
import com.label305.stan.memorymanagement.BitmapCache;
import com.label305.stan.utils.ImageUtils;
import com.label305.stan.utils.Logger;

/**
 * Created by Label305 on 02/04/2014.
 */
public class SVGImageView extends ImageView {

    private static LruCache<Integer, SVG> sSVGCache;

    private int mSvgResourceId;

    private boolean mInvertSvg = false;
    private boolean mOverrideColors = false;
    private boolean mIsPressable = false;

    private int mSvgColor;
    private int mPressedSvgColor;


    public SVGImageView(Context context) {
        super(context);
        init(null);
    }

    public SVGImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SVGImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        createCache();

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SVGImageView,
                0, 0);

        try {
            setDoInvertSvg(a.getBoolean(R.styleable.SVGImageView_invertSvg, false));
            setOverrideColors(a.getBoolean(R.styleable.SVGImageView_overrideColors, false));
            setPressableSvg(a.getBoolean(R.styleable.SVGImageView_isPressable, false));

            if (mOverrideColors) {
                setSvgColor(a.getColor(R.styleable.SVGImageView_svgColor, Color.BLACK));
            }

            if (mIsPressable) {
                setPressedSvgColor(a.getColor(R.styleable.SVGImageView_pressedSvgColor, Color.WHITE));
            }
            setSVGResource(a.getResourceId(R.styleable.SVGImageView_svg, 0));
        } finally {
            a.recycle();
        }
    }

    private static void createCache() {
        if (sSVGCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;

            sSVGCache = new LruCache<Integer, SVG>(cacheSize) {
                @Override
                protected int sizeOf(Integer key, SVG bitmap) {
                    String str = bitmap.toString();
                    return str.length();
                }
            };
        }
    }

    private void setDoInvertSvg(boolean invertSvg) {
        this.mInvertSvg = invertSvg;
    }

    public void doInvertSvg() {
        this.mInvertSvg = true;
        showSVGImage();
    }

    public void doNotInvertSvg() {
        this.mInvertSvg = false;
        showSVGImage();
    }


    private void setOverrideColors(boolean overrideColors) {
        this.mOverrideColors = overrideColors;
        showSVGImage();
    }

    /**
     * call if color needs to be overridden by user defined color (defaults to black)
     */
    public void doOverrideColors() {
        this.mOverrideColors = true;
        showSVGImage();
    }

    public void doNotOverrideColors() {
        this.mOverrideColors = false;
        showSVGImage();
    }

    /**
     * set color of svg
     * @param svgColor
     */
    public void setSvgColor(int svgColor) {
        this.mSvgColor = svgColor;
        showSVGImage();
    }

    /**
     * set color of pressed state of svg
     * @param pressedSvgColor
     */
    public void setPressedSvgColor(int pressedSvgColor) {
        setIsPressable();
        this.mPressedSvgColor = pressedSvgColor;
        if (mPressedSvgColor == mSvgColor) setIsNotPressable();
        showSVGImage();
    }

    private void setPressableSvg(boolean isPressable) {
        this.mIsPressable = isPressable;
    }

    public void setIsPressable() {
        this.mIsPressable = true;
        showSVGImage();
    }

    public void setIsNotPressable() {
        this.mIsPressable = false;
        showSVGImage();
    }

    public void setSVGResource(int resourceId) {
        this.mSvgResourceId = resourceId;
        showSVGImage();
    }

    private void showSVGImage() {
        if (mSvgResourceId == 0) {
            setImageResource(0);
        } else {
            if (mIsPressable) {
                showPressableSvgImage();
            } else {
                if (mInvertSvg || mOverrideColors) {
                    setImageBitmap(getImageBitmap());
                } else {
                    SVG svg = getSVGImage(mSvgResourceId);
                    setImageDrawable(new PictureDrawable(svg.renderToPicture()));
                }
            }
        }
    }

    private SVG getSVGImage(int svgResourceId) {
        SVG svg = sSVGCache.get(svgResourceId);
        if (svg == null) {
            try {
                svg = SVG.getFromResource(getContext(), svgResourceId);
            } catch (SVGParseException e) {
                Logger.log(e);
            }
            sSVGCache.put(svgResourceId, svg);
        }
        return svg;
    }

    private Bitmap convertImageColor(Drawable drawable, int invertColor) {
        return convertImageColor(ImageUtils.drawableToBmp(drawable), invertColor);
    }

    private Bitmap convertImageColor(Bitmap image, int invertColor) {

        int length = image.getWidth() * image.getHeight();
        int[] array = new int[length];
        image.getPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        for (int i = 0; i < length; i++) {
        /* If the bitmap is in ARGB_8888 format */
            if (array[i] != Color.TRANSPARENT) {
                array[i] = invertColor;
            } else {
                array[i] = Color.TRANSPARENT;
            }
        }

        image.setPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        return image;
    }

    private Bitmap invertImage(Drawable drawable, int invertColor) {
        return invertImage(ImageUtils.drawableToBmp(drawable), invertColor);
    }

    private Bitmap invertImage(Bitmap image, int invertColor) {

        int length = image.getWidth() * image.getHeight();
        int[] array = new int[length];
        image.getPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        for (int i = 0; i < length; i++) {
        /* If the bitmap is in ARGB_8888 format */
            if (array[i] == Color.TRANSPARENT) {
                array[i] = invertColor;
            } else {
                array[i] = Color.TRANSPARENT;
            }
        }

        image.setPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        return image;
    }

    private void showPressableSvgImage() {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new BitmapDrawable(getResources(), getPressedImageBitmap()));
        states.addState(new int[]{}, new BitmapDrawable(getResources(), getImageBitmap()));
        setImageDrawable(states);
    }

    private Bitmap getImageBitmap() {
        Bitmap image;

        image = BitmapCache.getBitmapFromCache(getSvgCacheTag());

        if(image == null) {
            SVG svg = getSVGImage(mSvgResourceId);
            PictureDrawable pictureDrawable = new PictureDrawable(svg.renderToPicture());
            if(mInvertSvg) {
                Bitmap invertedBitmap = invertImage(pictureDrawable, mSvgColor);
                image = convertImageColor(invertedBitmap, mSvgColor);
            } else {
                image = convertImageColor(pictureDrawable, mSvgColor);
            }
            BitmapCache.addBitmapToCache(getSvgCacheTag(), image);
        }

        return image;
    }

    private Bitmap getPressedImageBitmap() {
        Bitmap image;

        image = BitmapCache.getBitmapFromCache(getPressedSvgCacheTag());

        if(image == null) {
            SVG svg = getSVGImage(mSvgResourceId);
            PictureDrawable pictureDrawable = new PictureDrawable(svg.renderToPicture());
            if(mInvertSvg) {
                Bitmap invertedBitmap = invertImage(pictureDrawable, mPressedSvgColor);
                image = convertImageColor(invertedBitmap, mPressedSvgColor);
            } else {
                image = convertImageColor(pictureDrawable, mPressedSvgColor);
            }
            BitmapCache.addBitmapToCache(getPressedSvgCacheTag(), image);
        }

        return image;
    }

    private String getSvgCacheTag() {
        return mSvgResourceId + getWidth() + "," + getHeight() +String.valueOf(mSvgColor) + String.valueOf(mInvertSvg) + String.valueOf(mOverrideColors);
    }

    private String getPressedSvgCacheTag() {
        return mSvgResourceId + getWidth() + "," + getHeight() + String.valueOf(mPressedSvgColor) + String.valueOf(mInvertSvg) + String.valueOf(mOverrideColors);
    }

}
