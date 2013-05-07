package com.label305.stan.ui.widget;

import com.label305.stan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SimpleImageView extends AbstractImageHolder {

	private ImageView mImageView;
	private ProgressBar mProgressBar;

	public SimpleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SimpleImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.simpleimageholder, this, true);
		mImageView = (ImageView) findViewById(R.id.stan_simpleimageholder_imageview);
		mProgressBar = (ProgressBar) findViewById(R.id.stan_simpleimageholder_progressbar);
	}

	@Override
	protected ImageView getImageView() {
		return mImageView;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

}
