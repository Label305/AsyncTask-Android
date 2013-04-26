package com.label305.stan.ui.widget;

import com.label305.stan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SimpleImageView extends AbstractImageHolder {

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
	}

	@Override
	protected ImageView getImageView() {
		return (ImageView) findViewById(R.id.stan_simpleimageholder_imageview);
	}

	@Override
	protected ProgressBar getProgressBar() {
		return (ProgressBar) findViewById(R.id.stan_simpleimageholder_progressbar);
	}

}
