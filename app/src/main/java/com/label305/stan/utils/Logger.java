package com.label305.stan.utils;

import com.crashlytics.android.Crashlytics;
import com.label305.stan.BuildConfig;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * A logger class which only logs when the app is in debug mode. Uses the
 * (modifiable) tag "StanLogger".
 */
public class Logger {
	public static String TAG = "StanLogger";

	public static void log(String msg) {
		if (isDebug()) {
			Log.v(TAG, msg);
			Crashlytics.log(msg);
		}
	}

	public static void log(int msg) {
		log(String.valueOf(msg));
	}

	public static void log(Exception e) {
		log(null, e);
	}

	public static void log(Context context, Object msg) {
		if (msg == null) {
			msg = "null";
		}

		if (msg.toString().trim().equals(""))
			msg = "No message";

		if (isDebug()) {
			Log.v(TAG, msg.toString());
			Crashlytics.log(msg.toString());

			if (context != null) {
				try {
					Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
				}
			}
		}
	}

	public static void log(Context context, Exception e) {
		if (isDebug()) {
			if (!(e instanceof InterruptedException)) {
				e.printStackTrace();

				if (context != null) {
					try {
						Toast.makeText(context, e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
					} catch (Exception ex) {
					}
				}
			} else {
				Log.w(TAG, e.getClass().getSimpleName());
			}
		}
	}

	private static boolean isDebug() {
		return BuildConfig.DEBUG;
	}
}
