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
		init(context, attrs);
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CustomFontTextView(Context context, String fontPath, boolean shouldCapitalize, boolean shouldLowercase) {
		super(context);

		setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
		mShouldCapitalize = shouldCapitalize;
		mShouldLowercase = shouldLowercase;

		if (shouldCapitalize && shouldLowercase)
			throw new IllegalArgumentException("Cannot both capitalize and lowercase text!");
	}

	private void init(Context context, AttributeSet attrs) {
		if (!isInEditMode()) {
			setFont(context, attrs);
			setCapitalize(context, attrs);
			setLowercase(context, attrs);
			setText(context, attrs);
			setIncludeFontPadding(false);
		}
	}

	private void setFont(Context context, AttributeSet attrs) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
		setTypeface(Typeface.createFromAsset(context.getAssets(), a.getString(R.styleable.CustomFontTextView_font)));
		a.recycle();
	}

	private void setCapitalize(Context context, AttributeSet attrs) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
		mShouldCapitalize = a.getBoolean(R.styleable.CustomFontTextView_capitalize, false);
		a.recycle();
	}

	private void setLowercase(Context context, AttributeSet attrs) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
		mShouldCapitalize = a.getBoolean(R.styleable.CustomFontTextView_lowercase, false);
		a.recycle();
	}

	private void setText(Context context, AttributeSet attrs) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
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
