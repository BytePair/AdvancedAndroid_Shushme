package com.example.android.shushme.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    // COMPLETED (4) Create a GeofenceBroadcastReceiver class that extends BroadcastReceiver and override
    // onReceive() to simply log a message when called. Don't forget to add a receiver tag in the Manifest
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(GeofenceBroadcastReceiver.class.getSimpleName(), "Geo onReceive called");
    }
}
