package com.label305.stan;

public class GCMUtils {

//    public static final String EXTRA_MESSAGE = "message";
//    public static final String PROPERTY_REG_ID = "registration_id";
//    private static final String PROPERTY_APP_VERSION = "appVersion";
//
//    private static GoogleCloudMessaging sGoogleCloudMessaging;
//
//    /**
//     * Gets the current registration ID for application on GCM service.
//     * <p/>
//     * If result is empty, the app needs to register.
//     *
//     * @return registration ID, or empty string if there is no existing
//     * registration ID.
//     */
//    public static String getRegistrationId(final Activity activity) {
//        final SharedPreferences prefs = getGCMPreferences(activity);
//        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
//        if (registrationId.isEmpty()) {
//            Logger.log(activity, "Registration not found.");
//            return "";
//        }
//
//        // Check if app was updated; if so, it must clear the registration ID
//        // since the existing regID is not guaranteed to work with the new
//        // app version.
//        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(activity);
//        if (registeredVersion != currentVersion) {
//            Logger.log(activity, "App version changed.");
//            return "";
//        }
//        return registrationId;
//    }
//
//    /**
//     * @return Application's version code from the {@code PackageManager}.
//     */
//    private static int getAppVersion(final Context context) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (NameNotFoundException e) {
//            /* should never happen */
//            throw new RuntimeException("Could not get package name: " + e);
//        }
//    }
//
//    /**
//     * @return Application's {@code SharedPreferences}.
//     */
//    private static SharedPreferences getGCMPreferences(final Activity activity) {
//        return activity.getSharedPreferences(activity.getClass().getSimpleName(), Context.MODE_PRIVATE);
//    }
//
//    private static GoogleCloudMessaging getGCMInstance(Activity activity) {
//        synchronized (GCMUtils.class) {
//            if (sGoogleCloudMessaging == null) {
//                sGoogleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
//            }
//            return sGoogleCloudMessaging;
//        }
//    }
//
//    public static ExponentialBackoffAsyncTask<?> asyncRequestRegister(final Activity activity, final String senderId,
//                                                                      final OnDeviceRegisteredListener listener) {
//        return new ExponentialBackoffAsyncTask<String>() {
//            @Override
//            public String call() throws Exception {
//                if (Dependency.isPresent("com.google.android.gms.gcm.GoogleCloudMessaging")) {
//                    String regId = getRegistrationId(activity);
//                    if (regId.isEmpty()) {
//
//                        regId = getGCMInstance(activity).register(senderId);
//                        storeRegistrationId(activity, regId);
//                    }
//                    return regId;
//                } else {
//                    throw new NoClassDefFoundError("Could not find the Google Play Services, make sure the Google Play services (com.google.android.gms:play-services:4.4.+) are imported in the build.gradle file");
//                }
//            }
//
//            @Override
//            protected void onSuccess(final String result) {
//                listener.onDeviceRegistered(result);
//            }
//
//            @Override
//            protected void onException(final Exception e) {
//                listener.onException(e);
//            }
//        }.execute();
//    }
//
//    public static ExponentialBackoffAsyncTask<?> asyncRequestUnRegister(final Activity activity,
//                                                                        final OnDeviceRegisteredListener listener) {
//        return new ExponentialBackoffAsyncTask<Void>() {
//            @Override
//            public Void call() throws Exception {
//                if (Dependency.isPresent("com.google.android.gms.gcm.GoogleCloudMessaging")) {
//                    String regId = getRegistrationId(activity);
//                    if (regId.isEmpty()) {
//                        getGCMInstance(activity).unregister();
//                        storeRegistrationId(activity, "");
//                    }
//                    return null;
//                } else {
//                    throw new NoClassDefFoundError("Could not find the Google Play Services, make sure the Google Play services (com.google.android.gms:play-services:4.4.+) are imported in the build.gradle file");
//                }
//            }
//
//            @Override
//            protected void onSuccess(final Void result) {
//                listener.onDeviceUnRegistered();
//            }
//
//            @Override
//            protected void onException(final Exception e) {
//                listener.onException(e);
//            }
//        }.execute();
//    }
//
//    /**
//     * Stores the registration ID and app versionCode in the application's
//     * {@code SharedPreferences}.
//     *
//     * @param activity application's context.
//     * @param regId    registration ID
//     */
//    private static void storeRegistrationId(final Activity activity, final String regId) {
//        final SharedPreferences prefs = getGCMPreferences(activity);
//        int appVersion = getAppVersion(activity);
//        Logger.log(activity, "Saving regId on app version " + appVersion);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(PROPERTY_REG_ID, regId);
//        editor.putInt(PROPERTY_APP_VERSION, appVersion);
//        editor.commit();
//    }
//
//    public interface OnDeviceRegisteredListener {
//        void onDeviceRegistered(String gcmToken);
//
//        void onDeviceUnRegistered();
//
//        void onException(Exception e);
//    }
}
