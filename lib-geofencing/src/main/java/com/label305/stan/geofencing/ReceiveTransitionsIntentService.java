package com.label305.stan.geofencing;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that
 * triggered the event.
 */
public abstract class ReceiveTransitionsIntentService extends IntentService {

    /**
     * Sets an identifier for this class' background thread
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationClient.hasError(intent)) {
            int errorCode = LocationClient.getErrorCode(intent);
            onError(errorCode);
        } else {
            int transition = LocationClient.getGeofenceTransition(intent);
            if ((transition == Geofence.GEOFENCE_TRANSITION_ENTER) || (transition == Geofence.GEOFENCE_TRANSITION_EXIT)) {
                List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);

                String[] geofenceIds = new String[geofences.size()];
                for (int index = 0; index < geofences.size(); index++) {
                    geofenceIds[index] = geofences.get(index).getRequestId();
                }

                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    onEnteredGeofences(geofenceIds);
                } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    onExitedGeofences(geofenceIds);
                }
            }
        }
    }

    protected abstract void onEnteredGeofences(String[] geofenceIds);

    protected abstract void onExitedGeofences(String[] geofenceIds);

    protected abstract void onError(int errorCode);
}
