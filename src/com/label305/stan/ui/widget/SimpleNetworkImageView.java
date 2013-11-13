package com.label305.stan.ui.widget;

import android.app.Application;
import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.label305.stan.memorymanagement.BitmapCache;

/**
 * A {@link StanNetworkImageView} which handles the ImageLoader.
 */
public class SimpleNetworkImageView extends StanNetworkImageView {

	public SimpleNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SimpleNetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
 
	public SimpleNetworkImageView(Context context) {
		super(context);
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
		super.setImageUrl(url, NetworkImageHelper.getImageLoader(getContext().getApplicationContext()));
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
	}

	/**
	 * A helper class for managing an {@link ImageLoader} and a
	 * {@link BitmapCache}.
	 */
	private static class NetworkImageHelper {

		private static ImageLoader sImageLoader;

		public static ImageLoader getImageLoader(Context context) {
			if (!(context instanceof Application)) {
				throw new IllegalArgumentException("Pass a reference of ApplicationContext!");
			}

			if (sImageLoader == null) {
				BitmapCache.initialize(context);
				sImageLoader = new ImageLoader(Volley.newRequestQueue(context), new BitmapCache());
			}
			return sImageLoader;
		}
	}
}
