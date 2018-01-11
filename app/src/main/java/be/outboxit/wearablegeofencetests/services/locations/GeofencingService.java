package be.outboxit.wearablegeofencetests.services.locations;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import be.outboxit.wearablegeofencetests.R;
import be.outboxit.wearablegeofencetests.activities.MainActivity;
import be.outboxit.wearablegeofencetests.receivers.GeofenceBroadcastReceiver;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;
import static com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_ENTER;
import static com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_EXIT;

/**
 * Created by EXJ508 on 08/01/2018.
 */

public class GeofencingService extends Service implements OnSuccessListener, OnFailureListener {

    private static final String TAG = "GeofencingService";
    private static final int NOTIFICATION_ID = 1;

    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList;
    private NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofenceList = new ArrayList<>();
        createGeofences();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNofification();
        setupGeofence();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent getGeofencePendingIntent()
    {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(INITIAL_TRIGGER_ENTER | INITIAL_TRIGGER_EXIT);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void setupGeofence()
    {
        try{
            Log.i(TAG, "Setting up geofences...");
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnSuccessListener(this).addOnFailureListener(this);
        } catch (SecurityException ex)
        {
            Log.d(TAG, "Exception: " + ex.getMessage());
        }
    }

    private void createGeofences()
    {
        Log.i(TAG, "Creating geofence...");
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("Test1")
                // Set the circular region of this geofence.
                .setCircularRegion(
                        50.83535,
                        4.33139,
                        100
                )
                .setExpirationDuration(NEVER_EXPIRE)
                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(GEOFENCE_TRANSITION_ENTER | GEOFENCE_TRANSITION_EXIT)
                // Create the geofence.
                .build());
    }

    private void showNofification()
    {
        Intent appIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, appIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, NotificationCompat.CATEGORY_SERVICE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.location_service_title))
                .setContentText(getText(R.string.location_service_text))
                .setLocalOnly(true)
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d(TAG, "Exception: " + e.getMessage());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupGeofence();
            }
        }, 2000);
    }

    @Override
    public void onSuccess(Object o) {
        Log.d(TAG, "Success!");
    }
}