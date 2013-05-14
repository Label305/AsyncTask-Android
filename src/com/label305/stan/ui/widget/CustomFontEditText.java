package com.label305.stan.ui.widget;

import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.label305.stan.ui.widget.CustomFontHelper.CustomFontInterface;

public class CustomFontEditText extends EditText implements CustomFontInterface {

	private boolean mShouldCapitalize;
	private boolean mShouldLowercase;

	public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CustomFontHelper.init(this, attrs);
	}

	public CustomFontEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFontHelper.init(this, attrs);
	}

	public CustomFontEditText(Context context, String fontPath, boolean shouldCapitalize, boolean shouldLowercase) {
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
}
