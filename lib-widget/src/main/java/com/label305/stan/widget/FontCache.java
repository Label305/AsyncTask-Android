package com.label305.stan.widget;

import android.content.Context;
import android.graphics.Typeface;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FontCache {

    private static final Map<String, Typeface> FONT_CACHE = new HashMap<>();

    private FontCache() {
    }

    @NotNull
    public static Typeface getFont(@NotNull final Context context, @NotNull final String font) {
        Typeface typeface = FONT_CACHE.get(font);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), font);
            FONT_CACHE.put(font, typeface);
        }
        return typeface;
    }
}
