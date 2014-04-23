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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.label305.stan.R;
import com.label305.stan.memorymanagement.BitmapCache;
import com.label305.stan.memorymanagement.SvgCache;
import com.label305.stan.utils.ImageUtils;
import com.label305.stan.utils.Logger;

/**
 * Created by Label305 on 02/04/2014.
 */
public class SvgImageView extends ImageView {

    private int mSvgResourceId;

    private boolean mInvertSvg;
    private boolean mIsPressable;

    private int mSvgColor = Color.BLACK;
    private int mPressedSvgColor = Color.WHITE;

    private boolean mCustomColorSet;

    public SvgImageView(final Context context) {
        super(context);
    }

    public SvgImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SvgImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    private void init(final AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SvgImageView, 0, 0);
        mInvertSvg = a.getBoolean(R.styleable.SvgImageView_invertSvg, false);
        mIsPressable = a.getBoolean(R.styleable.SvgImageView_isPressable, false);
        mSvgColor = a.getColor(R.styleable.SvgImageView_svgColor, Color.BLACK);

        if (mIsPressable) {
            mPressedSvgColor = a.getColor(R.styleable.SvgImageView_pressedSvgColor, Color.WHITE);
        }
        mSvgResourceId = a.getResourceId(R.styleable.SvgImageView_svg, 0);

        showSvgImage();
        a.recycle();
    }

    /**
     * Inverts the Svg color.
     */
    public void doInvertSvg() {
        mInvertSvg = true;
        showSvgImage();
    }

    /**
     * Restores invertation of the Svg color.
     */
    public void doNotInvertSvg() {
        mInvertSvg = false;
        showSvgImage();
    }

    /**
     * Sets the color of the Svg.
     */
    public void setSvgColor(final int svgColor) {
        mSvgColor = svgColor;
        mCustomColorSet = true;
        showSvgImage();
    }

    /**
     * Sets the color of the Svg to the default color.
     */
    public void useDefaultColor() {
        mCustomColorSet = false;
        showSvgImage();
    }

    /**
     * Sets the color of the pressed state of the Svg.
     */
    public void setPressedSvgColor(final int pressedSvgColor) {
        setIsPressable();
        mPressedSvgColor = pressedSvgColor;
        if (mPressedSvgColor == mSvgColor) {
            setIsNotPressable();
        }
        showSvgImage();
    }

    /**
     * Adds a pressable state. Defaults to invertation of the Svg. Use {@link #setPressedSvgColor(int)} to provide a custom color.
     */
    public void setIsPressable() {
        mIsPressable = true;
        showSvgImage();
    }

    /**
     * Removes any added pressable states.
     */
    public void setIsNotPressable() {
        mIsPressable = false;
        showSvgImage();
    }

    /**
     * Sets the resource of the Svg to use.
     * @param resourceId the resource of the Svg, of the form R.raw.my_svg.
     */
    public void setSvgResource(final int resourceId) {
        mSvgResourceId = resourceId;
        showSvgImage();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setSoftwareLayerType() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void showSvgImage() {
        if (mSvgResourceId == 0) {
            setImageResource(0);
        } else {
            setSoftwareLayerType();
            if (mIsPressable) {
                showPressableSvgImage();
            } else {
                if (mInvertSvg || mCustomColorSet) {
                    setImageBitmap(getImageBitmap());
                } else {
                    SVG svg = getSvgImage(mSvgResourceId);
                    setImageDrawable(new PictureDrawable(svg.renderToPicture()));
                }
            }
        }
    }

    private SVG getSvgImage(final int svgResourceId) {
        SVG svg = SvgCache.getSvgFromCache(svgResourceId);
        if (svg == null) {
            try {
                svg = SVG.getFromResource(getContext(), svgResourceId);
            } catch (SVGParseException e) {
                Logger.log(e);
            }
            SvgCache.addSvgToCache(svgResourceId, svg);
        }
        return svg;
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

        if (image == null) {
            SVG svg = getSvgImage(mSvgResourceId);
            PictureDrawable pictureDrawable = new PictureDrawable(svg.renderToPicture());
            if (mInvertSvg) {
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

        if (image == null) {
            SVG svg = getSvgImage(mSvgResourceId);
            PictureDrawable pictureDrawable = new PictureDrawable(svg.renderToPicture());
            if (mInvertSvg) {
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
        return mSvgResourceId + getWidth() + "," + getHeight() + String.valueOf(mSvgColor) + String.valueOf(mInvertSvg) + String.valueOf(mCustomColorSet);
    }

    private String getPressedSvgCacheTag() {
        return mSvgResourceId + getWidth() + "," + getHeight() + String.valueOf(mPressedSvgColor) + String.valueOf(mInvertSvg) + String.valueOf(mCustomColorSet);
    }

    private static Bitmap convertImageColor(final Drawable drawable, final int invertColor) {
        return convertImageColor(ImageUtils.drawableToBmp(drawable), invertColor);
    }

    private static Bitmap convertImageColor(final Bitmap image, final int invertColor) {
        int length = image.getWidth() * image.getHeight();
        int[] array = new int[length];
        image.getPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        for (int i = 0; i < length; i++) {
        /* If the bitmap is in ARGB_8888 format */
            if (array[i] != Color.TRANSPARENT) {
                array[i] = invertColor;
            }
        }

        image.setPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        return image;
    }

    private static Bitmap invertImage(final Drawable drawable, final int invertColor) {
        return invertImage(ImageUtils.drawableToBmp(drawable), invertColor);
    }

    private static Bitmap invertImage(final Bitmap image, final int invertColor) {
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
}
