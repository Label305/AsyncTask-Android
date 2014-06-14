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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
        mSvgColor = a.getColor(R.styleable.SvgImageView_svgColor, Color.BLACK);
        mCustomColorSet = a.hasValue(R.styleable.SvgImageView_svgColor);
        mPressedSvgColor = a.getColor(R.styleable.SvgImageView_pressedSvgColor, Color.WHITE);
        mIsPressable = a.hasValue(R.styleable.SvgImageView_pressedSvgColor);
        mSvgResourceId = a.getResourceId(R.styleable.SvgImageView_svg, 0);

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
     *
     * @param resourceId the resource of the Svg, of the form R.raw.my_svg.
     */
    public void setSvgResource(final int resourceId) {
        mSvgResourceId = resourceId;
        showSvgImage();
    }

    @TargetApi(11)
    private void setSoftwareLayerType() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void showSvgImage() {
        showSvgImage(getWidth(), getHeight());
    }

    private void showSvgImage(final int width, final int height) {
        if (mSvgResourceId == 0) {
            setImageResource(0);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setSoftwareLayerType();
            }
            if (mIsPressable) {
                showPressableSvgImage(width, height);
            } else {
                if (mInvertSvg || mCustomColorSet) {
                    setImageBitmap(getImageBitmap(width, height));
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
            SvgCache.asyncCache(getContext(), svgResourceId);
        }
        return svg;
    }

    private void showPressableSvgImage(final int width, final int height) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new BitmapDrawable(getResources(), getPressedImageBitmap(width, height)));
        states.addState(new int[]{}, new BitmapDrawable(getResources(), getImageBitmap(width, height)));
        setImageDrawable(states);
    }

    private Bitmap getImageBitmap(final int width, final int height) {

        Bitmap image = BitmapCache.getBitmapFromCache(getSvgCacheTag());

        if (image == null) {
            SVG svg = getSvgImage(mSvgResourceId);

            image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            // Render our document onto our canvas
            svg.renderToCanvas(canvas);
            if (mInvertSvg) {
                Bitmap invertedBitmap = invertImage(image, mSvgColor);
                image = convertImageColor(invertedBitmap, mSvgColor);
            } else {
                image = convertImageColor(image, mSvgColor);
            }
            BitmapCache.addBitmapToCache(getSvgCacheTag(), image);
        }

        return image;
    }

    private Bitmap getPressedImageBitmap(final int width, final int height) {

        Bitmap image = BitmapCache.getBitmapFromCache(getPressedSvgCacheTag());

        if (image == null) {
            SVG svg = getSvgImage(mSvgResourceId);

            image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            // Render our document onto our canvas
            svg.renderToCanvas(canvas);
            if (mInvertSvg) {
                Bitmap invertedBitmap = invertImage(image, mPressedSvgColor);
                image = convertImageColor(invertedBitmap, mPressedSvgColor);
            } else {
                image = convertImageColor(image, mPressedSvgColor);
            }
            BitmapCache.addBitmapToCache(getPressedSvgCacheTag(), image);
        }

        return image;
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        showSvgImage(right - left, bottom - top);
    }

    private String getSvgCacheTag() {
        return mSvgResourceId + getWidth() + "," + getHeight() + String.valueOf(mSvgColor) + mInvertSvg + mCustomColorSet;
    }

    private String getPressedSvgCacheTag() {
        return mSvgResourceId + getWidth() + "," + getHeight() + String.valueOf(mPressedSvgColor) + mInvertSvg + mCustomColorSet;
    }

    private static Bitmap convertImageColor(final Bitmap image, final int invertColor) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                /* If the bitmap is in ARGB_8888 format */
                if (image.getPixel(x, y) != Color.TRANSPARENT) {
                    image.setPixel(x, y, invertColor);
                }
            }
        }
        return image;
    }

    private static Bitmap invertImage(final Bitmap image, final int invertColor) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                /* If the bitmap is in ARGB_8888 format */
                if (image.getPixel(x, y) == Color.TRANSPARENT) {
                    image.setPixel(x, y, invertColor);
                } else {
                    image.setPixel(x, y, Color.TRANSPARENT);
                }
            }
        }

        return image;
    }
}
