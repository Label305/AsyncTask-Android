/*
 * |   _            _          _ ____   ___  _____   |
 * |  | |          | |        | |___ \ / _ \| ____|  |
 * |  | |      __ _| |__   ___| | __) | |_| | |__    |
 * |  | |     / _` | '_ \ / _ \ ||__ <|     |___ \   |
 * |  | |____| (_| | |_) |  __/ |___) |     |___) |  |
 * |  |______|\__,_|_.__/ \___|_|____/ \___/|____/   |
 *
 * @author Niek Haarman <niek@label305.com>
 *
 * Copyright (c) 2013 Label305. All Right Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.label305.stan.utils;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.label305.stan.R;

@SuppressWarnings({"UnusedDeclaration", "StaticMethodOnlyUsedInOneClass"})
/**
 * A utility class for logging to Google Analytics.
 * To use this class, add the following to your strings.xml:
 *
 * {@code
 *         <string name="key_analytics">MY_ANALYTICS_KEY</string>
 *         <string name="key_analytics_debug">MY_DEBUG_ANALYTICS_KEY</string> <!-- optional -->
 * }
 *
 * Also, call {@link #init(android.content.Context, boolean)}.
 */
public class Analytics {

    private static final String ANALYTICS = "Analytics: ";
    private static final String START = "start";
    private static final char COMMA = ',';

    private static boolean mIsDebug;
    private static Tracker mTracker;

    private Analytics() {
    }

    @SuppressWarnings("BooleanParameter")
    /* Suppress the boolean parameter warning to allow easy calling with BuildConfig.DEBUG. */
    /**
     * Initialize the Analytics Tracker. Resolves the key based on isDebug.
     */
    public static void init(final Context context, final boolean isDebug) {
        mIsDebug = isDebug;
        Logger.setIsDebug(isDebug);
    }

    /**
     * Set the app version to send to Google Analytics.
     */
    public static void setAppVersion(final Context context, final String appVersion) {
        getDefaultTracker(context).setAppVersion(appVersion);
    }

    /**
     * Manually start the session.
     * @deprecated does nothing anymore since analytics v4 is supported
     */
    @Deprecated
    public static void startSession(final Context context) {
    }

    /**
     * Send a screen to Google Analytics. Also logs to Crashlytics if applicable.
     *
     * @param screenName the screen to send.
     */
    public static void sendScreen(final Context context, final String screenName) {
        Tracker tracker = getDefaultTracker(context);

        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.AppViewBuilder().build());
        Logger.log(ANALYTICS + screenName);
    }

    /**
     * Send an event to Google Analytics. Also logs to Crashlytics if applicable.
     *
     * @param category the event category.
     * @param action   the event action.
     * @param label    (optional) the event label.
     * @param value    (optional) the event value.
     */
    public static void sendEvent(final Context context, final String category, final String action, final String label, final Long value) {
//        getDefaultTracker(context).send(MapBuilder.createEvent(category, action, label, value).build());

        getDefaultTracker(context).send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());

        Logger.log(ANALYTICS + "Event( " + category + COMMA + action + COMMA + label + COMMA + value + COMMA); //NON-NLS
    }


    private static String getAnalyticsKey(final Context context, final boolean isDebug) {
        if (context.getString(R.string.key_analytics).length() == 0) {
            throw new IllegalArgumentException("Add a string value for key_analytics!");
        }

        String key;
        if (isDebug) {
            key = context.getString(R.string.key_analytics_debug);
        } else {
            key = context.getString(R.string.key_analytics);
        }
        return key;
    }

    private static Tracker getDefaultTracker(final Context context) {
        if(mTracker == null) {
            mTracker = GoogleAnalytics.getInstance(context).newTracker(getAnalyticsKey(context, mIsDebug));
            mTracker.setSampleRate(1.0f);
            if (mTracker == null) {
                throw new IllegalArgumentException("Call Analytics.init(Context, boolean) before using this class!");
            }
        }
        return mTracker;
    }
}
