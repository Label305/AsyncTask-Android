package com.label305.stan.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.label305.stan.R;

public class SimpleImageView extends AbstractImageHolder {

	private ImageView mImageView;

	private ProgressBar mProgressBar;

	public SimpleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SimpleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SimpleImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			LayoutInflater.from(getContext()).inflate(R.layout.simpleimageholder, this, true);
			mImageView = (ImageView) findViewById(R.id.stan_simpleimageholder_imageview);
			mProgressBar = (ProgressBar) findViewById(R.id.stan_simpleimageholder_progressbar);
		}
	}

	@Override
	public ImageView getImageView() {
		return mImageView;
	}

	@Override
	public ProgressBar getProgressBar() {
		return mProgressBar;
	}

	public void clearImage() {
		mImageView.setImageBitmap(null);
		mImageView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
	}
}
