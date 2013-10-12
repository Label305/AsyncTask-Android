package com.label305.stan.ui.widget;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.label305.stan.memorymanagement.BitmapCache;

/**
 * A {@link NetworkImageView} which handles the ImageLoader, and properly
 * handles image resource ids.
 */
public class SimpleNetworkImageView extends NetworkImageView {

	private int mDefaultImageResId;
	private int mErrorImageResId;
	private int mResId;

	public SimpleNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SimpleNetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleNetworkImageView(Context context) {
		super(context);
	}

	@Override
	public void setDefaultImageResId(int defaultImage) {
		super.setDefaultImageResId(defaultImage);
		mDefaultImageResId = defaultImage;
	}

	@Override
	public void setErrorImageResId(int errorImage) {
		super.setErrorImageResId(errorImage);
		mErrorImageResId = errorImage;
	}

	public void showDefaultImage() {
		mResId = -1;
		super.setImageResource(mDefaultImageResId);
	}

	public void showErrorImage() {
		mResId = -1;
		super.setImageResource(mErrorImageResId);
	}

	public int getmResId() {
		return mResId;
	}

	/**
	 * Sets URL of the image that should be loaded into this view. Note that
	 * calling this will immediately either set the cached image (if available)
	 * or the default image specified by
	 * {@link NetworkImageView#setDefaultImageResId(int)} on the view. </p>
	 * NOTE: If applicable, {@link NetworkImageView#setDefaultImageResId(int)}
	 * and {@link NetworkImageView#setErrorImageResId(int)} should be called
	 * prior to calling this function.
	 * 
	 * @param url
	 *            The URL that should be loaded into this ImageView.
	 */
	public void setImageUrl(String url) {
		if (url != null) {
			mResId = -1;
		}
		super.setImageUrl(url, NetworkImageHelper.getImageLoader(getContext().getApplicationContext()));
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (mResId <= 0 || mResId == mDefaultImageResId || mResId == mErrorImageResId) {
			if (bm == null) {
				showDefaultImage();
			} else {
				super.setImageBitmap(bm);
			}
		}
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mResId = resId;
	}

	private static class NetworkImageHelper {

		private static ImageLoader sImageLoader;

		public static ImageLoader getImageLoader(Context context) {
			if (!(context instanceof Application)) {
				throw new IllegalArgumentException("Pass a reference of ApplicationContext!");
			}

			if (sImageLoader == null) {
				sImageLoader = new ImageLoader(Volley.newRequestQueue(context), new BitmapCache());
			}
			return sImageLoader;
		}
	}

}
