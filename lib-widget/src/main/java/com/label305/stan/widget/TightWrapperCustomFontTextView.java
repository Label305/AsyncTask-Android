package com.label305.stan.widget;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

/**
 * A {@link CustomFontTextView} that tightly wraps text when displaying text on multiple lines.
 */
public class TightWrapperCustomFontTextView extends CustomFontTextView {


    public TightWrapperCustomFontTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TightWrapperCustomFontTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Layout layout = getLayout();
        if (layout != null) {
            int width = (int) Math.ceil(getMaxLineWidth(layout)) + getCompoundPaddingLeft() + getCompoundPaddingRight();
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        }
    }

    private static float getMaxLineWidth(final Layout layout) {
        float maxWidth = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > maxWidth) {
                maxWidth = layout.getLineWidth(i);
            }
        }
        return maxWidth;
    }
}
