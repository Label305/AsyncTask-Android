package com.label305.stan.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

/**
 * A logger class which only logs when the app is in debug mode.
 * If the app is not in debug mode, logs to Crashlytics if possible.
 * {@link #setIsDebug(boolean)} must be called before any logging occurs at all.
 */
@SuppressWarnings({"UnusedDeclaration", "StringConcatenation", "CallToPrintStackTrace", "UtilityClass", "UseOfSystemOutOrSystemErr"})
public class Logger {

    private static final String NO_MESSAGE = "No message";

    private static final String TAG = "StanLogger";

    private enum Debug {DEBUG, RELEASE, UNKNOWN}

    private static Debug sDebug = Debug.UNKNOWN;

    private Logger() {
    }

    @SuppressWarnings("BooleanParameter")
    /* Suppress the boolean parameter warning for easy calling using BuildConfig.DEBUG */
    /**
     * Set whether we are in DEBUG mode. This method MUST be called, or logging won't work!
     */
    public static void setIsDebug(final boolean isDebug) {
        sDebug = isDebug ? Debug.DEBUG : Debug.RELEASE;
    }

    /**
     * Logs an int to the logcat if in debug mode, otherwise to Crashlytics if possible.
     */
    public static void log(final int msg) {
        log(String.valueOf(msg));
    }


    /**
     * Logs a String to the logcat if in debug mode, otherwise to Crashlytics if possible.
     */
    public static void log(final String msg) {
        if (isDebug()) {
            Log.v(TAG, msg);
        } else {
            Crashlytics.log(msg);
        }
    }

    /**
     * If in debug mode, logs the String representation of an Object to the logcat, and tries to show a Toast message.
     * Otherwise logs to Crashlytics if possible.
     */
    public static void log(final Context context, final Object msg) {
        String message;

        if (msg == null) {
            message = "null";
        } else {
            message = msg.toString();
        }

        // (Not available in API 8)
        //noinspection SizeReplaceableByIsEmpty
        if (msg.toString().trim().length() == 0) {
            message = NO_MESSAGE;
        }

        log(message);
        toast(context, message);
    }

    /**
     * Prints the stacktrace of an Exception.
     */
    public static void log(final Exception e) {
        log(null, e);
    }

    /**
     * Prints the stacktrace of an Exception, and tries to show a Toast.
     */
    public static void log(final Context context, final Exception e) {
        toast(context, e.getClass().getSimpleName() + ": " + e.getMessage());

        if (isDebug()) {
            e.printStackTrace();
        }
    }

    private static void toast(final Context context, final CharSequence message) {
        if (isDebug()) {
            if (context != null) {
                //noinspection OverlyBroadCatchBlock
                try {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                    /* We don't care if this fails */
                }
            }
        }
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private static boolean isDebug() {
        boolean result;
        switch (sDebug) {
            case DEBUG:
                result = true;
                break;
            case RELEASE:
                result = false;
                break;
            case UNKNOWN:
                System.err.print("Unknown debug state! Not logging.");
                result = false;
                break;
            default:
                result = false;
        }
        return result;
    }
}
