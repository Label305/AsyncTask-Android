package com.label305.stan.ui.widget;

import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.label305.stan.R;

public class CustomFontTextView extends TextView {

	private boolean mShouldCapitalize;
	private boolean mShouldLowercase;

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public CustomFontTextView(Context context, String fontPath, boolean shouldCapitalize, boolean shouldLowercase) {
		super(context);

		setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontPath));
		mShouldCapitalize = shouldCapitalize;
		mShouldLowercase = shouldLowercase;

		if (shouldCapitalize && shouldLowercase)
			throw new IllegalArgumentException("Cannot both capitalize and lowercase text!");
	}

	private void init(AttributeSet attrs) {
		if (!isInEditMode()) {
			setFont(attrs);
			setCapitalize(attrs);
			setLowercase(attrs);
			setText(attrs);
			setIncludeFontPadding(false);
		}
	}

	private void setFont(AttributeSet attrs) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
		setTypeface(Typeface.createFromAsset(getContext().getAssets(), a.getString(R.styleable.CustomFontTextView_font)));
		a.recycle();
	}

	private void setCapitalize(AttributeSet attrs) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
		mShouldCapitalize = a.getBoolean(R.styleable.CustomFontTextView_capitalize, false);
		a.recycle();
	}

	private void setLowercase(AttributeSet attrs) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
		mShouldCapitalize = a.getBoolean(R.styleable.CustomFontTextView_lowercase, false);
		a.recycle();
	}

	private void setText(AttributeSet attrs) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
		CharSequence text = a.getText(R.styleable.CustomFontTextView_text);
		if (text != null)
			setText(text.toString());
		a.recycle();
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
