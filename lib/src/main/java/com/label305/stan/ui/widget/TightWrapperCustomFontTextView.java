package com.label305.stan.ui.widget;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

/**
 * A {@link CustomFontTextView} that tightly wraps text when displaying text on multiple lines.
 */
public class TightWrapperCustomFontTextView extends CustomFontTextView {

    public TightWrapperCustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TightWrapperCustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TightWrapperCustomFontTextView(Context context, String fontPath, boolean shouldCapitalize, boolean shouldLowercase) {
        super(context, fontPath, shouldCapitalize, shouldLowercase);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Layout layout = getLayout();
        if (layout != null) {
            int width = (int) Math.ceil(getMaxLineWidth(layout)) + getCompoundPaddingLeft() + getCompoundPaddingRight();
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        }
    }

    private float getMaxLineWidth(Layout layout) {
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }
}
