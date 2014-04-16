package com.label305.stan.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Label305 on 08/02/14.
 */
public class ImageUtils {

    public static Bitmap drawableToBmp(Drawable drawable) {
        Bitmap returnBmp;
        if (drawable instanceof BitmapDrawable) {
            returnBmp =  ((BitmapDrawable)drawable).getBitmap();
        } else {
            returnBmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnBmp);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return returnBmp;
    }
}
