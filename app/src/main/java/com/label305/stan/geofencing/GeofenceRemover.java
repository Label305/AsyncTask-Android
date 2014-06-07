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
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.label305.stan.utils.Dependency;
import com.label305.stan.utils.Logger;

import java.util.List;

/**
 * Class for connecting to Location Services and removing geofences.
 * <p/>
 * <b> Note: Clients must ensure that Google Play services is available before
 * removing geofences. </b> Use
 * GooglePlayServicesUtil.isGooglePlayServicesAvailable() to check.
 * <p/>
 * To use a GeofenceRemover, instantiate it, then call either
 * RemoveGeofencesById() or RemoveGeofencesByIntent(). Everything else is done
 * automatically.
 */
public class GeofenceRemover implements ConnectionCallbacks, OnConnectionFailedListener, OnRemoveGeofencesResultListener {

    // Storage for a context from the calling client
    private Activity mActivity;

    // Stores the current list of geofences
    private List<String> mCurrentGeofenceIds;

    // Stores the current instantiation of the location client
    private LocationClient mLocationClient;

    // The PendingIntent sent in removeGeofencesByIntent
    private PendingIntent mCurrentIntent;

    /*
     * Record the type of removal. This allows continueRemoveGeofences to call
     * the appropriate removal request method.
     */
    private GeofenceUtils.REMOVE_TYPE mRequestType;

    /*
     * Flag that indicates whether an add or remove request is underway. Check
     * this flag before attempting to start a new request.
     */
    private boolean mInProgress;

    /**
     * Construct a GeofenceRemover for the current Context
     *
     * @param context A valid Context
     */
    public GeofenceRemover(Activity context) {
        if (Dependency.isPresent("com.google.android.gms.location.Geofence")) {
            // Save the context
            mActivity = context;

            // Initialize the globals to null
            mCurrentGeofenceIds = null;
            mLocationClient = null;
            mInProgress = false;
        } else {
            throw new RuntimeException("Could not find Geofencing import, make sure the Google Play services (com.google.android.gms:play-services:4.4.+) are imported in the build.gradle file");
        }
    }

    /**
     * Set the "in progress" flag from a caller. This allows callers to re-set a
     * request that failed but was later fixed.
     *
     * @param flag Turn the in progress flag on or off.
     */
    public void setInProgressFlag(boolean flag) {
        // Set the "In Progress" flag.
        mInProgress = flag;
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
     * Remove the geofences in a list of geofence IDs. To remove all current
     * geofences associated with a request, you can also call
     * removeGeofencesByIntent.
     * <p/>
     * <b>Note: The List must contain at least one ID, otherwise an Exception is
     * thrown</b>
     *
     * @param geofenceIds A List of geofence IDs
     */
    public void removeGeofencesById(List<String> geofenceIds) throws IllegalArgumentException, UnsupportedOperationException {
        if ((null == geofenceIds) || (geofenceIds.size() == 0)) {
            throw new IllegalArgumentException();
        } else {
            if (!mInProgress) {
                mRequestType = GeofenceUtils.REMOVE_TYPE.LIST;
                mCurrentGeofenceIds = geofenceIds;
                requestConnection();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Once the connection is available, send a request to remove the Geofences.
     * The method signature used depends on which type of remove request was
     * originally received.
     */
    private void continueRemoveGeofences() {
        switch (mRequestType) {

            // If removeGeofencesByIntent was called
            case INTENT:
                mLocationClient.removeGeofences(mCurrentIntent, this);
                break;

            // If removeGeofencesById was called
            case LIST:
                mLocationClient.removeGeofences(mCurrentGeofenceIds, this);
                break;
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
     * When the request to remove geofences by PendingIntent returns, handle the
     * result.
     *
     * @param statusCode    the code returned by Location Services
     * @param requestIntent The Intent used to request the removal.
     */
    @Override
    public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent requestIntent) {

        // Create a broadcast Intent that notifies other components of success
        // or failure
        Intent broadcastIntent = new Intent();

        // If removing the geofences was successful
        if (statusCode == LocationStatusCodes.SUCCESS) {
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
        } else {
            Logger.log("Error removing goefences: " + statusCode);
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);
        }
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(broadcastIntent);
        requestDisconnection();
    }

    /**
     * When the request to remove geofences by IDs returns, handle the result.
     *
     * @param statusCode         The code returned by Location Services
     * @param geofenceRequestIds The IDs removed
     */
    @Override
    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
        Intent broadcastIntent = new Intent();

        if (LocationStatusCodes.SUCCESS == statusCode) {
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED).addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

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
        /*
         * If the request was done by PendingIntent, cancel the Intent. This
		 * prevents problems if the client gets disconnected before the
		 * disconnection request finishes; the location updates will still be
		 * cancelled.
		 */
        if (mRequestType == GeofenceUtils.REMOVE_TYPE.INTENT) {
            mCurrentIntent.cancel();
        }

    }

    /*
     * Called by Location Services once the location client is connected.
     *
     * Continue by removing the requested geofences.
     */
    @Override
    public void onConnected(Bundle arg0) {
        continueRemoveGeofences();
    }

    /*
     * Called by Location Services if the connection is lost.
     */
    @Override
    public void onDisconnected() {
        mInProgress = false;
        mLocationClient = null;
    }

    /*
     * Implementation of OnConnectionFailedListener.onConnectionFailed If a
     * connection or disconnection request fails, report the error
     * connectionResult is passed in from Location Services
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mInProgress = false;

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {

            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(mActivity, GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }

			/*
			 * If no resolution is available, put the error code in an error
			 * Intent and broadcast it back to the main Activity. The Activity
			 * then displays an error dialog. is out of date.
			 */
        } else {

            Intent errorBroadcastIntent = new Intent(GeofenceUtils.ACTION_CONNECTION_ERROR);
            errorBroadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES).putExtra(GeofenceUtils.EXTRA_CONNECTION_ERROR_CODE,
                    connectionResult.getErrorCode());
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(errorBroadcastIntent);
        }
    }
}
