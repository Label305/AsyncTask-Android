package com.label305.stan.geofencing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.label305.stan.Dependency;

import java.util.List;

/**
 * Class for connecting to Location Services and requesting geofences. <b> Note:
 * Clients must ensure that Google Play services is available before requesting
 * geofences. </b> Use GooglePlayServicesUtil.isGooglePlayServicesAvailable() to
 * check.
 * <p/>
 * <p/>
 * To use a GeofenceRequester, instantiate it and call addGeofence(). Everything
 * else is done automatically.
 */
public class GeofenceRequester implements OnAddGeofencesResultListener, ConnectionCallbacks, OnConnectionFailedListener {

    private final Activity mActivity;

    private final Class<? extends ReceiveTransitionsIntentService> mReceiverClass;

    private PendingIntent mGeofencePendingIntent;

    private List<Geofence> mCurrentGeofences;

    private LocationClient mLocationClient;

    private boolean mInProgress;

    public GeofenceRequester(final Activity activity, final Class<? extends ReceiveTransitionsIntentService> receiverClass) {
        if (Dependency.isPresent(Geofence.class.getName())) {
            mActivity = activity;
            mReceiverClass = receiverClass;

            mGeofencePendingIntent = null;
            mLocationClient = null;
            mInProgress = false;
        } else {
            throw new NoClassDefFoundError(
                    "Could not find Geofencing import, make sure the Google Play services (com.google.android.gms:play-services:4.4.+) are imported in the build.gradle file"
            );
        }
    }

    /**
     * Get the current in progress status.
     *
     * @return The current value of the in progress flag.
     */
    public boolean getInProgressFlag() {
        return mInProgress;
    }

    /**
     * Set the "in progress" flag from a caller. This allows callers to re-set a
     * request that failed but was later fixed.
     *
     * @param flag Turn the in progress flag on or off.
     */
    public void setInProgressFlag(final boolean flag) {
        mInProgress = flag;
    }

    /**
     * Returns the current PendingIntent to the caller.
     *
     * @return The PendingIntent used to create the current set of geofences
     */
    public PendingIntent getRequestPendingIntent() {
        return createRequestPendingIntent();
    }

    /**
     * Start adding geofences. Save the geofences, then start adding them by
     * requesting a connection
     *
     * @param geofences A List of one or more geofences to add
     */
    public void addGeofences(final List<Geofence> geofences) {
        mCurrentGeofences = geofences;
        if (!mInProgress) {
            mInProgress = true;
            requestConnection();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Request a connection to Location Services. This call returns immediately,
     * but the request is not complete until onConnected() or
     * onConnectionFailure() is called.
     */
    private void requestConnection() {
        getLocationClient().connect();
    }

    /**
     * Get the current location client, or create a new one if necessary.
     *
     * @return A LocationClient object
     */
    private GooglePlayServicesClient getLocationClient() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(mActivity, this, this);
        }
        return mLocationClient;
    }

    /**
     * Once the connection is available, send a request to add the Geofences
     */
    private void continueAddGeofences() {
        mGeofencePendingIntent = createRequestPendingIntent();
        mLocationClient.addGeofences(mCurrentGeofences, mGeofencePendingIntent, this);
    }

    @Override
    public void onAddGeofencesResult(final int statusCode, final String[] geofenceRequestIds) {
        Intent broadcastIntent = new Intent();
        if (statusCode == LocationStatusCodes.SUCCESS) {
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_ADDED).addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
        } else {
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR).addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
        }
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(broadcastIntent);
        requestDisconnection();
    }

    /**
     * Get a location client and disconnect from Location Services
     */
    private void requestDisconnection() {
        mInProgress = false;
        getLocationClient().disconnect();
    }

    @Override
    public void onConnected(final Bundle arg0) {
        continueAddGeofences();
    }

    @Override
    public void onDisconnected() {
        mInProgress = false;
        mLocationClient = null;
    }

    /**
     * Get a PendingIntent to send with the request to add Geofences. Location
     * Services issues the Intent inside this PendingIntent whenever a geofence
     * transition occurs for the current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence
     * transitions.
     */
    private PendingIntent createRequestPendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        } else {
            Intent intent = new Intent(mActivity, mReceiverClass);
            return PendingIntent.getService(mActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        mInProgress = false;
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mActivity, GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Intent errorBroadcastIntent = new Intent(GeofenceUtils.ACTION_CONNECTION_ERROR);
            errorBroadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES).putExtra(
                    GeofenceUtils.EXTRA_CONNECTION_ERROR_CODE,
                    connectionResult.getErrorCode()
            );
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(errorBroadcastIntent);
        }
    }
}
