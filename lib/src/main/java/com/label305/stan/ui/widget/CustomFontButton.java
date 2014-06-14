package com.label305.stan.ui.widget;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.label305.stan.ui.widget.CustomFontHelper.CustomFontInterface;

public class CustomFontButton extends Button implements CustomFontInterface {

	private boolean mShouldCapitalize;
	private boolean mShouldLowercase;

	public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CustomFontHelper.init(this, attrs);
	}

	public CustomFontButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFontHelper.init(this, attrs);
	}

	public CustomFontButton(Context context, String fontPath, boolean shouldCapitalize, boolean shouldLowercase) {
		super(context);

		CustomFontHelper.init(this, fontPath, shouldCapitalize, shouldLowercase);
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
