package com.label305.stan.ui.widget;

import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.label305.stan.ui.widget.CustomFontHelper.CustomFontInterface;
import com.label305.stan.utils.Logger;
import com.label305.stan.utils.StringUtils;

public class CustomFontTextView extends TextView implements CustomFontInterface {

	private boolean mShouldCapitalize;
	private boolean mShouldLowercase;

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CustomFontHelper.init(this, attrs);
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFontHelper.init(this, attrs);
	}

	public CustomFontTextView(Context context, String fontPath, boolean shouldCapitalize, boolean shouldLowercase) {
		super(context);

		setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontPath));
		mShouldCapitalize = shouldCapitalize;
		mShouldLowercase = shouldLowercase;

		if (shouldCapitalize && shouldLowercase)
			throw new IllegalArgumentException("Cannot both capitalize and lowercase text!");
	}

	public void setShouldCapitalize(boolean mShouldCapitalize) {
		this.mShouldCapitalize = mShouldCapitalize;
	}

	public void setShouldLowercase(boolean mShouldLowercase) {
		this.mShouldLowercase = mShouldLowercase;
	}

	public void setFont(String font) {
		if (!StringUtils.isNullOrEmpty(font)) {
			setTypeface(Typeface.createFromAsset(getContext().getAssets(), font));
		} else {
			Logger.log(getContext(), "Invalid font: " + font);
		}
	}

	public void setText(String text) {
		if (text == null) {
			super.setText(null);
		} else if (mShouldCapitalize) {
			super.setText(text.toUpperCase(Locale.getDefault()));
		} else if (mShouldLowercase) {
			super.setText(text.toLowerCase(Locale.getDefault()));
		} else {
			super.setText(text);
		}
	}

	public boolean shouldCapitalize() {
		return mShouldCapitalize;
	}

	public boolean shouldLowercase() {
		return mShouldLowercase;
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
