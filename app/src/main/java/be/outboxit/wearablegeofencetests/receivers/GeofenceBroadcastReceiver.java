package be.outboxit.wearablegeofencetests.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by EXJ508 on 08/01/2018.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Geofence called", Toast.LENGTH_SHORT).show();
        notify(context);
    }

    private void notify(Context context)
    {
        vibrate(context);
    }

    private void vibrate(Context context)
    {
        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator())
        {
            if (Build.VERSION.SDK_INT >= 26) {
                VibrationEffect effect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(1000);
            }
        } else {
            Toast.makeText(context, "Vibrate...", Toast.LENGTH_SHORT).show();
        }
    }
}
