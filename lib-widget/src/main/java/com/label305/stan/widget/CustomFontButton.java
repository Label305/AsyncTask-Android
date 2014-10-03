package com.label305.stan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

public class CustomFontButton extends Button {

    public CustomFontButton(@NotNull final Context context) {
        super(context);
    }

    public CustomFontButton(@NotNull final Context context, @NotNull final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomFontButton(@NotNull final Context context, @NotNull final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@NotNull final AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontButton);

        String font = a.getString(R.styleable.CustomFontButton_font);
        if (font != null) {
            setFont(font);
        }

        a.recycle();
    }

    public void setFont(@NotNull final String font) {
        setTypeface(FontCache.getFont(getContext(), font));
    }
}
