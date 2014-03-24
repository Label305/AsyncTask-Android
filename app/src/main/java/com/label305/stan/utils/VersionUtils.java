package com.label305.stan.utils;

import android.os.Build;

public class VersionUtils {
	public static boolean isV11OrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}
}
