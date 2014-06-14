package com.label305.stan.utils;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Label305 on 07/06/2014.
 */
public class GooglePlayServices {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean isAvailable(final Activity activity) {
        if (Dependency.isPresent("com.google.android.gms.common.GooglePlayServicesUtil")) {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Logger.log(activity, "This device is not supported.");
                }
                return false;
            }
            return true;
        } else {
            throw new NoClassDefFoundError("Could not find the Google Play Services, make sure the Google Play services (com.google.android.gms:play-services:x.x.+) are imported in the build.gradle file");
        }
    }
}
