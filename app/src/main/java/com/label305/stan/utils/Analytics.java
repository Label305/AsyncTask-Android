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

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
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

    private Analytics() {
    }

    @SuppressWarnings("BooleanParameter")
    /* Suppress the boolean parameter warning to allow easy calling with BuildConfig.DEBUG. */
    /**
     * Initialize the Analytics Tracker. Resolves the key based on isDebug.
     */
    public static void init(final Context context, final boolean isDebug) {
        GoogleAnalytics.getInstance(context).getTracker(getAnalyticsKey(context, isDebug));
        Logger.setIsDebug(isDebug);
    }

    /**
     * Manually start the session.
     */
    public static void startSession(final Context context) {
        getDefaultTracker(context).set(Fields.SESSION_CONTROL, START);
    }

    /**
     * Send a screen to Google Analytics. Also logs to Crashlytics if applicable.
     * @param screenName the screen to send.
     */
    public static void sendScreen(final Context context, final String screenName) {
        Tracker tracker = getDefaultTracker(context);
        tracker.set(Fields.SCREEN_NAME, screenName);
        tracker.send(MapBuilder.createAppView().build());

        Logger.log(ANALYTICS + screenName);
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
        Tracker tracker = GoogleAnalytics.getInstance(context).getDefaultTracker();
        if (tracker == null) {
            throw new IllegalArgumentException("Call Analytics.init(Context, boolean) before using this class!");
        }
        return tracker;
    }
}
