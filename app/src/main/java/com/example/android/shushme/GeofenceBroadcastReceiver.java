package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     * Careful here though, this is running on the main thread so make sure you start an AsyncTask for
     * anything that takes longer than say 10 second to run
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive called");

        // COMPLETED (4) Use GeofencingEvent.fromIntent to retrieve the GeofencingEvent that caused the transition
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // COMPLETED (5) Call getGeofenceTransition to get the transition type and use AudioManager to set the
        // phone ringer mode based on the transition type. Feel free to create a helper method (setRingerMode)
        int geofenceTransitionType = geofencingEvent.getGeofenceTransition();
        if (geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
        }
        else if (geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
        }
        else {
            Log.e(TAG, "Uknown geofence transition type: " + geofenceTransitionType);
            return;
        }

        // COMPLETE (6) Show a notification to alert the user that the ringer mode has changed.
        // Feel free to create a helper method (sendNotification)
        sendNotification(context, geofenceTransitionType);
    }

    private void sendNotification(Context context, int geofenceTransitionType) {

        // create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);

        // construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // get a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Toast.makeText(context, "Ringer set to silent", Toast.LENGTH_LONG).show();
            builder.setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),  R.drawable.ic_volume_off_white_24dp))
                    .setContentTitle("Ringer set to silent");
        }
        else if (geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Toast.makeText(context, "Ringer returned to normal", Toast.LENGTH_LONG).show();
            builder.setSmallIcon(R.drawable.ic_volume_up_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),  R.drawable.ic_volume_up_white_24dp))
                    .setContentTitle("Ringer set to silent");
        }

        // continue building notification
        builder.setContentText("Click to edit geofencing locations");
        builder.setContentIntent(notificationPendingIntent);

        // dismiss notification when clicked
        builder.setAutoCancel(true);

        // get instance of notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // send notification
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

    /**
     * Changes the ringer mode on t he device to either silent or back to normal
     *
     * @param context   The context to access AUDIO_SERVICE
     * @param mode      The desired mode to switch device to:
     *                      AudioManager.RINGER_MODE_SILENT or AudioManger.RINGER_MODE_NORMAL
     */
    private void setRingerMode(Context context, int mode) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // check for do not disturb permissions for api 24+
        if (Build.VERSION.SDK_INT < 24 || (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted())) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }
}
