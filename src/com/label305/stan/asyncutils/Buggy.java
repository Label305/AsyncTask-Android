package com.label305.stan.asyncutils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.label305.stan.utils.HttpHelper;
import com.label305.stan.utils.Logger;

/**
 * A class to report unexpected errors to Buggy. To use this class, one must
 * first initialize Buggy using Buggy.init(Context).
 */
public class Buggy {

	private static final String PLATFORM = "Android";
	private static final String URL = "http://buggy.label305.com/dump";
	private static final String PARAM_PLATFORM = "platform";
	private static final String PARAM_PACKAGE = "package";
	private static final String PARAM_VERSION = "version";
	private static final String PARAM_ERRORTYPE = "error_type";
	private static final String PARAM_CONTENT = "content";
	private static final String PARAM_LABEL = "label";
	private static final String PARAM_PLATFORMVERSION = "os_version";
	private static Context sContext;

	public static void init(Context context) {
		sContext = context;
	}

	public static void report(final Throwable e, final String label) {
		report(stackTraceToString(e), e.getClass().getName(), label);
	}

	public static void report(final String stackTrace, final String errorType, final String label) {
		if (sContext == null) {
			Logger.log(null, "Not initialized!");
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				HashMap<String, Object> data = new HashMap<String, Object>();

				data.put(PARAM_PLATFORM, PLATFORM);
				data.put(PARAM_PACKAGE, sContext.getPackageName());
				data.put(PARAM_VERSION, getVersionName(sContext));
				data.put(PARAM_ERRORTYPE, errorType);
				data.put(PARAM_CONTENT, stackTrace);
				data.put(PARAM_LABEL, label);
				data.put(PARAM_PLATFORMVERSION, Build.VERSION.RELEASE);

				try {
					new HttpHelper().post(URL, new HashMap<String, Object>(), data);
				} catch (IOException e) {
					Logger.log(sContext, e);
				}
			}
		}).start();
	}

	private static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Logger.log(context, e);
			return "?";
		}
	}

	private static String stackTraceToString(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
