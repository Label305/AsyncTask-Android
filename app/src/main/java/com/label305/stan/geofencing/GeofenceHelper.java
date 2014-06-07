package com.label305.stan.geofencing;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.label305.stan.geofencing.GeofenceUtils.REQUEST_TYPE;
import com.label305.stan.utils.Dependency;
import com.label305.stan.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

/**
 * This is a helper class to work with Geofences. </p> Usage: </br> <li>
 * Implement the {@link ReceiveTransitionsIntentService} class.</li> <li>Create
 * a new instance of this GeofenceHelper class, providing your calling
 * {@link android.app.Activity} and your ReceiveTransitionsIntentService class.</li> <li>
 * Optionally set a {@link GeofenceListener}</li> <li>Call the
 * {@link GeofenceHelper#handleOnResume(android.app.Activity)},
 * {@link GeofenceHelper#handleOnPause(android.app.Activity)} and
 * {@link GeofenceHelper#handleActivityResult(int, int, android.content.Intent)} from the
 * appropriate places.</li> </p></p> NOTE: If a call to add / remove geofences
 * is placed, and the user exits (pauses) the calling Activity, no guarantees
 * about removing or adding geolocations can be made. Block UI manipulation
 * until {@link GeofenceListener#onGeofencesAdded()},
 * {@link GeofenceListener#onGeofencesRemoved()} or
 * {@link GeofenceListener#onGeofenceError(android.content.Intent)} is called.
 */
public class GeofenceHelper {

    /**
     * Create a {@link Geofence} with given id, latitude, longitude and radius.
     * Will use {@link Geofence#GEOFENCE_TRANSITION_ENTER} and
     * {@link Geofence#NEVER_EXPIRE}.</p>Does <i>not</i> register created
     * Geofence.
     *
     * @param radius in meters.
     */
    public static Geofence createEnterGeofence(String id, double latitude, double longitude, float radius) {
        if (Dependency.isPresent("com.google.android.gms.location.Geofence")) {
            return new Geofence.Builder().setRequestId(id).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).setCircularRegion(latitude, longitude, radius).setExpirationDuration(Geofence.NEVER_EXPIRE).build();
        } else {
            throw new NoClassDefFoundError("Could not find Geofencing import, make sure the Google Play services (com.google.android.gms:play-services:4.4.+) are imported in the build.gradle file");
        }
    }

    /**
     * Create a {@link Geofence} with given id, latitude, longitude and radius.
     * Will use {@link Geofence#GEOFENCE_TRANSITION_EXIT} and
     * {@link Geofence#NEVER_EXPIRE}.</p>Does <i>not</i> register created
     * Geofence.
     *
     * @param radius in meters.
     */
    public static Geofence createExitGeofence(String id, double latitude, double longitude, float radius) {
        if (Dependency.isPresent("com.google.android.gms.location.Geofence")) {
            return new Geofence.Builder().setRequestId(id).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT).setCircularRegion(latitude, longitude, radius).setExpirationDuration(Geofence.NEVER_EXPIRE).build();
        } else {
            throw new NoClassDefFoundError("Could not find Geofencing import, make sure the Google Play services (com.google.android.gms:play-services:4.4.+) are imported in the build.gradle file");
        }
    }

    private Activity mActivity;

    private GeofenceListener mGeofenceListener;

    private GeofenceRequester mGeofenceRequester;
    private GeofenceRemover mGeofenceRemover;

    private GeofenceBroadcastReceiver mBroadcastReceiver;
    private IntentFilter mBroadcastIntentFilter;

    private REQUEST_TYPE mRequestType;

    private List<String> mProcessingRemoveGeofenceIds;
    private List<String> mPendingRemoveGeofenceIds;
    private List<Geofence> mProcessingAddGeofences;
    private List<Geofence> mPendingAddGeofences;

    /**
     * Create a new {@link GeofenceHelper} for given Activity and
     * {@link ReceiveTransitionsIntentService} class.
     */
    public GeofenceHelper(Activity activity, Class<? extends ReceiveTransitionsIntentService> receiverClass) {

        if (Dependency.isPresent("com.google.android.gms.location.Geofence")) {
            mActivity = activity;

            mGeofenceRequester = new GeofenceRequester(activity, receiverClass);
            mGeofenceRemover = new GeofenceRemover(activity);

            mBroadcastReceiver = new GeofenceBroadcastReceiver();

            mBroadcastIntentFilter = new IntentFilter();
            mBroadcastIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);
            mBroadcastIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
            mBroadcastIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);
            mBroadcastIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

            mProcessingRemoveGeofenceIds = new ArrayList<String>();
            mPendingRemoveGeofenceIds = new ArrayList<String>();
            mProcessingAddGeofences = new ArrayList<Geofence>();
            mPendingAddGeofences = new ArrayList<Geofence>();
        } else {
            throw new NoClassDefFoundError("Could not find Geofencing import, make sure the Google Play services (com.google.android.gms:play-services:4.4.+) are imported in the build.gradle file");
        }
    }

    /**
     * Set the {@link GeofenceListener} to get notified of add / removal / error
     * updates.
     */
    public void setGeofenceListener(GeofenceListener listener) {
        mGeofenceListener = listener;
    }

    /**
     * Call this method in your Activity's onResume method.
     */
    public void handleOnResume() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiver, mBroadcastIntentFilter);
    }

    /**
     * Call this method in your Activity's onPause method.
     */
    public void handleOnPause() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Call this method in your Activity's onActivityResult method. Returns true
     * when the result has been handled, false otherwise.
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {
                    mGeofenceRequester.setInProgressFlag(false);
                    mGeofenceRequester.addGeofences(mPendingAddGeofences);
                } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType) {
                    mGeofenceRemover.setInProgressFlag(false);
                    mGeofenceRemover.removeGeofencesById(mPendingRemoveGeofenceIds);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    public boolean googlePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        return ConnectionResult.SUCCESS == resultCode;
    }

    /**
     * Register given geofences. If there already is a pending register request,
     * they will be added when this pending request is done.
     */
    public void addGeofences(Geofence... geofences) {
        addGeofences(Arrays.asList(geofences));
    }

    /**
     * Register given geofences. If there already is a pending register request,
     * they will be added when this pending request is done.
     */
    public void addGeofences(List<Geofence> geofences) {
        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
        mPendingAddGeofences.addAll(geofences);

        processPendingAddGeofences();
    }

    private void processPendingAddGeofences() {
        if (mProcessingAddGeofences.isEmpty() && !mPendingAddGeofences.isEmpty()) {
            Logger.log("Adding geofences");
            mProcessingAddGeofences.addAll(mPendingAddGeofences);
            mPendingAddGeofences.clear();
            mGeofenceRequester.addGeofences(mProcessingAddGeofences);
        }
    }

    /**
     * Remove given geofences. If there already is a pending remove request,
     * they will be removed when this pending request is done.
     */
    public void removeGeofences(String... ids) {
        removeGeofences(Arrays.asList(ids));
    }

    /**
     * Remove given geofences. If there already is a pending remove request,
     * they will be removed when this pending request is done.
     */
    public void removeGeofences(List<String> ids) {
        mRequestType = GeofenceUtils.REQUEST_TYPE.REMOVE;
        mPendingRemoveGeofenceIds.addAll(ids);

        processPendingRemoveGeofences();
    }

    private void processPendingRemoveGeofences() {
        if (mProcessingRemoveGeofenceIds.isEmpty() && !mPendingRemoveGeofenceIds.isEmpty()) {
            mProcessingRemoveGeofenceIds.addAll(mPendingRemoveGeofenceIds);
            mPendingRemoveGeofenceIds.clear();
            mGeofenceRemover.removeGeofencesById(mProcessingRemoveGeofenceIds);
        }
    }

    private class GeofenceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {
                handleGeofenceError(context, intent);
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)) {
                handleGeofencesAdded();
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {
                handleGeofencesRemoved();
            } else {
                Logger.log("UNKNOWN: " + action);
            }
        }

        private void handleGeofencesAdded() {
            Logger.log("Successfully added geofences");
            mProcessingAddGeofences.clear();
            processPendingAddGeofences();

            if (mGeofenceListener != null) {
                mGeofenceListener.onGeofencesAdded();
            }
        }

        private void handleGeofencesRemoved() {
            Logger.log("Successfully removed geofences");
            mProcessingRemoveGeofenceIds.clear();
            processPendingRemoveGeofences();

            if (mGeofenceListener != null) {
                mGeofenceListener.onGeofencesRemoved();
            }
        }

        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Logger.log("Geofence error: " + msg);

            if (mGeofenceListener != null) {
                mGeofenceListener.onGeofenceError(intent);
            }
        }
    }

    /**
     * An interface to get notified of adding / removing / error updates.
     */
    public interface GeofenceListener {

        /**
         * Called when all pending adding geofences have been processed.
         */
        public void onGeofencesAdded();

        /**
         * Called when all pending removed geofences have been processed.
         */
        public void onGeofencesRemoved();

        /**
         * Called when an error occured.
         */
        public void onGeofenceError(Intent intent);
    }
}
