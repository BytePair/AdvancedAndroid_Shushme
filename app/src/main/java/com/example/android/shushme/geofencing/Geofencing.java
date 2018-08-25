package com.example.android.shushme.geofencing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;

public class Geofencing implements ResultCallback<Status> {

    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // in ms
    private static final int GEOFENCE_RADIUS = 50; // in meters
    private static final String TAG = Geofencing.class.getSimpleName();

    // COMPLETED (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
    // initializes a private member ArrayList of Geofences called mGeofenceList
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mPendingIntent;

    public Geofencing(Context context, GoogleApiClient googleApiClient) {
        this.mContext = context;
        this.mGoogleApiClient = googleApiClient;
        mGeofenceList = new ArrayList<>();
        mPendingIntent = null;
    }

    // COMPLETED (2) Inside Geofencing, implement a public method called updateGeofencesList that
    // given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
    // and add that Geofence to mGeofenceList
    public void updateGeofencesList(PlaceBuffer places) {
        if (places != null && places.getCount() != 0) {
            for (Place place : places) {
                Geofence.Builder builder = new Geofence.Builder();
                builder.setRequestId(place.getId());
                builder.setCircularRegion(place.getLatLng().latitude, place.getLatLng().longitude, GEOFENCE_RADIUS);
                builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
                builder.setExpirationDuration(GEOFENCE_TIMEOUT);
                mGeofenceList.add(builder.build());
            }
        }
    }

    // COMPLETED (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
    // uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    // COMPLETED (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
    // returns a PendingIntent for the GeofenceBroadcastReceiver class
    private PendingIntent getGeofencePendingIntent() {
        if (mPendingIntent != null) {
            return mPendingIntent;
        }
        else {
            Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(mContext,  0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return mPendingIntent;
        }
    }

    // COMPLETED (6) Inside Geofencing, implement a public method called registerAllGeofences that
    // registers the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
    // using the helper functions getGeofencingRequest() and getGeofencePendingIntent()
    public void registerAllGeofences() {
        // ensure google api client is connected and we have geofences available
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() || mGeofenceList == null || mGeofenceList.size() == 0) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);
        } catch (SecurityException e) {
            // app does not have ACCESS_FINE_LOCATION permission
            Log.e(TAG, e.getMessage());
        }
    }

    // COMPLETED (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
    // unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
    // using the helper function getGeofencePendingIntent()
    public void unRegisterAllGeofences() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent()).setResultCallback(this);
        } catch (SecurityException e) {
            // app does not have ACCESS_FINE_LOCATION permission
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        // when there is an error adding/removing
        Log.e(TAG, String.format("Error adding or removing geofence: %s", status.getStatus().toString()));
    }
}
