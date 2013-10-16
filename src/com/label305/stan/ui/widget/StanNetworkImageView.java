package com.label305.stan.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 * <i>This is a modified copy of the NetworkImageView class,
 * https://android.googlesource.com/platform/frameworks/volley/+/android-4.3
 * .1_r1/src/com/android/volley/toolbox/NetworkImageView.java</i> </p> Handles
 * fetching an image from a URL as well as the life-cycle of the associated
 * request.
 */
public class StanNetworkImageView extends ImageView {

	/** The URL of the network image to load */
	private String mUrl;

	/**
	 * Resource ID of the image to be used as a placeholder until the network
	 * image is loaded.
	 */
	private int mDefaultImageId;

	/**
	 * Resource ID of the image to be used if the network response fails.
	 */
	private int mErrorImageId;

	/** Local copy of the ImageLoader. */
	private ImageLoader mImageLoader;

	/** Current ImageContainer. (either in-flight or finished) */
	private ImageContainer mImageContainer;

	private ImageResponseListener mImageResponseListener;

	public StanNetworkImageView(Context context) {
		this(context, null);
	}

	public StanNetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StanNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mImageResponseListener = new DefaultImageResponseListener();
	}

	/**
	 * Sets URL of the image that should be loaded into this view. Note that
	 * calling this will immediately either set the cached image (if available)
	 * or the default image specified by
	 * {@link NetworkImageView#setDefaultImageResId(int)} on the view.
	 * 
	 * NOTE: If applicable, {@link NetworkImageView#setDefaultImageResId(int)}
	 * and {@link NetworkImageView#setErrorImageResId(int)} should be called
	 * prior to calling this function.
	 * 
	 * @param url
	 *            The URL that should be loaded into this ImageView.
	 * @param imageLoader
	 *            ImageLoader that will be used to make the request.
	 */
	public void setImageUrl(String url, ImageLoader imageLoader) {
		mUrl = url;
		mImageLoader = imageLoader;
		// The URL has potentially changed. See if we need to load it.
		loadImageIfNecessary(false);
	}

	/**
	 * Sets the default image resource ID to be used for this view until the
	 * attempt to load it completes.
	 */
	public void setDefaultImageResId(int defaultImage) {
		mDefaultImageId = defaultImage;
	}

	/**
	 * Returns the default image resource ID that is used for this view until
	 * the attempt to load it completes.
	 */
	public int getDefaultImageResId() {
		return mDefaultImageId;
	}

	/**
	 * Sets the error image resource ID to be used for this view in the event
	 * that the image requested fails to load.
	 */
	public void setErrorImageResId(int errorImage) {
		mErrorImageId = errorImage;
	}

	/**
	 * Returns the error image resource ID that is used for this view in the
	 * event that the image requested fails to load.
	 */
	public int getErrorImageResId() {
		return mErrorImageId;
	}

	/**
	 * Sets the {@link ImageResponseListener} that handles image responses.
	 * 
	 * @param listener
	 *            The ImageResponseListener, or null for the default.
	 */
	public void setImageResponseListener(ImageResponseListener listener) {
		if (listener != null) {
			mImageResponseListener = listener;
		} else {
			mImageResponseListener = new DefaultImageResponseListener();
		}
	}

	/**
	 * Loads the image for the view if it isn't already loaded.
	 * 
	 * @param isInLayoutPass
	 *            True if this was invoked from a layout pass, false otherwise.
	 */
	private void loadImageIfNecessary(final boolean isInLayoutPass) {
		int width = getWidth();
		int height = getHeight();

		boolean isFullyWrapContent = getLayoutParams().height == LayoutParams.WRAP_CONTENT && getLayoutParams().width == LayoutParams.WRAP_CONTENT;
		// if the view's bounds aren't known yet, and this is not a
		// wrap-content/wrap-content
		// view, hold off on loading the image.
		if (width == 0 && height == 0 && !isFullyWrapContent) {
			return;
		}

		// if the URL to be loaded in this view is empty, cancel any old
		// requests and clear the
		// currently loaded image.
		if (TextUtils.isEmpty(mUrl)) {
			if (mImageContainer != null) {
				mImageContainer.cancelRequest();
				mImageContainer = null;
			}
			setImageBitmap(null);
			return;
		}

		// if there was an old request in this view, check if it needs to be
		// canceled.
		if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
			if (mImageContainer.getRequestUrl().equals(mUrl)) {
				// if the request is from the same URL, return.
				return;
			} else {
				// if there is a pre-existing request, cancel it if it's
				// fetching a different URL.
				mImageContainer.cancelRequest();
				setImageBitmap(null);
			}
		}

		// The pre-existing content of this view didn't match the current URL.
		// Load the new image
		// from the network.
		ImageContainer newContainer = mImageLoader.get(mUrl, new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mImageResponseListener.onErrorResponse(error);
			}

			@Override
			public void onResponse(final ImageContainer response, boolean isImmediate) {
				// If this was an immediate response that was delivered inside
				// of a layout
				// pass do not set the image immediately as it will trigger a
				// requestLayout
				// inside of a layout. Instead, defer setting the image by
				// posting back to
				// the main thread.
				if (isImmediate && isInLayoutPass) {
					post(new Runnable() {
						@Override
						public void run() {
							onResponse(response, false);
						}
					});
					return;
				} else {
					mImageResponseListener.onResponse(response);
				}
			}
		});

		// update the ImageContainer to be the new bitmap container.
		mImageContainer = newContainer;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		loadImageIfNecessary(true);
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mImageContainer != null) {
			// If the view was bound to an image request, cancel it and clear
			// out the image from the view.
			mImageContainer.cancelRequest();
			setImageBitmap(null);
			// also clear out the container so we can reload the image if
			// necessary.
			mImageContainer = null;
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}

	private class DefaultImageResponseListener implements ImageResponseListener {

		@Override
		public void onResponse(ImageContainer response) {
			if (response.getBitmap() != null) {
				setImageBitmap(response.getBitmap());
			} else if (mDefaultImageId != 0) {
				setImageResource(mDefaultImageId);
			}
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			if (mErrorImageId != 0) {
				setImageResource(mErrorImageId);
			}
		}
	}

	public interface ImageResponseListener {
		public void onResponse(ImageContainer response);

		public void onErrorResponse(VolleyError error);
	}
}