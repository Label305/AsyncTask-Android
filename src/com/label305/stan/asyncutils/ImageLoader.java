package com.label305.stan.asyncutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.label305.stan.utils.VersionUtils;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * An implementation of the Universal Image Loader which handles showing of a
 * ProgressDialog. Also uses AsyncTask.THREAD_POOL_EXECUTOR to load images, and
 * saves the images to memory and disc cache.
 * 
 * Usage: Call init(Context, int) to initialize the ImageLoader, call
 * loadImage(String, ProgressBar, ImageView) to load images.
 */
public class ImageLoader {

	private static int sUnavailableImageResource;

	/**
	 * Initialize the ImageLoader. Must be called at the very start of the
	 * application.
	 * 
	 * @param unavailableImageResource
	 *            a resource of an image that will be shown when there is
	 *            nothing to be shown (yet).
	 */
	@SuppressLint("NewApi")
	public static void init(Context context, int unavailableImageResource) {
		sUnavailableImageResource = unavailableImageResource;

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
		displayImageOptionsBuilder.cacheInMemory();
		displayImageOptionsBuilder.cacheOnDisc();
		displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);
		displayImageOptionsBuilder.displayer(new FadeInBitmapDisplayer(500));

		Builder builder = new ImageLoaderConfiguration.Builder(context);
		builder.memoryCache(new LruMemoryCache(cacheSize));
		builder.memoryCacheSize(cacheSize);

		if (VersionUtils.isV11OrHigher()) {
			builder.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			builder.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		builder.threadPoolSize(1);

		builder.defaultDisplayImageOptions(displayImageOptionsBuilder.build());

		ImageLoaderConfiguration config = builder.build();
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
	}

	public static void loadImage(String uri, ProgressBar progressBar, ImageView imageView) {
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().cancelDisplayTask(imageView);
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(uri, imageView, new ImageLoadingListener(progressBar, imageView));
	}

	public static void loadImage(String uri, ImageLoadingListener imageLoadingListener) {
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().cancelDisplayTask(imageLoadingListener.getImageView());
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(uri, imageLoadingListener.getImageView(), imageLoadingListener);
	}

	public static void cancelDisplayTask(ImageView imageView) {
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().cancelDisplayTask(imageView);
	}

	public static class ImageLoadingListener implements com.nostra13.universalimageloader.core.assist.ImageLoadingListener {

		private ProgressBar mProgressBar;
		private ImageView mImageView;

		public ImageLoadingListener(ProgressBar progressBar, ImageView imageView) {
			mProgressBar = progressBar;
			mImageView = imageView;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			mImageView.setImageBitmap(null);
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			mImageView.setImageResource(sUnavailableImageResource);

			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
			if (bitmap != null) {
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.GONE);
				}
			} else {
				onLoadingFailed(imageUri, view, null);
			}
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.GONE);
			}
		}

		public ImageView getImageView() {
			return mImageView;
		}

	}
}
