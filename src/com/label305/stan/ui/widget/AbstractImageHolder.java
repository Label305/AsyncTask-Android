package com.label305.stan.ui.widget;

import com.label305.stan.asyncutils.ImageLoader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public abstract class AbstractImageHolder extends LinearLayout {

	public AbstractImageHolder(Context context) {
		super(context);
	}

	public AbstractImageHolder(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected abstract ImageView getImageView();

	protected abstract ProgressBar getProgressBar();

	public void setImageUrl(String uri) {
		ImageLoader.loadImage(uri, getProgressBar(), getImageView());
	}
}
