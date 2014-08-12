package com.label305.stan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CheckBox;

import org.jetbrains.annotations.NotNull;

public class CustomFontCheckBox extends CheckBox {

    public CustomFontCheckBox(@NotNull final Context context, @NotNull final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomFontCheckBox(@NotNull final Context context, @NotNull final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@NotNull final AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontCheckBox);

        String font = a.getString(R.styleable.CustomFontCheckBox_font);
        if (font != null) {
            setTypeface(FontCache.getFont(getContext(), font));
        }

        a.recycle();
    }
}
