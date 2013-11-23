package com.label305.stan.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.label305.stan.asyncutils.SafeAsyncTask;

public class GCMUtils {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	static GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCMUtils";

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	public static boolean checkPlayServices(Activity activity) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				activity.finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private static String getRegistrationId(Activity activity) {
		final SharedPreferences prefs = getGCMPreferences(activity);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(activity);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getGCMPreferences(Activity activity) {
		return activity.getSharedPreferences(activity.getClass().getSimpleName(), Context.MODE_PRIVATE);
	}

	public static SafeAsyncTask<?> asyncRequestRegister(final Activity activity, final String senderId,
			final OnDeviceRegisteredListener listener) {
		return new SafeAsyncTask<String>() {
			@Override
			public String call() throws Exception {
				String regId = getRegistrationId(activity);
				if (regId.isEmpty()) {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(activity);
					}
					regId = gcm.register(senderId);
					storeRegistrationId(activity, regId);
				}
				return regId;
			}

			@Override
			protected void onSuccess(String result) throws Exception {
				listener.onDeviceRegistered(result);
			}

			@Override
			protected void onException(Exception e) throws RuntimeException {
				listener.onException(e);
			}
		}.execute();
	}

	public static SafeAsyncTask<?> asyncRequestUnRegister(final Activity activity,
			final OnDeviceRegisteredListener listener) {
		return new SafeAsyncTask<Void>() {
			@Override
			public Void call() throws Exception {
				String regId = getRegistrationId(activity);
				if (regId.isEmpty()) {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(activity);
					}
					gcm.unregister();
					storeRegistrationId(activity, "");
				}
				return null;
			}

			@Override
			protected void onSuccess(Void result) throws Exception {
				listener.onDeviceUnRegistered();
			}

			@Override
			protected void onException(Exception e) throws RuntimeException {
				listener.onException(e);
			}
		}.execute();
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private static void storeRegistrationId(Activity activity, String regId) {
		final SharedPreferences prefs = getGCMPreferences(activity);
		int appVersion = getAppVersion(activity);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	public interface OnDeviceRegisteredListener {
		public void onDeviceRegistered(String gcmToken);

		public void onDeviceUnRegistered();

		public void onException(Exception e);
	}
}
